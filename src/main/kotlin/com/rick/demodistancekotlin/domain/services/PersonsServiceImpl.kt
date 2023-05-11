package com.rick.demodistancekotlin.domain.services

import com.rick.demodistancekotlin.data.Person
import jakarta.annotation.Resource
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class PersonsServiceImpl:PersonsService {

    @Resource
    lateinit var redisTemplate: RedisTemplate<String, Any>

    override fun getByIds(personIds: ArrayList<Long>): ArrayList<Person> {
        val personList:ArrayList<Person> = ArrayList()
        for (personId in personIds) {
            val get = redisTemplate.opsForValue().get(personId.toString())
            if (get != null) {
                personList.add(get as Person)
            }
        }

        return personList
    }

    override fun save(person: Person) :Boolean{
        try {
            redisTemplate.opsForValue().set(person.id.toString(), person)
        } catch (ex:Exception){
            println(ex.message)
            return false
        }

        return true
    }
}