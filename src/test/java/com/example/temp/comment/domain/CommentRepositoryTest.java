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