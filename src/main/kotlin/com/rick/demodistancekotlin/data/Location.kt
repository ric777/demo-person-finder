package com.rick.demodistancekotlin.data

import java.io.Serializable

data class Location(
    // Tip: Person's id can be used for this field
    val referenceId: Long,
    val latitude: Double,
    val longitude: Double
):Serializable