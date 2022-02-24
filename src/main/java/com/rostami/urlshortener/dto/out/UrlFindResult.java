package com.rostami.urlshortener.dto.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlFindResult {
    private String originalUrl;
    private String shortUrl;
}
