package com.rostami.urlshortener.exception.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionMessages {
    public static final String URL_NOT_FOUND_EXCEPTION_MESSAGE = "There Is No Url For This Shorted Url.";
    public static final String NULL_URL_MESSAGE = "Url Cannot be null or empty";
}
