package com.rostami.urlshortener.dto.api;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseResult<T> {
    private T data;
    private String message;
}
