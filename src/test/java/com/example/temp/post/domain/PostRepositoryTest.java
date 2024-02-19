package com.example.temp.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.example.temp.common.entity.Email;
import com.example.temp.image.domain.Image;
import com.example.temp.image.domain.ImageRepository;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.domain.nickname.Nickname;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ImageRepository imageRepository;

    @DisplayName("팔로우 리스트에 들어 있는 사용자의 게시글만 조회할 수 있다.")
    @Test
    void findByMemberIn() {
        // Given
        Member member1 = saveMember("email1@test.com", "nick1");
        Member member2 = saveMember("email2@test.com", "nick2");
        Member member3 = saveMember("email3@test.com", "nick3");

        saveImage("image1");
        saveImage("image2");
        saveImage("image3");

        savePost(member1, "내용1", List.of("image1"));
        savePost(member2, "내용2", List.of("image2"));
        savePost(member3, "내용3", List.of("image3"));

        List<Member> followMembers = List.of(member1, member2);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Slice<Post> slicePost = postRepository.findByMemberInOrderByRegisteredAtDesc(
            followMembers, pageable);
        List<Post> posts = slicePost.getContent();

        // Then
        assertThat(posts).hasSize(2)
            .extracting("member")
            .containsExactlyInAnyOrder(member1, member2);
    }

    @DisplayName("팔로우 리스트에 없는 사용자 게시글은 조회할 수 없다.")
    @Test
    void notFindByMemberNotIn() {
        // Given
        Member member1 = saveMember("email1@test.com", "nick1");
        Member member2 = saveMember("email2@test.com", "nick2");
        Member member3 = saveMember("email3@test.com", "nick3");

        saveImage("image1");

        savePost(member3, "content", List.of("image1"));

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Post> slicePost = postRepository.findByMemberInOrderByRegisteredAtDesc(
            List.of(member1, member2), pageable);

        // Then
        assertThat(slicePost.getContent()).isEmpty();
    }

    @DisplayName("게시글을 최근 작성게시글 부터 한 페이지에 5개씩 가져 올 수 있다.")
    @Test
    void findByMemberInWithPagination() {
        // Given
        final int pageNumber = 1;
        final int pageSize = 5;
        Member member1 = saveMember("email1@test.com", "nick1");
        Member member2 = saveMember("email2@test.com", "nick2");

        for (int i = 0; i < 20; i++) {
            saveImage("image" + i);
            savePost(member1, "content" + i, List.of("image" + i));
        }

        // When
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("registeredAt").descending());
        Slice<Post> slicePost = postRepository.findByMemberInOrderByRegisteredAtDesc(
            List.of(member1, member2), pageable);

        // Then
        assertThat(slicePost.getNumber()).isEqualTo(pageNumber);
        assertThat(slicePost.getSize()).isEqualTo(pageSize);
        assertThat(slicePost.getContent()).hasSize(pageSize);
    }

    @DisplayName("게시글은 최신순으로 조회 된다.")
    @Test
    void findByMemberInOrderByCreatedAtDesc() {
        // Given
        Member member1 = saveMember("email1@test.com", "nick1");
        Member member2 = saveMember("email2@test.com", "nick2");

        saveImage("image1");
        saveImage("image2");

        savePost(member1, "내용1", List.of("image1"));
        savePost(member2, "내용2", List.of("image2"));

        List<Member> followMembers = List.of(member1, member2);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Slice<Post> slicePost = postRepository.findByMemberInOrderByRegisteredAtDesc(
            followMembers, pageable);
        List<Post> posts = slicePost.getContent();

        // Then
        assertThat(posts).hasSize(2)
            .extracting("content", "member")
            .containsExactly(
                tuple("내용2", member2),
                tuple("내용1", member1)
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

    private void savePost(Member member, String content, List<String> imageUrls) {
        Post post = Post.builder()
            .member(member)
            .content(Content.builder()
                .value(content)
                .build())
            .registeredAt(LocalDateTime.now())
            .build();

        imageUrls.stream()
            .map(url -> imageRepository.findByUrl(url))
            .forEach(image -> {
                image.use();
                PostImage postImage = PostImage.createPostImage(image);
                postImage.relate(post);

            });
        postRepository.save(post);
    }

    private void saveImage(String url) {
        Image image = Image.create(url);
        imageRepository.save(image);
    }
}