package com.rick.demodistancekotlin.domain.services

import com.rick.demodistancekotlin.data.Location
import com.rick.demodistancekotlin.data.Person
import jakarta.annotation.Resource
import org.springframework.data.geo.Circle
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class LocationServiceImpl : LocationsService{

    @Resource
    lateinit var redisTemplate: RedisTemplate<String, Any>

    override fun addLocation(location: Location): Boolean {
        if (redisTemplate.opsForValue().get(location.referenceId.toString()) == null) {
            return false
        }

        redisTemplate.opsForGeo().add("testGeo", Point(location.longitude, location.latitude), location.referenceId.toString())
        return true
    }

    override fun removeLocation(locationReferenceId: Long): Long {
        return redisTemplate.opsForGeo().remove("testGeo", locationReferenceId.toString())?: 0L
    }

    override fun getLocationById(referenceId: Long): MutableList<Point>? {
        return redisTemplate.opsForGeo().position("testGeo", referenceId.toString())
    }

    override fun findAround(latitude: Double, longitude: Double, radiusInKm: Double): List<Location> {
        val circle =
            Circle(Point(longitude, latitude), Distance(radiusInKm, Metrics.KILOMETERS)) //Point(经度, 纬度) Distance(距离量, 距离单位)

        val args = GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance()
            .includeCoordinates().sortAscending().limit(5)
        val results = redisTemplate.opsForGeo().radius("testGeo", circle, args) //params: key, Circle, GeoRadiusCommandArgs

        val locations = mutableListOf<Location>()

        results?.forEach { geoResult ->
            val location = geoResult.content
            val distance = geoResult.distance
            val referenceId: Long = location.name.toString().toLongOrNull() ?: 0L

            locations.add(Location(referenceId, location.point.y, location.point.x))
        }

        return locations
    }

}