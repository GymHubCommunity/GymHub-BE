package com.example.temp.common.infrastructure.exceptionsender;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.common.dto.UserContext;
import com.example.temp.common.exception.ExceptionInfo;
import com.example.temp.common.infrastructure.exceptionsender.DiscordExceptionSender.DiscordBodyValue;
import com.example.temp.common.infrastructure.exceptionsender.DiscordExceptionSender.DiscordBodyValue.Embed;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DiscordDiscordBodyValueTest {

    @Test
    @DisplayName("사용자의 정보와 함께 DiscordBodyValue를 생성한다.")
    void create() throws Exception {
        // given
        ExceptionInfo exceptionInfo = createExceptionInfo("clazz", "message", "/hello", "POST",
            UserContext.builder().id(1L).build());

        // when
        DiscordBodyValue discordBodyValue = new DiscordBodyValue(exceptionInfo);

        // then
        List<Embed> embeds = discordBodyValue.getEmbeds();
        assertThat(embeds).hasSize(1);
        Embed embed = embeds.get(0);
        assertThat(embed.getTitle()).isEqualTo(exceptionInfo.getClazz());
        assertThat(embed.getDescription()).isEqualTo(createEmbedDescriptionIfLogin(exceptionInfo));
    }

    @Test
    @DisplayName("사용자가 로그인하지 않았어도 DiscordBodyValue를 생성한다.")
    void createIfNotLogin() throws Exception {
        // given
        ExceptionInfo exceptionInfo = createExceptionInfo("clazz", "message", "/hello", "POST", null);

        // when
        DiscordBodyValue discordBodyValue = new DiscordBodyValue(exceptionInfo);

        // then
        List<Embed> embeds = discordBodyValue.getEmbeds();
        assertThat(embeds).hasSize(1);
        Embed embed = embeds.get(0);
        assertThat(embed.getTitle()).isEqualTo(exceptionInfo.getClazz());
        assertThat(embed.getDescription()).isEqualTo(createEmbedDescriptionIfNotLogin(exceptionInfo));
    }

    private String createEmbedDescriptionIfLogin(ExceptionInfo exceptionInfo) {
        UserContext userContext = exceptionInfo.getUserContextOpt().get();
        return createMessageFormat(exceptionInfo, userContext.toString());
    }

    private String createEmbedDescriptionIfNotLogin(ExceptionInfo exceptionInfo) {
        return createMessageFormat(exceptionInfo, "로그인하지 않은 사용자");
    }

    private String createMessageFormat(ExceptionInfo exceptionInfo, String loginUserInfo) {
        return String.format(
            """
                **Endpoint**
                %s
                **Message**
                %s
                **Login User Info**
                %s
                """, exceptionInfo.getEndpoint(), exceptionInfo.getMessage(), loginUserInfo);
    }

    private ExceptionInfo createExceptionInfo(String clazz, String message, String requestUri, String method,
        UserContext userContext) {
        return ExceptionInfo.builder()
            .clazz(clazz)
            .message(message)
            .requestUri(requestUri)
            .method(method)
            .userContextOpt(Optional.ofNullable(userContext))
            .build();
    }
}