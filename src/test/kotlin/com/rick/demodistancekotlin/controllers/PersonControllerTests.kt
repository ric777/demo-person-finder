package com.rick.demodistancekotlin.controllers

import com.rick.demodistancekotlin.data.HttpResponse
import com.rick.demodistancekotlin.data.Location
import com.rick.demodistancekotlin.data.Person
import com.rick.demodistancekotlin.domain.services.LocationsService
import com.rick.demodistancekotlin.domain.services.PersonsService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.data.geo.Point
import org.springframework.http.HttpStatus

class PersonControllerTests {
    @Mock
    private lateinit var locationsService: LocationsService

    @Mock
    private lateinit var personsService: PersonsService

    private lateinit var personController: PersonController

    @BeforeEach
    fun setup(){
        MockitoAnnotations.openMocks(this)
        personController = PersonController(personsService, locationsService)
    }

    @Test
    fun testCreatePerson(){
        val person = Person(1, "David De Gea")
        `when`(personsService.save(person)).thenReturn(true)
        val responseEntity = personController.createPerson(person)
        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertTrue(responseEntity.body?.status.equals("OK"))
    }

    @Test
    fun testListPerson(){
        val personIds = arrayListOf(1L, 2L)
        val persons = arrayListOf(Person(1, "Kaka"), Person(2, "Ronaldo"))
        `when`(personsService.getByIds(personIds)).thenReturn(persons)

        val responseEntity = personController.listPerson(personIds)
        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals(persons, responseEntity.body)
    }

    @Test
    fun testCreateOrPutLocation_ValidLocation() {
        val location = Location(1L, 1.0, 2.0)
        `when`(locationsService.addLocation(location)).thenReturn(true)

        val responseEntity = personController.createOrPutLocation(location)
        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals("Successfully", responseEntity.body?.respMsg)
    }

    @Test
    fun testCreateOrPutLocation_NotValidLocation() {
        val location = Location(1L, 1.0, 2.0)
        `when`(locationsService.addLocation(location)).thenReturn(false)

        val responseEntity = personController.createOrPutLocation(location)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        assertEquals("Information not exists for this person", responseEntity.body?.respMsg)
    }

    @Test
    fun testGetPersonNearBy_ExistingLocation(){
        val locationReferenceId = 1L
        val radiusInKm = 100.0
        val point = Point(120.23, 12.22)
        `when`(locationsService.getLocationById(locationReferenceId)).thenReturn(mutableListOf(point))

        val nearByPersonA = Location(2, 120.24, 12.22)
        val nearByPersonB = Location(2, 120.25, 12.22)
        `when`(locationsService.findAround(point.y, point.x, radiusInKm)).thenReturn(mutableListOf(nearByPersonA,nearByPersonB))

        val expectNearByList = mutableListOf(nearByPersonA,nearByPersonB)

        val responseEntity = personController.getPersonNearBy(locationReferenceId, radiusInKm)

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals(expectNearByList, responseEntity.body)
    }

    @Test
    fun testGetPersonNearBy_NonExistingLocation(){
        val locationReferenceId = 3L
        val radiusInKm = 100.0
        `when`(locationsService.getLocationById(locationReferenceId)).thenReturn(null)

        val responseEntity = personController.getPersonNearBy(locationReferenceId, radiusInKm)
        val httpResponse = responseEntity.body as HttpResponse
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        assertEquals("Bad Request", httpResponse.status)
        assertEquals("The person does not have related location",httpResponse.respMsg)
    }

    @Test
    fun testDeleteLocation_ExistingLocation() {
        val locationReferenceId = 1L
        `when`(locationsService.removeLocation(locationReferenceId)).thenReturn(1)

        val responseEntity = personController.deleteLocation(locationReferenceId)
        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals("OK", responseEntity.body?.status)
        assertEquals("Successfully", responseEntity.body?.respMsg)
    }

    @Test
    fun testDeleteLocation_NonExistingLocation() {
        val locationReferenceId = 2L
        `when`(locationsService.removeLocation(locationReferenceId)).thenReturn(0)

        val responseEntity = personController.deleteLocation(locationReferenceId)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        assertEquals("Bad Request", responseEntity.body?.status)
        assertEquals("Current Location does not exist", responseEntity.body?.respMsg)
    }

    @Test
    fun testGetExample() {
        val responseEntity = personController.getExample()

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals("hello", responseEntity.body)
    }
}