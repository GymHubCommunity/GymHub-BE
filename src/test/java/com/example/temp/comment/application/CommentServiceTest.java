package com.example.temp.comment.application;

import static com.example.temp.common.exception.ErrorCode.AUTHENTICATED_FAIL;
import static com.example.temp.common.exception.ErrorCode.POST_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.auth.domain.Role;
import com.example.temp.comment.domain.Comment;
import com.example.temp.comment.domain.CommentRepository;
import com.example.temp.comment.dto.request.CommentCreateRequest;
import com.example.temp.common.dto.UserContext;
import com.example.temp.common.entity.Email;
import com.example.temp.common.exception.ApiException;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.domain.nickname.Nickname;
import com.example.temp.post.domain.Content;
import com.example.temp.post.domain.Post;
import com.example.temp.post.domain.PostRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CommentRepository commentRepository;

    @DisplayName("게시글에 댓글을 작성할 수 있다.")
    @Test
    void createComment() {
        //given
        Member member = createMember("유저1", "user1@gymhub.run");
        Post post = createPost(member, "게시글1");

        UserContext userContext = UserContext.fromMember(member);
        CommentCreateRequest request = new CommentCreateRequest("댓글 내용");

        //when
        Long commentId = commentService.createComment(post.getId(), userContext, request);

        //then
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        assertThat(comment.getPost()).isEqualTo(post);
        assertThat(comment.getMember()).isEqualTo(member);
        assertThat(comment.getContent()).isEqualTo(request.content());
    }

    @DisplayName("존재하지 않는 게시글에 댓글을 작성하면 예외가 발생한다.")
    @Test
    void createCommentWithInvalidPost() {
        //given
        Member member = createMember("유저1", "user1@gymhub.run");
        UserContext userContext = UserContext.fromMember(member);
        CommentCreateRequest request = new CommentCreateRequest("댓글 내용");

        //when, then
        assertThatThrownBy(() -> commentService.createComment(1L, userContext, request))
            .isInstanceOf(ApiException.class)
            .hasMessage(POST_NOT_FOUND.getMessage());
    }

    @DisplayName("로그인에 문제가 생기면 댓글 작성시 예외가 발생한다.")
    @Test
    void createCommentWithInvalidUserContext() {
        //given
        Member member1 = createMember("user1", "user1@gymhub.run");
        Post post = createPost(member1, "게시글1");
        UserContext userContext = UserContext.builder()
            .id(2L)
            .role(Role.NORMAL)
            .build();
        CommentCreateRequest request = new CommentCreateRequest("댓글 내용");

        //when, then
        assertThatThrownBy(() -> commentService.createComment(post.getId(), userContext, request))
            .isInstanceOf(ApiException.class)
            .hasMessage(AUTHENTICATED_FAIL.getMessage());
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