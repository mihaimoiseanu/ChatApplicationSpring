package com.coders.chat.model.exceptions

import com.coders.chat.model.exceptions.base.SubException

class ApiValidationException(
    var obj: String,
    var field: String,
    var rejectedValue: Any? = null,
    var message: String
) : SubException()
