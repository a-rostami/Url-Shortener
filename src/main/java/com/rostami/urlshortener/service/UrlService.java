package com.rostami.urlshortener.service;

import com.google.common.hash.Hashing;
import com.rostami.urlshortener.dto.out.UrlCreateResult;
import com.rostami.urlshortener.dto.out.UrlFindResult;
import com.rostami.urlshortener.exception.NullUrlException;
import com.rostami.urlshortener.exception.UrlNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static com.rostami.urlshortener.exception.message.ExceptionMessages.NULL_URL_MESSAGE;
import static com.rostami.urlshortener.exception.message.ExceptionMessages.URL_NOT_FOUND_EXCEPTION_MESSAGE;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final StringRedisTemplate redisTemplate;
    
    @Transactional
    public UrlCreateResult<String> generateShortUrl(String originalUrl){
        String shortedUrl = Hashing.murmur3_32().hashString(originalUrl, StandardCharsets.UTF_8).toString();
        redisTemplate.opsForValue().set(shortedUrl, originalUrl);
        return UrlCreateResult.<String>builder()
                .data(shortedUrl)
                .success(true)
                .build();
    }

    @Transactional(readOnly = true)
    public UrlFindResult loadOriginalUrl(String shortUrl){
        if (shortUrl == null || shortUrl.isBlank()) throw new NullUrlException(NULL_URL_MESSAGE);
        String originalUrl = redisTemplate.opsForValue().get(shortUrl);
        if (originalUrl == null) throw new UrlNotFoundException(URL_NOT_FOUND_EXCEPTION_MESSAGE);
        return UrlFindResult.builder()
                .originalUrl(originalUrl)
                .shortUrl(shortUrl)
                .build();
    }
}
