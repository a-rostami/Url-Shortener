package com.rostami.urlshortener.service;

import com.rostami.urlshortener.dto.out.UrlCreateResult;
import com.rostami.urlshortener.dto.out.UrlFindResult;
import com.rostami.urlshortener.exception.NullUrlException;
import com.rostami.urlshortener.exception.UrlNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;


import static com.rostami.urlshortener.exception.message.ExceptionMessages.NULL_URL_MESSAGE;
import static com.rostami.urlshortener.exception.message.ExceptionMessages.URL_NOT_FOUND_EXCEPTION_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;
    private UrlService urlService;


    @BeforeEach
    void setup(){
        urlService = new UrlService(redisTemplate);
    }

    @Test
    void test_generate_shortUrl_isOk() {
        // given ----------------------------------------------------------------------------------
        String url = "https:stackoverflow.com";
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        // when ----------------------------------------------------------------------------------
        UrlCreateResult<String> urlCreateResult = urlService.generateShortUrl(url);

        // then ----------------------------------------------------------------------------------
        ArgumentCaptor<String> originalUrlCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> shortedUrlCapture = ArgumentCaptor.forClass(String.class);
        verify(redisTemplate.opsForValue()).set(shortedUrlCapture.capture(), originalUrlCapture.capture());

        String originalUrl = originalUrlCapture.getValue();
        String shortedUrl = shortedUrlCapture.getValue();

        assertThat(originalUrl).isEqualTo(url);
        assertThat(shortedUrl).isEqualTo(urlCreateResult.getData());
        assertThat(urlCreateResult.isSuccess()).isTrue();
    }

    @Test
    void test_loadOriginalUrl_will_throw_when_urlIsNull_isOk() {
        // given ----------------------------------------------------------------------------------
        // when ----------------------------------------------------------------------------------
        // then ----------------------------------------------------------------------------------
        assertThatThrownBy(() -> urlService.loadOriginalUrl(null))
                .isInstanceOf(NullUrlException.class)
                .hasMessage(NULL_URL_MESSAGE);

    }

    @Test
    void test_loadOriginalUrl_will_throw_when_urlIsBlank_isOk() {
        // given ----------------------------------------------------------------------------------
        String shortUrl = "";

        // when ----------------------------------------------------------------------------------
        // then ----------------------------------------------------------------------------------
        assertThatThrownBy(() -> urlService.loadOriginalUrl(shortUrl))
                .isInstanceOf(NullUrlException.class)
                .hasMessage(NULL_URL_MESSAGE);
    }

    @Test
    void test_loadOriginalUrl_will_throw_when_shortUrl_doesNot_exist_isOk(){
        // given ----------------------------------------------------------------------------------
        String shortUrl = "132dAfd";
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(redisTemplate.opsForValue().get(shortUrl)).willReturn(null);

        // when ----------------------------------------------------------------------------------
        // then ----------------------------------------------------------------------------------
        assertThatThrownBy(() -> urlService.loadOriginalUrl(shortUrl))
                .isInstanceOf(UrlNotFoundException.class)
                .hasMessage(URL_NOT_FOUND_EXCEPTION_MESSAGE);
    }

    @Test
    void test_loadOriginalUrl_returnValue_isOk(){
        // given ----------------------------------------------------------------------------------
        String originalUrl = "https://stackoverflow.com";
        String shortLink = "13Ads4";
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(redisTemplate.opsForValue().get(shortLink)).willReturn(originalUrl);


        // when ----------------------------------------------------------------------------------
        UrlFindResult result = urlService.loadOriginalUrl(shortLink);

        // then ----------------------------------------------------------------------------------
        assertThat(result.getOriginalUrl()).isEqualTo(originalUrl);
        assertThat(result.getShortUrl()).isEqualTo(shortLink);

    }
}