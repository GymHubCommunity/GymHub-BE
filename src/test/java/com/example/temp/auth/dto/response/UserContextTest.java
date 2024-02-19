package com.example.temp.auth.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.auth.domain.Role;
import com.example.temp.common.dto.UserContext;
import com.example.temp.common.entity.Email;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.domain.nickname.Nickname;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

class UserContextTest {

    @Test
    @DisplayName("member를 사용해 Normal 권한을 가진 UserContext를 생성한다.")
    void fromMemberTest() throws Exception {
        // given
        Member member = Member.builder().build();

        // when
        UserContext userContext = UserContext.fromMember(member);

        // then
        assertThat(userContext.role()).isEqualTo(Role.NORMAL);
    }

    @Test
    @DisplayName("UserContext가 Normal 권한인지 확인한다.")
    void testIsNormal() throws Exception {
        // given
        UserContext userContext = new UserContext(1L, Role.NORMAL);

        // when & then
        assertThat(userContext.isNormal()).isTrue();
        assertThat(userContext.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("UserContext가 Admin 권한인지 확인한다.")
    void testIsAdmin() throws Exception {
        // given
        UserContext userContext = new UserContext(1L, Role.ADMIN);

        // when & then
        assertThat(userContext.isAdmin()).isTrue();
        assertThat(userContext.isNormal()).isFalse();
    }
}