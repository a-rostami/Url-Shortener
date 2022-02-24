package com.rostami.urlshortener.dto.in;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
public class ShortUrlCreateParam {
    @Pattern(regexp = "((http|https)://)(www.)?[a-zA-Z0-9@:%._+~#?&/=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%._+~#?&/=]*)")
    @NotNull
    private String originalUrl;
}
