package com.rostami.urlshortener.controller.api.core;

import com.rostami.urlshortener.controller.api.errors.api.ApiError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResult<T> {

    private int code;
    private T data;
    private String message;
    private ApiError error;

    public static ServiceResult<Void> fail(ApiError apiError) {
        return new ServiceResult<>(apiError.getStatus().value(),
                null,
                apiError.getMessage(),
                apiError);
    }
}
