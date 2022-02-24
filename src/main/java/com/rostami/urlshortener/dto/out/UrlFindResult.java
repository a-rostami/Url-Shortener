package com.rostami.urlshortener.dto.out;

import lombok.Data;

@Data
public class UrlFindResult {
    private String originalUrl;
    private String shortUrl;
}
