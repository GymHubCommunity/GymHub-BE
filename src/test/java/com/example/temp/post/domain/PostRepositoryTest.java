package com.example.temp.post.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.common.entity.Email;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.infrastructure.nickname.Nickname;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    int globalIdx = 0;

    @DisplayName("팔로우 리스트에 들어 있는 사용자의 게시글만 조회할 수 있다.")
    @Test
    void findByMemberIn() {
        // Given
        Member member1 = saveMember();
        Member member2 = saveMember();
        Member member3 = saveMember();

        Post post1 = createPost(member1, "내용1", "이미지1");
        Post post2 = createPost(member2, "내용2", "이미지2");
        Post post3 = createPost(member3, "내용3", "이미지3");

        postRepository.saveAll(List.of(post1, post2, post3));

        List<Member> followMembers = List.of(member1, member2);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Post> postsPage = postRepository.findByMemberIn(followMembers, pageable);
        List<Post> posts = postsPage.getContent();

        // Then
        assertThat(posts).hasSize(2);
        assertThat(posts).extracting("member")
            .containsExactlyInAnyOrder(member1, member2);
    }

    @DisplayName("팔로우 리스트에 없는 사용자 게시글은 조회할 수 없다.")
    @Test
    void findByMemberInTest_withNotIncludedMember() {
        // Given
        Member member1 = saveMember();
        Member member2 = saveMember();
        Member member3 = saveMember();

        Post post = createPost(member3, "content", "image");
        postRepository.save(post);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postsPage = postRepository.findByMemberIn(List.of(member1, member2), pageable);

        // Then
        assertThat(postsPage.getContent()).isEmpty();
    }

    @DisplayName("게시글을 최근 작성게시글 부터 한 페이지에 5개씩 가져 올 수 있다.")
    @Test
    void findByMemberInWithPagination() {
        // Given
        Member member1 = saveMember();
        Member member2 = saveMember();

        for (int i = 0; i < 20; i++) {
            Post post = createPost(member1, "content" + i, "image" + i);
            postRepository.save(post);
        }

        // When
        Pageable pageable = PageRequest.of(1, 5, Sort.by("createdAt").descending());
        Page<Post> postsPage = postRepository.findByMemberIn(List.of(member1, member2), pageable);

        // Then
        assertThat(postsPage.getNumber()).isEqualTo(1);
        assertThat(postsPage.getSize()).isEqualTo(5);
        assertThat(postsPage.getContent()).hasSize(5);
    }

    private Post createPost(Member member, String content, String url) {
        return Post.builder()
            .member(member)
            .content(Content.builder()
                .value(content)
                .build())
            .imageUrl(url)
            .build();
    }

    private Member saveMember() {
        Member member = Member.builder()
            .email(Email.create("이메일"))
            .profileUrl("프로필")
            .nickname(Nickname.create("nick" + (globalIdx++)))
            .followStrategy(FollowStrategy.EAGER)
            .privacyPolicy(PrivacyPolicy.PUBLIC)
            .build();
        memberRepository.save(member);
        return member;
    }
}