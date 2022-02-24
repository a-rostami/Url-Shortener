package com.rostami.urlshortener.dto.out;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UrlCreateResult<T> {
    private boolean success;
    private T data;
}
