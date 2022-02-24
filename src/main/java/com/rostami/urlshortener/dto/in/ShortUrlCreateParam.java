package com.rostami.urlshortener.dto.in;

import lombok.Data;

@Data
public class ShortUrlCreateParam {
    private String originalUrl;
}
