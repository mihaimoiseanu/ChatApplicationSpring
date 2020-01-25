package com.coders.chat.model

import java.util.*

fun <T : Any> Optional<T>.toNullable(): T? = this.orElse(null)