package com.example.temp.comment.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.common.entity.Email;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.domain.nickname.Nickname;
import com.example.temp.post.domain.Content;
import com.example.temp.post.domain.Post;
import com.example.temp.post.domain.PostRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EntityManager em;

    @DisplayName("게시글에 포함된 댓글 리스트를 가져올 수 있다.")
    @Test
    void findCommentsByPost() {
        //given
        Member member1 = createMember("user1", "user1@gymhub.run");
        Member member2 = createMember("user2", "user2@gymhub.run");
        Post post = createPost(member1, "게시글1");

        Comment comment1 = Comment.create(member2, "댓글1", post, LocalDateTime.now());
        Comment comment2 = Comment.create(member1, "댓글2", post, LocalDateTime.now());
        Comment comment3 = Comment.create(member2, "댓글3", post, LocalDateTime.now());
        commentRepository.saveAll(List.of(comment1, comment2, comment3));

        //when
        Slice<Comment> comments = commentRepository.findAllByPostId(post.getId(), PageRequest.of(0, 10));

        //then
        assertThat(comments.hasNext()).isFalse();
        assertThat(comments).hasSize(3)
            .extracting("post.id", "member.nickname.value", "content")
            .containsExactly(
                Tuple.tuple(post.getId(), "user2", "댓글3"),
                Tuple.tuple(post.getId(), "user1", "댓글2"),
                Tuple.tuple(post.getId(), "user2", "댓글1")
            );
    }

    @Test
    @DisplayName("게시물에 연관된 모든 댓글들을 삭제하는지 확인한다.")
    void deleteAllInBatchByPostsSuccess() throws Exception {
        // given
        Member member = createMember("user1", "user1@gymhub.run");

        Post post1 = createPost(member, "게시글1");
        Post post2 = createPost(member, "게시글2");

        Comment comment1 = Comment.create(member, "댓글1", post1, LocalDateTime.now());
        Comment comment2 = Comment.create(member, "댓글2", post2, LocalDateTime.now());
        Comment comment3 = Comment.create(member, "댓글3", post2, LocalDateTime.now());
        commentRepository.saveAll(List.of(comment1, comment2, comment3));

        // when
        commentRepository.deleteAllInBatchByPosts(List.of(post1, post2));

        // then
        em.flush();
        em.clear();

        assertThat(em.find(Comment.class, comment1.getId())).isNull();
        assertThat(em.find(Comment.class, comment2.getId())).isNull();
        assertThat(em.find(Comment.class, comment3.getId())).isNull();
    }

    @Test
    @DisplayName("deleteAllInBatchByPosts 메서드에서 입력하지 않은 게시물의 댓글은 삭제되지 않는다.")
    void deleteAllInBatchByPostsNotRelated() throws Exception {
        // given
        Member member = createMember("user1", "user1@gymhub.run");

        Post target = createPost(member, "게시글1");
        Post another = createPost(member, "게시글2");

        Comment targetComment1 = Comment.create(member, "댓글1", target, LocalDateTime.now());
        Comment targetComment2 = Comment.create(member, "댓글2", target, LocalDateTime.now());
        Comment anotherComment = Comment.create(member, "댓글3", another, LocalDateTime.now());

        commentRepository.saveAll(List.of(targetComment1, anotherComment, targetComment2));

        // when
        commentRepository.deleteAllInBatchByPosts(List.of(target));

        // then
        em.flush();
        em.clear();

        assertThat(em.find(Comment.class, targetComment1.getId())).isNull();
        assertThat(em.find(Comment.class, targetComment2.getId())).isNull();
        assertThat(em.find(Comment.class, anotherComment.getId())).isNotNull();
    }

    private Post createPost(Member savedMember, String content) {
        Post post = Post.builder()
            .member(savedMember)
            .content(Content.create(content))
            .registeredAt(LocalDateTime.now())
            .build();
        return postRepository.save(post);
    }

    private Member createMember(String nickName, String email) {
        Member member = Member.builder()
            .registered(true)
            .email(Email.create(email))
            .profileUrl("프로필")
            .nickname(Nickname.create(nickName))
            .followStrategy(FollowStrategy.EAGER)
            .privacyPolicy(PrivacyPolicy.PUBLIC)
            .build();
        return memberRepository.save(member);
    }
}