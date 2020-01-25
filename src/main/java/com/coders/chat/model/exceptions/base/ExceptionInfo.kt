package com.coders.chat.model.exceptions.base

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

/**
 * Class that defines a json serialization for a exception
 * Created by cosmin on 21-11-2017.
 */
@JsonInclude(value = NON_NULL)
class ExceptionInfo(
        var tag: ExceptionTag,
        var status: HttpStatus? = null,
        @JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        var timestamp: LocalDateTime = LocalDateTime.now(),
        var message: String,
        var subExceptions: List<SubException>? = null
) {

    /**
     * Build an ExceptionInfo base on a known actors exception
     */
    constructor(e: ApplicationException) : this(message = e.message, tag = e.tag, timestamp = e.timestamp, status = e.status, subExceptions = e.subExceptions)

    /**
     * Build an ExceptionInfo base on an unexpected exception
     */
    constructor(e: Exception) : this(message = e.message ?: "Unexpected exception!", tag = ExceptionTag.UNSPECIFIED)
}
