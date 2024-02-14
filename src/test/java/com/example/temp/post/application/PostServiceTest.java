package com.example.temp.post.application;

import static com.example.temp.common.exception.ErrorCode.IMAGE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.example.temp.common.dto.UserContext;
import com.example.temp.common.entity.Email;
import com.example.temp.common.exception.ApiException;
import com.example.temp.follow.domain.Follow;
import com.example.temp.follow.domain.FollowRepository;
import com.example.temp.follow.domain.FollowStatus;
import com.example.temp.image.domain.Image;
import com.example.temp.image.domain.ImageRepository;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.domain.nickname.Nickname;
import com.example.temp.post.domain.Content;
import com.example.temp.post.domain.Post;
import com.example.temp.post.domain.PostImage;
import com.example.temp.post.domain.PostRepository;
import com.example.temp.post.dto.request.PostCreateRequest;
import com.example.temp.post.dto.response.PagePostResponse;
import com.example.temp.post.dto.response.PostCreateResponse;
import com.example.temp.post.dto.response.PostElementResponse;
import com.example.temp.post.dto.response.WriterInfo;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FollowRepository followRepository;

    @DisplayName("내가 팔로우한 유저의 게시글 목록을 볼 수 있다.")
    @Test
    void findPostsByFollowedMembers() {
        // Given
        Member member1 = saveMember("email1@test.com", "nick1");
        Member member2 = saveMember("email2@test.com", "nick2");
        Member member3 = saveMember("email3@test.com", "nick3");

        saveFollow(member1, member2);
        saveFollow(member1, member3);

        saveImage("image1");
        saveImage("image2");
        saveImage("image3");

        savePost(member2, "content1", List.of("image1"));
        savePost(member3, "content2", List.of("image2"));
        savePost(member1, "content3", List.of("image3"));

        UserContext userContext = UserContext.from(member1);
        Pageable pageable = PageRequest.of(0, 5);

        // When
        PagePostResponse pagePostResponse = postService.findPostsFromFollowings(userContext, pageable);

        // Then
        assertThat(pagePostResponse.posts()).hasSize(2)
            .extracting("writerInfo")
            .containsExactlyInAnyOrder(WriterInfo.from(member2), WriterInfo.from(member3));
    }

    @DisplayName("팔로우 하지 않은 유저의 게시물은 볼 수 없다.")
    @Test
    void notFindPostsByUnFollowedMembers() {
        // Given
        Member member1 = saveMember("email1@test.com", "nick1");
        Member member2 = saveMember("email2@test.com", "nick2");
        Member member3 = saveMember("email3@test.com", "nick3");
        Member member4 = saveMember("email4@test.com", "nick4");

        saveFollow(member1, member2);
        saveFollow(member1, member3);

        saveImage("image1");
        saveImage("image2");
        saveImage("image3");
        saveImage("image4");

        savePost(member2, "content1", List.of("image1"));
        savePost(member3, "content2", List.of("image2"));
        savePost(member1, "content3", List.of("image3"));
        savePost(member4, "content4", List.of("image4"));

        UserContext userContext = UserContext.from(member1);
        Pageable pageable = PageRequest.of(0, 5);

        // When
        PagePostResponse pagePostResponse = postService.findPostsFromFollowings(userContext, pageable);

        // Then
        assertThat(pagePostResponse.posts()).hasSize(2)
            .extracting(post -> post.writerInfo().writerId())
            .containsExactlyInAnyOrder(member2.getId(), member3.getId())
            .doesNotContain(member4.getId());
    }

    @DisplayName("게시글은 최신글 부터 조회된다.")
    @Test
    void postsOrderByCreatedAt() {
        // Given
        Member member1 = saveMember("email1@test.com", "nick1");
        Member member2 = saveMember("email2@test.com", "nick2");
        Member member3 = saveMember("email3@test.com", "nick3");

        saveFollow(member1, member2);
        saveFollow(member1, member3);

        saveImage("image1");
        saveImage("image2");
        saveImage("image3");
        saveImage("image4");

        savePost(member2, "content1", List.of("image1"));
        savePost(member3, "content2", List.of("image2"));
        savePost(member2, "content3", List.of("image3"));
        savePost(member3, "content4", List.of("image4"));

        UserContext userContext = UserContext.from(member1);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        PagePostResponse postsPage = postService.findPostsFromFollowings(userContext, pageable);
        List<PostElementResponse> posts = postsPage.posts();

        // Then
        assertThat(posts).hasSize(4)
            .extracting("writerInfo", "content")
            .containsExactly(
                tuple(WriterInfo.from(member3), "content4"),
                tuple(WriterInfo.from(member2), "content3"),
                tuple(WriterInfo.from(member3), "content2"),
                tuple(WriterInfo.from(member2), "content1")
            );
    }

    @DisplayName("게시글을 정상적으로 작성할 수 있다.")
    @Test
    void createPost() {
        //given
        Member member = saveMember("email@test.com", "nick");
        UserContext userContext = UserContext.from(member);
        List<String> imageUrls = List.of("imageUrl1", "ImageUrl2");
        List<String> savedImageUrls = saveImagesAndGetUrls(imageUrls);
        PostCreateRequest request = new PostCreateRequest("content1", savedImageUrls);
        LocalDateTime registeredAt = LocalDateTime.now();

        //when
        PostCreateResponse response = postService.createPost(userContext, request, registeredAt);

        //then
        assertThat(response).isNotNull();
        assertThat(response.writerInfo().writerId()).isEqualTo(member.getId());
        assertThat(response.content()).isEqualTo(request.content());
        assertThat(response.postImages()).containsExactlyElementsOf(imageUrls);
    }

    private List<String> saveImagesAndGetUrls(List<String> imageUrls) {
        imageUrls.forEach(this::saveImage);
        return imageUrls;
    }

    @DisplayName("이미지 url로 해당 이미지를 찾지 못하면 예외를 발생시킨다.")
    @Test
    void createPostWithNonexistentImage() {
        // Given
        Member member = saveMember("email@test.com", "nick");
        UserContext userContext = UserContext.from(member);

        List<String> imageUrls = List.of("nonexistent_image");

        PostCreateRequest request = new PostCreateRequest("content1", imageUrls);

        // When & Then
        assertThatThrownBy(() -> postService.createPost(userContext, request, LocalDateTime.now()))
            .isInstanceOf(ApiException.class)
            .hasMessage(IMAGE_NOT_FOUND.getMessage());
    }

    @DisplayName("이미지를 첨부하지 않아도 게시글을 작성할 수 있다.")
    @Test
    void createPostWithoutImage() {
        //given
        Member member = saveMember("email@test.com", "nick");
        UserContext userContext = UserContext.from(member);
        PostCreateRequest postCreateRequest = new PostCreateRequest("content", null);

        //when
        PostCreateResponse postCreateResponse = postService.createPost(userContext, postCreateRequest,
            LocalDateTime.now());

        //then
        assertThat(postCreateResponse).isNotNull()
            .satisfies(response -> {
                assertThat(response.content()).isEqualTo("content");
                assertThat(response.postImages()).isEmpty();
            });
    }

    private Member saveMember(String email, String nickname) {
        Member member = Member.builder()
            .email(Email.create(email))
            .profileUrl("프로필")
            .nickname(Nickname.create(nickname))
            .followStrategy(FollowStrategy.EAGER)
            .privacyPolicy(PrivacyPolicy.PUBLIC)
            .build();
        return memberRepository.save(member);
    }

    private void saveFollow(Member from, Member to) {
        Follow follow = Follow.builder()
            .from(from)
            .to(to)
            .status(FollowStatus.APPROVED)
            .build();
        followRepository.save(follow);
    }

    private void savePost(Member member, String content, List<String> imageUrls) {
        Post post = Post.builder()
            .member(member)
            .content(Content.builder()
                .value(content)
                .build())
            .build();

        imageUrls.stream()
            .map(url -> imageRepository.findByUrl(url))
            .forEach(image -> {
                PostImage postImage = PostImage.createPostImage(image);
                postImage.addPost(post);

            });
        postRepository.save(post);
    }

    private void saveImage(String url) {
        Image image = Image.create(url);
        imageRepository.save(image);
    }
}