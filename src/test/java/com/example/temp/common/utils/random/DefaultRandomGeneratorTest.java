package com.example.temp.common.utils.random;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultRandomGeneratorTest {

    DefaultRandomGenerator randomGenerator;

    @BeforeEach
    void setUp() {
        randomGenerator = new DefaultRandomGenerator();
    }

    @Test
    @DisplayName("랜덤한 닉네임을 생성한다")
    void generate() throws Exception {
        Set<String> values = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String value = randomGenerator.generate();
            assertThat(value).isNotNull();
            values.add(value);
        }
        assertThat(values.size()).isBetween(2, 1000);
    }

    @Test
    @DisplayName("랜덤한 닉네임을 사이즈와 함께 생성한다")
    void generateWithSize() throws Exception {
        int size = 5;

        Set<String> values = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String value = randomGenerator.generate(size);
            assertThat(value).hasSize(size);
            values.add(value);
        }
        assertThat(values.size()).isBetween(2, 1000);
    }

    @Test
    @DisplayName("시드를 지정하면 만들어지는 문자열은 항상 동일하다.")
    void generateWithSeed() throws Exception {
        String seed = "1";
        int size = 5;

        Set<String> values = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String value = randomGenerator.generateWithSeed(seed, size);
            assertThat(value).hasSize(size);
            values.add(value);
        }
        assertThat(values).hasSize(1);
    }

}