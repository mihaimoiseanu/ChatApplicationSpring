package com.coders.chat.model.exceptions.base

import org.springframework.http.HttpStatus

enum class ExceptionTag(var httpStatus: HttpStatus) {
    UNSPECIFIED(HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    FORBIDDEN(HttpStatus.FORBIDDEN),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    INVALID_PARAMETER(HttpStatus.CONFLICT),
    NO_CONTENT(HttpStatus.NO_CONTENT),
    CONFLICT(HttpStatus.CONFLICT),
    EMAIL_EXCEPTION(HttpStatus.CONFLICT)
}
