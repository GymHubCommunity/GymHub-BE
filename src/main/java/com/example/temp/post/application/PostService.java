package com.example.temp.post.application;

import static com.example.temp.common.exception.ErrorCode.*;
import static com.example.temp.common.exception.ErrorCode.AUTHENTICATED_FAIL;
import static com.example.temp.common.exception.ErrorCode.IMAGE_NOT_FOUND;
import static com.example.temp.common.exception.ErrorCode.POST_NOT_FOUND;
import static com.example.temp.common.exception.ErrorCode.UNAUTHORIZED_POST;

import com.example.temp.common.dto.UserContext;
import com.example.temp.common.exception.ApiException;
import com.example.temp.follow.domain.Follow;
import com.example.temp.follow.domain.FollowRepository;
import com.example.temp.follow.domain.FollowStatus;
import com.example.temp.hashtag.application.HashtagService;
import com.example.temp.hashtag.domain.Hashtag;
import com.example.temp.hashtag.domain.HashtagRepository;
import com.example.temp.image.domain.Image;
import com.example.temp.image.domain.ImageRepository;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.member.event.MemberDeletedEvent;
import com.example.temp.post.domain.Post;
import com.example.temp.post.domain.PostHashtag;
import com.example.temp.post.domain.PostHashtagRepository;
import com.example.temp.post.domain.PostImage;
import com.example.temp.post.domain.PostImageRepository;
import com.example.temp.post.domain.PostRepository;
import com.example.temp.post.dto.request.PostCreateRequest;
import com.example.temp.post.dto.request.PostUpdateRequest;
import com.example.temp.post.dto.response.PostDetailResponse;
import com.example.temp.post.dto.response.PostResponse;
import com.example.temp.post.dto.response.PostSearchResponse;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final ImageRepository imageRepository;
    private final PostImageRepository postImageRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final HashtagRepository hashtagRepository;
    private final HashtagService hashtagService;

    @Transactional
    public Long createPost(UserContext userContext, PostCreateRequest postCreateRequest,
        LocalDateTime registeredAt) {
        Member member = findMember(userContext);
        Post post = postCreateRequest.toEntity(member, registeredAt);

        createPostImages(postCreateRequest.imageUrls(), post);
        createPostHashtags(postCreateRequest.hashTags(), post);

        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    public PostResponse findMyAndFollowingPosts(UserContext userContext, Pageable pageable) {
        Member member = findMember(userContext);
        List<Member> myselfAndFollowings = findMyselfAndFollowings(member);
        myselfAndFollowings.add(member);
        Slice<Post> posts = postRepository.findAllByMemberInOrderByRegisteredAtDesc(myselfAndFollowings, pageable);
        return PostResponse.from(posts);
    }

    public PostDetailResponse findPost(Long postId, UserContext userContext) {
        findMember(userContext);
        Post findPost = findPostBy(postId);
        return PostDetailResponse.from(findPost);
    }

    @Transactional
    public void updatePost(Long postId, UserContext userContext, PostUpdateRequest request) {
        Post post = findPostBy(postId);
        validateOwner(userContext, post);

        post.updateContent(request.content());
        updatePostImages(request, post);
        updatePostHashtags(request, post);
    }

    @Transactional
    public void deletePost(Long postId, UserContext userContext) {
        Post post = findPostBy(postId);
        validateOwner(userContext, post);

        disableImage(post);
        postRepository.delete(post);
    }

    public PostSearchResponse findPostsByHashtag(String hashtag, UserContext userContext, Pageable pageable) {
        findMember(userContext);
        Hashtag findHashtag = findHashtag(hashtag);
        Page<Post> posts = postRepository.findAllPostByHashtag(findHashtag.getId(), pageable);
        return PostSearchResponse.from(posts);
    }

    private void updatePostImages(PostUpdateRequest request, Post post) {
        disableImage(post);
        post.getPostImages().clear();
        createPostImages(request.imageUrls(), post);
    }

    private void updatePostHashtags(PostUpdateRequest request, Post post) {
        post.getPostHashtags().clear();
        createPostHashtags(request.hashTags(), post);
    }

    private Member findMember(UserContext userContext) {
        return memberRepository.findById(userContext.id())
            .orElseThrow(() -> new ApiException(AUTHENTICATED_FAIL));
    }

    private void createPostImages(List<String> imageUrl, Post post) {
        List<String> url = imageUrl != null ? imageUrl : Collections.emptyList();
        url.stream()
            .map(this::getImageByUrl)
            .map(this::createPostImage)
            .forEach(postImage -> addPostImageToPost(postImage, post));
    }

    private Image getImageByUrl(String imageUrl) {
        if (!imageRepository.existsByUrl(imageUrl)) {
            throw new ApiException(IMAGE_NOT_FOUND);
        }
        return imageRepository.findByUrl(imageUrl);
    }

    private PostImage createPostImage(Image image) {
        return PostImage.createPostImage(image);
    }

    private void addPostImageToPost(PostImage postImage, Post post) {
        postImage.relate(post);
    }

    private void createPostHashtags(List<String> hashtags, Post post) {
        List<Hashtag> savedHashtags = hashtagService.saveHashtag(hashtags);
        savedHashtags.stream()
            .map(PostHashtag::createPostHashtag)
            .forEach(postHashtag -> postHashtag.relatePost(post));
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new ApiException(POST_NOT_FOUND));
    }

    private List<Member> findMyselfAndFollowings(Member member) {
        List<Member> followings = followRepository.findAllByFromIdAndStatus(
                member.getId(), FollowStatus.APPROVED).stream()
            .map(Follow::getTo)
            .collect(Collectors.toList());
        followings.add(member);

        return followings;
    }

    private void disableImage(Post post) {
        List<Image> images = imageRepository.findByUrlIn(post.getPostImages().stream()
            .map(PostImage::getImageUrl)
            .toList());
        images.forEach(Image::deactivate);
    }

    private Hashtag findHashtag(String hashtag) {
        return hashtagRepository.findByName("#" + hashtag)
            .orElseThrow(() -> new ApiException(HASHTAG_NOT_FOUND));
    }

    private void validateOwner(UserContext userContext, Post post) {
        if (!post.isOwner(userContext.id())) {
            throw new ApiException(UNAUTHORIZED_POST);
        }
    }

    /**
     * 회원이 삭제되었을 때, 해당 회원이 작성한 게시글을 삭제합니다.
     */
    @Transactional
    @EventListener
    public void handleMemberDeletedEvent(MemberDeletedEvent event) {
        List<Post> posts = postRepository.findAllByMemberId(event.getMemberId());
        postHashtagRepository.deleteAllInBatchByPostIn(posts);
        postImageRepository.deleteAllInBatchByPostIn(posts);
        postRepository.deleteAllInBatch(posts);
    }
}
