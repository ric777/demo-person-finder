package com.rick.demodistancekotlin.domain.services

import com.rick.demodistancekotlin.data.Location
import com.rick.demodistancekotlin.data.Person

interface PersonsService {
    fun getByIds(personIds: ArrayList<Long>): ArrayList<Person>
    fun save(person: Person): Boolean
}