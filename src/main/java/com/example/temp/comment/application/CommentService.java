package com.example.temp.comment.application;

import static com.example.temp.common.exception.ErrorCode.AUTHENTICATED_FAIL;
import static com.example.temp.common.exception.ErrorCode.POST_NOT_FOUND;

import com.example.temp.comment.domain.Comment;
import com.example.temp.comment.domain.CommentRepository;
import com.example.temp.comment.dto.request.CommentCreateRequest;
import com.example.temp.common.dto.UserContext;
import com.example.temp.common.exception.ApiException;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.post.domain.Post;
import com.example.temp.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
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
    public Long createComment(Long postId, UserContext userContext, CommentCreateRequest commentCreateRequest) {
        Post post = findPostBy(postId);
        Member member = findMemberBy(userContext);

        Comment comment = Comment.create(member, commentCreateRequest.content(), post);
        Comment savedComment = commentRepository.save(comment);

        return savedComment.getId();
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new ApiException(POST_NOT_FOUND));
    }

    private Member findMemberBy(UserContext userContext) {
        return memberRepository.findById(userContext.id())
            .orElseThrow(() -> new ApiException(AUTHENTICATED_FAIL));
    }
}
