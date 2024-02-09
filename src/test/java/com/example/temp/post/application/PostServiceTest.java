package com.example.temp.post.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.common.dto.UserContext;
import com.example.temp.common.entity.Email;
import com.example.temp.follow.domain.Follow;
import com.example.temp.follow.domain.FollowRepository;
import com.example.temp.follow.domain.FollowStatus;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.infrastructure.nickname.Nickname;
import com.example.temp.post.domain.Content;
import com.example.temp.post.domain.Post;
import com.example.temp.post.domain.PostRepository;
import com.example.temp.post.dto.response.PagePostResponse;
import com.example.temp.post.dto.response.PostElementResponse;
import com.example.temp.post.dto.response.WriterInfo;
import java.util.List;
import org.assertj.core.groups.Tuple;
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

        savePost(member2, "content1", "image1");
        savePost(member3, "content2", "image2");
        savePost(member1, "content3", "image3");

        UserContext userContext = UserContext.from(member1);
        Pageable pageable = PageRequest.of(0, 5);

        // When
        PagePostResponse pagePostResponse = postService.findPostsByFollowedMembers(userContext, pageable);

        // Then
        assertThat(pagePostResponse.posts()).hasSize(2);
        assertThat(pagePostResponse.posts())
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

        savePost(member2, "content1", "image1");
        savePost(member3, "content2", "image2");
        savePost(member1, "content3", "image3");
        savePost(member4, "content4", "image4");

        UserContext userContext = UserContext.from(member1);
        Pageable pageable = PageRequest.of(0, 5);

        // When
        PagePostResponse pagePostResponse = postService.findPostsByFollowedMembers(userContext, pageable);

        // Then
        assertThat(pagePostResponse.posts()).hasSize(2);
        assertThat(pagePostResponse.posts())
            .extracting(post -> post.writerInfo().id())
            .containsExactlyInAnyOrder(member2.getId(), member3.getId());
        assertThat(pagePostResponse.posts())
            .extracting(post -> post.writerInfo().id())
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

        Post post1 = savePost(member2, "content1", "image1");
        Post post2 = savePost(member3, "content2", "image2");
        Post post3 = savePost(member2, "content3", "image3");
        Post post4 = savePost(member3, "content4", "image4");

        UserContext userContext = UserContext.from(member1);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        PagePostResponse postsPage = postService.findPostsByFollowedMembers(userContext, pageable);
        List<PostElementResponse> posts = postsPage.posts();

        // Then
        assertThat(posts).hasSize(4)
            .extracting("writerInfo", "content")
            .containsExactly(
                Tuple.tuple(WriterInfo.from(member3), "content4"),
                Tuple.tuple(WriterInfo.from(member2), "content3"),
                Tuple.tuple(WriterInfo.from(member3), "content2"),
                Tuple.tuple(WriterInfo.from(member2), "content1")
            );
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

    private Post savePost(Member member, String content, String image) {
        Post post = Post.builder()
            .member(member)
            .content(Content.builder()
                .value(content)
                .build())
            .imageUrl(image)
            .build();
        return postRepository.save(post);
    }

    private void saveFollow(Member from, Member to) {
        Follow follow = Follow.builder()
            .from(from)
            .to(to)
            .status(FollowStatus.APPROVED)
            .build();
        followRepository.save(follow);
    }
}