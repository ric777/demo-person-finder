package com.rick.demodistancekotlin.domain.services

import com.rick.demodistancekotlin.data.Location
import org.springframework.data.geo.Point

interface LocationsService {
    fun addLocation(location: Location):Boolean
    fun removeLocation(locationReferenceId: Long):Long
    fun getLocationById(referenceId: Long): MutableList<Point>?
    fun findAround(latitude: Double, longitude: Double, radiusInKm: Double) : List<Location>
}