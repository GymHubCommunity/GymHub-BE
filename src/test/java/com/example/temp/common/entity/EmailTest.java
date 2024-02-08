package com.example.temp.common.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    @DisplayName("email의 값이 일치하면 동등한 객체인지 확인한다.")
    void equals() throws Exception {
        // given
        Email email1 = Email.create("email");
        Email email2 = Email.create("email");

        // when & then
        assertThat(email1.equals(email2)).isTrue();
    }

    @Test
    @DisplayName("email의 값이 일치하면 해시코드가 일치하는지 확인한다.")
    void hashcode() throws Exception {
        // given
        Email email1 = Email.create("email");
        Email email2 = Email.create("email");

        // when
        boolean result = email1.hashCode() == email2.hashCode();

        // then
        assertThat(result).isTrue();
    }

}