package com.rostami.urlshortener.service;

import com.google.common.hash.Hashing;
import com.rostami.urlshortener.dto.api.CreateResult;
import com.rostami.urlshortener.dto.out.UrlFindResult;
import com.rostami.urlshortener.exception.UrlNotFoundException;
import com.rostami.urlshortener.exception.message.ExceptionMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static com.rostami.urlshortener.exception.message.ExceptionMessages.URL_NOT_FOUND_EXCEPTION;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final StringRedisTemplate redisTemplate;
    
    @Transactional
    public CreateResult<String> generateShortUrl(String originalUrl){
        String shortedUrl = Hashing.murmur3_32().hashString(originalUrl, StandardCharsets.UTF_8).toString();
        redisTemplate.opsForValue().set(shortedUrl, originalUrl);
        return CreateResult.<String>builder()
                .success(true)
                .data(shortedUrl)
                .message("Successfully Created Short Url.")
                .build();
    }

    @Transactional(readOnly = true)
    public UrlFindResult loadOriginalUrl(String shortUrl){
        String originalUrl = redisTemplate.opsForValue().get(shortUrl);
        if (originalUrl == null)
            throw new UrlNotFoundException(URL_NOT_FOUND_EXCEPTION);
        return UrlFindResult.builder()
                .originalUrl(originalUrl)
                .shortUrl(shortUrl)
                .build();
    }
}
