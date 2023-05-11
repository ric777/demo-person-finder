package com.rick.demodistancekotlin.controllers

import com.rick.demodistancekotlin.data.HttpResponse
import com.rick.demodistancekotlin.data.Location
import com.rick.demodistancekotlin.data.Person
import com.rick.demodistancekotlin.domain.services.LocationsService
import com.rick.demodistancekotlin.domain.services.PersonsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/persons")
class PersonController @Autowired constructor
    (var personsService: PersonsService, var locationsService: LocationsService) {

    @PostMapping
    fun createPerson(@RequestBody person:Person): ResponseEntity<HttpResponse> {
        return if (personsService.save(person)){
            ResponseEntity.ok().body(HttpResponse("OK", "Successful store the data with id: ${person.id}"))
        } else {
            ResponseEntity.badRequest().body(HttpResponse("Bad Request", "Error found when store the person data: $person"))
        }
    }

    @GetMapping
    fun listPerson(@RequestBody list: ArrayList<Long>):ResponseEntity<List<Person>>{
       return ResponseEntity.ok().body(personsService.getByIds(list))
    }

    @PutMapping("/location")
    fun createOrPutLocation(@RequestBody location: Location): ResponseEntity<HttpResponse> {
        return if (locationsService.addLocation(location)) {
            ResponseEntity.ok().body(HttpResponse("OK","Successfully"))
        } else {
            ResponseEntity.badRequest().body(HttpResponse("Bad Request","Information not exists for this person"))
        }
    }

    @GetMapping("/location/{locationReferenceId}")
    fun getPersonNearBy(@PathVariable locationReferenceId: Long, @RequestParam radiusInKm: Double): ResponseEntity<Any> {
        var points = locationsService.getLocationById(locationReferenceId)
        if (points?.get(0) == null){
            return ResponseEntity.badRequest().body(HttpResponse("Bad Request","The person does not have related location"))
        }
        var findAround = locationsService.findAround(points[0].y, points[0].x, radiusInKm)
        return ResponseEntity.ok().body(findAround)
    }

    @DeleteMapping("/location/{locationReferenceId}")
    fun deleteLocation(@PathVariable locationReferenceId: Long): ResponseEntity<HttpResponse>{
        return if (locationsService.removeLocation(locationReferenceId) > 0) {
            ResponseEntity.ok().body(HttpResponse("OK","Successfully"))
        } else {
            ResponseEntity.badRequest().body(HttpResponse("Bad Request","Current Location does not exist"))
        }
    }

    @GetMapping("/example")
    fun getExample(): ResponseEntity<String> {
        return ResponseEntity.ok().body("hello")
    }
}