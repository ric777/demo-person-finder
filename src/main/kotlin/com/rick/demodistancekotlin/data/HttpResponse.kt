package com.rick.demodistancekotlin.data

import java.io.Serializable

data class HttpResponse (
    val status: String,
    val respMsg: String
): Serializable