package com.example.temp.post.application;

import static com.example.temp.common.exception.ErrorCode.IMAGE_NOT_FOUND;
import static com.example.temp.common.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.example.temp.common.dto.UserContext;
import com.example.temp.common.exception.ApiException;
import com.example.temp.follow.domain.Follow;
import com.example.temp.follow.domain.FollowRepository;
import com.example.temp.follow.domain.FollowStatus;
import com.example.temp.image.domain.Image;
import com.example.temp.image.domain.ImageRepository;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.post.domain.Post;
import com.example.temp.post.domain.PostImage;
import com.example.temp.post.domain.PostRepository;
import com.example.temp.post.dto.request.PostCreateRequest;
import com.example.temp.post.dto.response.PagePostResponse;
import com.example.temp.post.dto.response.PostCreateResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Transactional
    public PostCreateResponse create(UserContext userContext, PostCreateRequest postCreateRequest,
        LocalDateTime registeredAt) {
        Member member = findMember(userContext);
        List<PostImage> postImages = createPostImages(postCreateRequest);

        Post post = postCreateRequest.toEntity(member, registeredAt, postImages);
        Post savedPost = postRepository.save(post);

        return PostCreateResponse.from(savedPost);
    }

    public PagePostResponse findPostsFromFollowings(UserContext userContext, Pageable pageable) {
        Member member = findMember(userContext);
        List<Member> followings = findFollowingFrom(member);
        Page<Post> posts = postRepository.findByMemberInOrderByCreatedAtDesc(followings, pageable);
        return PagePostResponse.from(posts);
    }

    private Member findMember(UserContext userContext) {
        return memberRepository.findById(userContext.id())
            .orElseThrow(() -> new ApiException(MEMBER_NOT_FOUND));
    }

    private List<PostImage> createPostImages(PostCreateRequest postCreateRequest) {
        return postCreateRequest.imageUrl().stream()
            .map(this::getImageByUrl)
            .map(image -> {
                image.use();
                return PostImage.createPostImage(image);
            })
            .toList();
    }

    private Image getImageByUrl(String imageUrl) {
        if (!imageRepository.existsByUrl(imageUrl)) {
            throw new ApiException(IMAGE_NOT_FOUND);
        }
        return imageRepository.findByUrl(imageUrl);
    }

    private List<Member> findFollowingFrom(Member member) {
        return followRepository.findAllByFromIdAndStatus(
                member.getId(), FollowStatus.APPROVED).stream()
            .map(Follow::getTo)
            .toList();
    }
}
