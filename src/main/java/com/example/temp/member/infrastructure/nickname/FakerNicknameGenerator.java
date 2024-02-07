package com.example.temp.member.infrastructure.nickname;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

@Component
public class FakerNicknameGenerator implements NicknameGenerator {

    Faker faker = new Faker();

    @Override
    public Nickname generate() {
        String name = faker.number().digits(10);
        return Nickname.create(name);
    }
}
