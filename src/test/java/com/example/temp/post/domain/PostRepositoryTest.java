package com.example.temp.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.example.temp.common.entity.Email;
import com.example.temp.hashtag.domain.Hashtag;
import com.example.temp.hashtag.domain.HashtagRepository;
import com.example.temp.image.domain.Image;
import com.example.temp.image.domain.ImageRepository;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.domain.nickname.Nickname;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
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

    @Autowired
    private HashtagRepository hashtagRepository;

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

        savePost(member1, "내용1", List.of("image1"), new ArrayList<>());
        savePost(member2, "내용2", List.of("image2"), new ArrayList<>());
        savePost(member3, "내용3", List.of("image3"), new ArrayList<>());

        List<Member> followMembers = List.of(member1, member2);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Slice<Post> slicePost = postRepository.findAllByMemberInOrderByRegisteredAtDesc(
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

        savePost(member3, "content", List.of("image1"), new ArrayList<>());

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Post> slicePost = postRepository.findAllByMemberInOrderByRegisteredAtDesc(
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
            savePost(member1, "content" + i, List.of("image" + i), new ArrayList<>());
        }

        // When
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("registeredAt").descending());
        Slice<Post> slicePost = postRepository.findAllByMemberInOrderByRegisteredAtDesc(
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

        savePost(member1, "내용1", List.of("image1"), new ArrayList<>());
        savePost(member2, "내용2", List.of("image2"), new ArrayList<>());

        List<Member> followMembers = List.of(member1, member2);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Slice<Post> slicePost = postRepository.findAllByMemberInOrderByRegisteredAtDesc(
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

    @DisplayName("멤버Id로 해당 멤버가 작성한 게시글을 전부 조회 할 수 있다.")
    @Test
    void findAllByMemberId() {
        //given
        Member member = saveMember("email1@naver.com", "작성자");
        saveImage("이미지1");
        savePost(member, "게시글1", List.of("이미지1"), new ArrayList<>());
        savePost(member, "게시글2", new ArrayList<>(), new ArrayList<>());

        //when
        List<Post> posts = postRepository.findAllByMemberId(member.getId());

        //then
        assertThat(posts).hasSize(2)
            .extracting("content")
            .containsExactly("게시글1", "게시글2");
    }

    @DisplayName("공개계정의 게시글을 해시태그로 검색할 수 있다.")
    @Test
    void findAllPostsByHashTag() {
        //given
        Member member = saveMember("email1@naver.com", "작성자");
        saveImage("이미지1");
        saveImage("이미지2");
        saveImage("이미지3");
        saveHashtag("#해시태그1");
        Hashtag hashtag = saveHashtag("#해시태그2");
        saveHashtag("#해시태그3");
        saveHashtag("#해시태그4");
        savePost(member, "게시글1", List.of("이미지1"), List.of("#해시태그1", "#해시태그2"));
        savePost(member, "게시글2", List.of("이미지2", "이미지3"), List.of("#해시태그1", "#해시태그2", "#해시태그3"));
        savePost(member, "게시글3", List.of("이미지1", "이미지2", "이미지3"), List.of("#해시태그1", "#해시태그4"));

        //when
        Page<Post> posts = postRepository.findAllPostByHashtag(hashtag.getId(), PageRequest.of(0, 5));
        List<Post> postContents = posts.getContent();

        //then
        assertThat(posts.hasNext()).isFalse();
        assertThat(postContents).hasSize(2)
            .extracting("content")
            .containsExactly("게시글2", "게시글1");
    }

    @DisplayName("비공개 계정의 게시글은 검색되지 않는다.")
    @Test
    void notFoundPrivateAccountPost() {
        //given
        Member privateMember = saveMember("private@naver.com", "비공개계정");
        privateMember.changePrivacy(PrivacyPolicy.PRIVATE);
        Hashtag hashtag = saveHashtag("#privateHashtag");
        saveImage("이미지1");
        savePost(privateMember, "비공개계정 게시글", List.of("이미지1"), List.of("#privateHashtag"));

        //when
        Page<Post> posts = postRepository.findAllPostByHashtag(hashtag.getId(), PageRequest.of(0, 5));

        //then
        assertThat(posts.getTotalElements()).isZero();
    }

    @DisplayName("해시태그가 없는 게시글은 검색되지 않는다.")
    @Test
    void notFoundPostWithoutHashtag() {
        //given
        Member member = saveMember("email2@naver.com", "작성자2");
        saveImage("이미지4");
        savePost(member, "게시글4", List.of("이미지4"), List.of());

        //when
        Page<Post> posts = postRepository.findAllPostByHashtag(999L, PageRequest.of(0, 5)); // 존재하지 않는 해시태그 ID

        //then
        assertThat(posts.getTotalElements()).isZero();
    }

    @DisplayName("게시글은 등록일자 내림차순으로 정렬된다.")
    @Test
    void postsAreOrderedByRegisteredAtDesc() {
        //given
        Member member = saveMember("email3@naver.com", "작성자3");
        Hashtag hashtag = saveHashtag("#hashtag5");
        saveImage("이미지5");
        saveImage("이미지6");
        saveImage("이미지7");
        savePost(member, "게시글5", List.of("이미지5"), List.of("#hashtag5"));
        savePost(member, "게시글6", List.of("이미지6"), List.of("#hashtag5"));
        savePost(member, "게시글7", List.of("이미지7"), List.of("#hashtag5"));

        //when
        Page<Post> posts = postRepository.findAllPostByHashtag(hashtag.getId(), PageRequest.of(0, 5));
        List<Post> postContents = posts.getContent();

        //then
        assertThat(postContents).isSortedAccordingTo(Comparator.comparing(Post::getRegisteredAt).reversed());
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

    private void savePost(Member member, String content, List<String> imageUrls, List<String> hashtags) {
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
                image.activate();
                PostImage postImage = PostImage.createPostImage(image);
                postImage.relate(post);

            });

        hashtags.stream()
            .map(name -> hashtagRepository.findByName(name).orElseThrow())
            .forEach(hashtag -> {
                PostHashtag postHashtag = PostHashtag.createPostHashtag(hashtag);
                postHashtag.relatePost(post);
            });
        postRepository.save(post);
    }

    private Image saveImage(String url) {
        Image image = Image.create(url);
        return imageRepository.save(image);
    }

    private Hashtag saveHashtag(String name) {
        Hashtag hashtag = Hashtag.create(name);
        return hashtagRepository.save(hashtag);
    }
}