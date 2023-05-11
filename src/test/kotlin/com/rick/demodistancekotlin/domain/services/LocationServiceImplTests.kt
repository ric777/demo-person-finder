package com.rick.demodistancekotlin.domain.services

import com.rick.demodistancekotlin.data.Location
import com.rick.demodistancekotlin.data.Person
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.data.geo.Point
import org.springframework.data.redis.core.GeoOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations

class LocationServiceImplTests {

    @Mock
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    @Mock
    private lateinit var valueOperations: ValueOperations<String, Any>

    @Mock
    private lateinit var geoOperations: GeoOperations<String, Any>

    @InjectMocks
    private lateinit var locationService: LocationServiceImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(redisTemplate.opsForValue()).thenReturn(valueOperations)
        `when`(redisTemplate.opsForGeo()).thenReturn(geoOperations)
    }

    @Test
    fun testAddLocation(){
        val location = Location(1, 10.0, 20.0)
        val person = Person(1, "Rick")
        `when`(valueOperations.get(location.referenceId.toString())).thenReturn(person)
        `when`(geoOperations.add(anyString(), any(), any())).thenReturn(null)

        val result = locationService.addLocation(location)
        assertTrue(result)
    }

    @Test
    fun testAddLocation_NonExistPerson(){
        val location = Location(2, 10.0, 20.0)
        `when`(valueOperations.get(location.referenceId.toString())).thenReturn(null)

        val result = locationService.addLocation(location)
        assertFalse(result)
    }

    @Test
    fun testRemoveLocation() {
        val locationReferenceId = 1L
        `when`(redisTemplate.opsForGeo().remove("testGeo", locationReferenceId.toString())).thenReturn(1L)
        val result = locationService.removeLocation(locationReferenceId)

        assertEquals(1L, result)
    }

    @Test
    fun testGetLocationById() {
        val referenceId = 1L
        val point = Point(20.0, 20.0)

        `when`(redisTemplate.opsForGeo().position("testGeo", referenceId.toString())).thenReturn(mutableListOf(point))

        val result = locationService.getLocationById(referenceId)

        assertEquals(1, result?.size)
    }

}