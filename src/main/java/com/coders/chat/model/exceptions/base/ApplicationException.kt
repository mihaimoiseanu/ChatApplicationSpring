package com.coders.chat.model.exceptions.base

import com.coders.chat.model.exceptions.base.ExceptionTag.*
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class ApplicationException(
        var status: HttpStatus? = null,
        var timestamp: LocalDateTime = LocalDateTime.now(),
        override var message: String,
        var tag: ExceptionTag,
        var subExceptions: List<SubException>? = null,
        throwable: Throwable? = null
) : RuntimeException(throwable) {

    init {
        status = tag.httpStatus
    }

    constructor(tag: ExceptionTag, throwable: Throwable) : this(
            status = tag.httpStatus,
            tag = tag,
            message = throwable.localizedMessage,
            throwable = throwable)

    //    Exception utils
    companion object {
        fun notFoundException(message: String = "Not Found!") = ApplicationException(tag = NOT_FOUND, message = message)
        fun invalidParameterException(message: String = "Invalid Parameter!") = ApplicationException(tag = INVALID_PARAMETER, message = message)
        fun conflictException(message: String = "Conflict!") = ApplicationException(tag = CONFLICT, message = message)
    }
}
