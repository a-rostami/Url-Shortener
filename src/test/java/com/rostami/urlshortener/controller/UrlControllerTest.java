package com.rostami.urlshortener.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rostami.urlshortener.dto.in.UrlCreateParam;
import com.rostami.urlshortener.dto.out.UrlCreateResult;
import com.rostami.urlshortener.dto.out.UrlFindResult;
import com.rostami.urlshortener.service.UrlService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @MockBean
    private UrlService urlService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test_generateShortUrl_isOk() throws Exception {
        // given -------------------------------------------------------------
        String originalUrl = "https://stackoverflow.com";
        String shortUrl = "as123d";
        UrlCreateParam createParam = UrlCreateParam.builder().originalUrl(originalUrl).build();

        UrlCreateResult<String> result = UrlCreateResult.<String>builder().success(true).data(shortUrl).build();
        given(urlService.generateShortUrl(originalUrl)).willReturn(result);


        // when --------------------------------------------------------------
        // then --------------------------------------------------------------
        mockMvc.perform(post("/api/urlShortenerService/generateShortUrl")
                .contentType(APPLICATION_JSON)
                .content(toJson(createParam)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data", Matchers.is(shortUrl)));
    }

    @Test
    void test_loadOriginalUrl_isOk() throws Exception {
        // given -------------------------------------------------------------
        String originalUrl = "https://stackoverflow.com";
        String shortUrl = "as123d";
        UrlFindResult findResult = UrlFindResult.builder()
                .originalUrl(originalUrl)
                .shortUrl(shortUrl)
                .build();
        given(urlService.loadOriginalUrl(shortUrl)).willReturn(findResult);


        // when -------------------------------------------------------------
        // then -------------------------------------------------------------
        mockMvc.perform(get("/api/urlShortenerService/loadOriginalUrl/{shortUrl}", shortUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.originalUrl", Matchers.is(originalUrl)))
                .andExpect(jsonPath("$.data.shortUrl", Matchers.is(shortUrl)));

    }

    String toJson(UrlCreateParam param) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(param);
    }
}