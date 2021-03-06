package com.rostami.urlshortener.controller;

import com.rostami.urlshortener.dto.api.ResponseResult;
import com.rostami.urlshortener.dto.in.UrlCreateParam;
import com.rostami.urlshortener.dto.out.UrlCreateResult;
import com.rostami.urlshortener.dto.out.UrlFindResult;
import com.rostami.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/urlShortenerService")
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/generateShortUrl")
    public ResponseEntity<ResponseResult<String>> generateShortUrl(@Valid @RequestBody UrlCreateParam urlCreateParam){
        UrlCreateResult<String> result = urlService.generateShortUrl(urlCreateParam.getOriginalUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseResult.<String>builder()
                .message("Successfully Created Short Url")
                .data(result.getData())
                .build());
    }

    @GetMapping("/loadOriginalUrl/{shortUrl}")
    public ResponseEntity<ResponseResult<UrlFindResult>> loadOriginalUrl(@PathVariable String shortUrl){
        UrlFindResult urlFindResult = urlService.loadOriginalUrl(shortUrl);
        return ResponseEntity.ok(ResponseResult.<UrlFindResult>builder()
                .data(urlFindResult)
                .message("Successfully Load Original Url Of Short URL")
                .build());
    }
}
