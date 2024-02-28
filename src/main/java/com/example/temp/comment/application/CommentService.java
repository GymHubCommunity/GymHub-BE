package com.example.temp.comment.application;

import static com.example.temp.common.exception.ErrorCode.AUTHENTICATED_FAIL;
import static com.example.temp.common.exception.ErrorCode.COMMENT_NOT_FOUND;
import static com.example.temp.common.exception.ErrorCode.POST_NOT_FOUND;
import static com.example.temp.common.exception.ErrorCode.UNAUTHORIZED_COMMENT;

import com.example.temp.comment.domain.Comment;
import com.example.temp.comment.domain.CommentRepository;
import com.example.temp.comment.dto.request.CommentCreateRequest;
import com.example.temp.comment.dto.request.CommentUpdateRequest;
import com.example.temp.comment.dto.response.CommentsResponse;
import com.example.temp.common.dto.UserContext;
import com.example.temp.common.exception.ApiException;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.member.event.MemberDeletedEvent;
import com.example.temp.post.domain.Post;
import com.example.temp.post.domain.PostRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long createComment(Long postId, UserContext userContext, CommentCreateRequest commentCreateRequest,
        LocalDateTime registeredAt) {
        Post post = findPostBy(postId);
        Member member = findMemberBy(userContext.id());

        Comment comment = Comment.create(member, commentCreateRequest.content(), post, registeredAt);
        Comment savedComment = commentRepository.save(comment);
        post.increaseCommentCount();

        return savedComment.getId();
    }

    public CommentsResponse findCommentsByPost(Long postId, UserContext userContext, Pageable pageable) {
        findMemberBy(userContext.id());
        Post post = findPostBy(postId);
        Slice<Comment> comments = commentRepository.findAllByPostId(post.getId(), pageable);
        return CommentsResponse.from(comments);
    }

    @Transactional
    public void updateComment(Long postId, Long commentId, UserContext userContext,
        CommentUpdateRequest commentUpdateRequest) {
        findMemberBy(userContext.id());
        findPostBy(postId);
        Comment comment = findCommentBy(commentId);
        validateOwner(userContext, comment);
        comment.updateContent(commentUpdateRequest.content());
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, UserContext userContext) {
        findMemberBy(userContext.id());
        Post post = findPostBy(postId);
        Comment comment = findCommentBy(commentId);
        validateOwner(userContext, comment);
        commentRepository.delete(comment);
        post.decreaseCommentCount();
    }


    /**
     * 회원이 삭제되었을 때, 해당 회원이 달았던 모든 댓글을 삭제합니다.
     */
    @EventListener
    public void handleMemberDeletedEvent(MemberDeletedEvent event) {
        List<Comment> comments = commentRepository.findAllByMemberId(event.getMemberId());
        commentRepository.deleteAllInBatch(comments);
    }

    private Member findMemberBy(Long userContextId) {
        return memberRepository.findById(userContextId)
            .orElseThrow(() -> new ApiException(AUTHENTICATED_FAIL));
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new ApiException(POST_NOT_FOUND));
    }

    private Comment findCommentBy(Long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new ApiException(COMMENT_NOT_FOUND));
    }

    private void validateOwner(UserContext userContext, Comment comment) {
        if (!comment.isOwner(userContext.id())) {
            throw new ApiException(UNAUTHORIZED_COMMENT);
        }
    }
}
