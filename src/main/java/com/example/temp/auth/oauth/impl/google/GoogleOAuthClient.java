package com.example.temp.auth.oauth.impl.google;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface GoogleOAuthClient {

    @PostExchange(url = "/oauth2/v4/token", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    GoogleToken fetchToken(@RequestBody MultiValueMap<String, String> params);
}
