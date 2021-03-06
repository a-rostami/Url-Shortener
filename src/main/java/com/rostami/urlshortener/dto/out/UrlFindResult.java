package com.rostami.urlshortener.dto.out;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UrlFindResult {
    private String originalUrl;
    private String shortUrl;
}
