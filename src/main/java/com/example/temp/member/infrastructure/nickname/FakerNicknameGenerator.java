package com.example.temp.member.infrastructure.nickname;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

@Component
public class FakerNicknameGenerator implements NicknameGenerator {

    Faker faker = new Faker();

    @Override
    public String generate() {
        String job = faker.job().position();
        String color = faker.color().name();
        String champ = faker.leagueOfLegends().champion();
        return String.format("%s's %s %s", job, color, champ);
    }
}
