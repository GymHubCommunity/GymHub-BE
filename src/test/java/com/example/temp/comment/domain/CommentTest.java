package com.example.temp.comment.domain;

import static com.example.temp.common.exception.ErrorCode.COMMENT_TOO_LONG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.common.entity.Email;
import com.example.temp.common.exception.ApiException;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.domain.nickname.Nickname;
import com.example.temp.post.domain.Content;
import com.example.temp.post.domain.Post;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentTest {

    @DisplayName("댓글은 최대 500자 까지 작성 가능하다.")
    @Test
    void createCommentLimitContentLength() {
        //given
        Member member = createMember("user1", "user1@gymhub.run");
        Post post = createPost(member, "게시글1");
        String content = String.join("", Collections.nCopies(100, "테스트댓글"));

        //when
        Comment comment = Comment.create(member, content, post);

        //then
        assertThat(comment.getContent()).isEqualTo(content);
    }

    @DisplayName("댓글은 500자가 초과하면 예외가 발생한다.")
    @Test
    void validateContentLength() {
        //given
        Member member = createMember("user1", "user1@gymhub.run");
        Post post = createPost(member, "게시글1");
        String content = String.join("", Collections.nCopies(501, "a"));

        //when, then
        assertThatThrownBy(() -> Comment.create(member, content, post))
            .isInstanceOf(ApiException.class)
            .hasMessage(COMMENT_TOO_LONG.getMessage());

    }

    private Post createPost(Member savedMember, String content) {
        return Post.builder()
            .member(savedMember)
            .content(Content.create(content))
            .registeredAt(LocalDateTime.now())
            .build();
    }

    private Member createMember(String nickName, String email) {
        return Member.builder()
            .registered(true)
            .email(Email.create(email))
            .profileUrl("프로필")
            .nickname(Nickname.create(nickName))
            .followStrategy(FollowStrategy.EAGER)
            .privacyPolicy(PrivacyPolicy.PUBLIC)
            .build();
    }
}