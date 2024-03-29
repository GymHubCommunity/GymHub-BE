package com.example.temp.comment.application;

import static com.example.temp.common.exception.ErrorCode.AUTHENTICATED_FAIL;
import static com.example.temp.common.exception.ErrorCode.COMMENT_NOT_FOUND;
import static com.example.temp.common.exception.ErrorCode.POST_NOT_FOUND;
import static com.example.temp.common.exception.ErrorCode.UNAUTHORIZED_COMMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.auth.domain.Role;
import com.example.temp.comment.domain.Comment;
import com.example.temp.comment.domain.CommentRepository;
import com.example.temp.comment.dto.request.CommentCreateRequest;
import com.example.temp.comment.dto.request.CommentUpdateRequest;
import com.example.temp.comment.dto.response.CommentsResponse;
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
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
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
        Long commentId = commentService.createComment(post.getId(), userContext, request, LocalDateTime.now());
        Comment comment = commentRepository.findById(commentId).orElseThrow();

        //then
        assertThat(post.getCommentCount()).isEqualTo(1);
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
        assertThatThrownBy(() -> commentService.createComment(1L, userContext, request, LocalDateTime.now()))
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
        assertThatThrownBy(() -> commentService.createComment(post.getId(), userContext, request, LocalDateTime.now()))
            .isInstanceOf(ApiException.class)
            .hasMessage(AUTHENTICATED_FAIL.getMessage());
    }

    @DisplayName("게시글에 포함된 댓글 목록을 가져 올 수 있다.")
    @Test
    void findCommentsByPost() {
        //given
        Member member1 = createMember("유저1", "user1@gymhub.run");
        Member member2 = createMember("유저2", "user2@gymhub.run");
        Post post = createPost(member1, "게시글1");
        UserContext userContext = UserContext.fromMember(member2);
        createComment(member2, "댓글1", post);
        createComment(member2, "댓글2", post);
        createComment(member2, "댓글3", post);

        //when
        CommentsResponse commentResponse = commentService.findCommentsByPost(post.getId(), userContext,
            PageRequest.of(0, 10));

        //then
        assertThat(commentResponse.hasNext()).isFalse();
        assertThat(commentResponse.comments()).hasSize(3)
            .extracting("writerInfo.writerId", "content")
            .containsExactly(
                Tuple.tuple(member2.getId(), "댓글3"),
                Tuple.tuple(member2.getId(), "댓글2"),
                Tuple.tuple(member2.getId(), "댓글1")
            );
    }

    @DisplayName("가져올 댓글이 남아 있으면 hashNext가 true를 반환한다.")
    @Test
    void findCommentsByPostHashNext() {
        //given
        Member member1 = createMember("유저1", "user1@gymhub.run");
        Member member2 = createMember("유저2", "user2@gymhub.run");
        Post post = createPost(member1, "게시글1");
        UserContext userContext = UserContext.fromMember(member2);
        createComment(member2, "댓글1", post);
        createComment(member2, "댓글2", post);

        //when
        CommentsResponse commentResponse = commentService.findCommentsByPost(post.getId(), userContext,
            PageRequest.of(0, 1));

        //then
        assertThat(commentResponse.hasNext()).isTrue();
    }

    @DisplayName("댓글이 없으면 response가 비어 있다.")
    @Test
    void findCommentsByPostNoComment() {
        //given
        Member member1 = createMember("유저1", "user1@gymhub.run");
        Member member2 = createMember("유저2", "user2@gymhub.run");
        Post post = createPost(member1, "게시글1");
        UserContext userContext = UserContext.fromMember(member2);

        //when
        CommentsResponse commentResponse = commentService.findCommentsByPost(post.getId(), userContext,
            PageRequest.of(0, 1));

        //then
        assertThat(commentResponse.comments()).isEmpty();
    }

    @DisplayName("로그인에 문제가 생기면 댓글을 조회할 수 없다.")
    @Test
    void findCommentsByPostWithInvalidUserContext() {
        //given
        Member member1 = createMember("user1", "user1@gymhub.run");
        Post post = createPost(member1, "게시글1");
        UserContext userContext = UserContext.builder()
            .id(999999999L)
            .role(Role.NORMAL)
            .build();
        createComment(member1, "댓글1", post);

        //when, then
        assertThatThrownBy(() -> commentService.findCommentsByPost(
            post.getId(), userContext, PageRequest.of(0, 1)))
            .isInstanceOf(ApiException.class)
            .hasMessage(AUTHENTICATED_FAIL.getMessage());
    }

    @DisplayName("존재하지 않는 게시글의 댓글을 조회할 수 없다.")
    @Test
    void findCommentsByPostWithInvalidPost() {
        //given
        Member member = createMember("유저1", "user1@gymhub.run");
        UserContext userContext = UserContext.fromMember(member);

        //when, then
        assertThatThrownBy(() -> commentService.findCommentsByPost(1L, userContext, PageRequest.of(0, 1)))
            .isInstanceOf(ApiException.class)
            .hasMessage(POST_NOT_FOUND.getMessage());
    }

    @DisplayName("댓글을 수정할 수 있다.")
    @Test
    void updateComment() {
        //given
        Member member1 = createMember("유저1", "user1@gymhub.run");
        Member member2 = createMember("유저2", "user2@gymhub.run");
        Post post = createPost(member1, "게시글1");
        UserContext userContext = UserContext.fromMember(member2);
        Comment comment = createComment(member2, "댓글1", post);
        CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest("수정 후 댓글");

        //when
        commentService.updateComment(post.getId(), comment.getId(), userContext, commentUpdateRequest);

        //then
        assertThat(comment.getContent()).isEqualTo(commentUpdateRequest.content());
    }

    @DisplayName("댓글 수정시 권한이 없으면 삭제할 수 없다.")
    @Test
    void updateCommentNotAuthentication() {
        //given
        Member member1 = createMember("유저1", "user1@gymhub.run");
        Member member2 = createMember("유저2", "user2@gymhub.run");
        Post post = createPost(member1, "게시글1");
        UserContext userContext = UserContext.fromMember(member1);
        Comment comment = createComment(member2, "댓글1", post);
        CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest("수정 후 댓글");

        //when, then
        assertThatThrownBy(
            () -> commentService.updateComment(post.getId(), comment.getId(), userContext, commentUpdateRequest))
            .isInstanceOf(ApiException.class)
            .hasMessage(UNAUTHORIZED_COMMENT.getMessage());
    }

    @DisplayName("존재하지 않는 댓글을 수정할 수 없다.")
    @Test
    void updateCommentNotFound() {
        //given
        Member member1 = createMember("유저1", "user1@gymhub.run");
        Member member2 = createMember("유저2", "user2@gymhub.run");
        Post post = createPost(member1, "게시글1");
        UserContext userContext = UserContext.fromMember(member1);
        createComment(member2, "댓글1", post);
        CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest("수정 후 댓글");

        //when, then
        assertThatThrownBy(() -> commentService.updateComment(post.getId(), 99L, userContext, commentUpdateRequest))
            .isInstanceOf(ApiException.class)
            .hasMessage(COMMENT_NOT_FOUND.getMessage());
    }

    @DisplayName("존재하지 않는 게시글에 댓글을 수정할 수 없다.")
    @Test
    void updateCommentNotFoundPost() {
        //given
        Member member1 = createMember("유저1", "user1@gymhub.run");
        Member member2 = createMember("유저2", "user2@gymhub.run");
        Post post = createPost(member1, "게시글1");
        UserContext userContext = UserContext.fromMember(member1);
        Comment comment = createComment(member2, "댓글1", post);
        CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest("수정 후 댓글");

        //when, then
        assertThatThrownBy(
            () -> commentService.updateComment(123123123L, comment.getId(), userContext, commentUpdateRequest))
            .isInstanceOf(ApiException.class)
            .hasMessage(POST_NOT_FOUND.getMessage());
    }

    @DisplayName("댓글 수정 시 로그인에 문제가 발생하면 삭제할 수 없다.")
    @Test
    void updateCommentWhenLoginError() {
        //given
        Member member1 = createMember("유저1", "user1@gymhub.run");
        Member member2 = createMember("유저2", "user2@gymhub.run");
        Post post = createPost(member1, "게시글1");
        UserContext userContext = UserContext.builder()
            .id(123412341234L)
            .role(Role.NORMAL)
            .build();
        Comment comment = createComment(member2, "댓글1", post);
        CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest("수정 후 댓글");

        //when, then
        assertThatThrownBy(
            () -> commentService.updateComment(post.getId(), comment.getId(), userContext, commentUpdateRequest))
            .isInstanceOf(ApiException.class)
            .hasMessage(AUTHENTICATED_FAIL.getMessage());
    }

    @DisplayName("댓글을 삭제할 수 있다.")
    @Test
    void deleteComment() {
        //given
        Member member1 = createMember("유저1", "user1@gymhub.run");
        Member member2 = createMember("유저2", "user2@gymhub.run");
        Post post = createPost(member1, "게시글1");
        UserContext userContext = UserContext.fromMember(member2);
        Comment comment = createComment(member2, "댓글1", post);

        //when
        commentService.deleteComment(post.getId(), comment.getId(), userContext);

        //then
        assertThat(post.getCommentCount()).isZero();
        assertThatThrownBy(() -> commentRepository.findById(comment.getId())
            .orElseThrow(() -> new ApiException(COMMENT_NOT_FOUND)))
            .isInstanceOf(ApiException.class)
            .hasMessage(COMMENT_NOT_FOUND.getMessage());
    }

    @DisplayName("댓글 삭제시 권한이 없으면 삭제할 수 없다.")
    @Test
    void deleteCommentNotAuthentication() {
        //given
        Member member1 = createMember("유저1", "user1@gymhub.run");
        Member member2 = createMember("유저2", "user2@gymhub.run");
        Post post = createPost(member1, "게시글1");
        UserContext userContext = UserContext.fromMember(member1);
        Comment comment = createComment(member2, "댓글1", post);

        //when, then
        assertThatThrownBy(() -> commentService.deleteComment(post.getId(), comment.getId(), userContext))
            .isInstanceOf(ApiException.class)
            .hasMessage(UNAUTHORIZED_COMMENT.getMessage());
    }

    @DisplayName("존재하지 않는 댓글을 삭제할 수 없다.")
    @Test
    void deleteCommentNotFound() {
        //given
        Member member1 = createMember("유저1", "user1@gymhub.run");
        Member member2 = createMember("유저2", "user2@gymhub.run");
        Post post = createPost(member1, "게시글1");
        UserContext userContext = UserContext.fromMember(member1);
        createComment(member2, "댓글1", post);

        //when, then
        assertThatThrownBy(() -> commentService.deleteComment(post.getId(), 99L, userContext))
            .isInstanceOf(ApiException.class)
            .hasMessage(COMMENT_NOT_FOUND.getMessage());
    }

    @DisplayName("댓글 삭제 시 로그인에 문제가 발생하면 삭제할 수 없다.")
    @Test
    void deleteCommentWhenLoginError() {
        //given
        Member member1 = createMember("유저1", "user1@gymhub.run");
        Member member2 = createMember("유저2", "user2@gymhub.run");
        Post post = createPost(member1, "게시글1");
        UserContext userContext = UserContext.builder()
            .id(123412341234L)
            .role(Role.NORMAL)
            .build();
        Comment comment = createComment(member2, "댓글1", post);

        //when, then
        assertThatThrownBy(() -> commentService.deleteComment(post.getId(), comment.getId(), userContext))
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

    private Comment createComment(Member member, String content, Post post) {
        return commentRepository.save(
            Comment.create(member, content, post, LocalDateTime.now())
        );
    }
}