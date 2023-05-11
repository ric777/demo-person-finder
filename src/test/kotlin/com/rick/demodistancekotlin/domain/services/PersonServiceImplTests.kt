package com.rick.demodistancekotlin.domain.services

import com.rick.demodistancekotlin.data.Person
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.mockito.Mockito.`when`


class PersonServiceImplTests {

    @Mock
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    @Mock
    private lateinit var valueOperations: ValueOperations<String, Any>

    @InjectMocks
    private lateinit var personServiceImpl: PersonsServiceImpl

    @BeforeEach
    fun setup(){
        MockitoAnnotations.openMocks(this)
        `when`(redisTemplate.opsForValue()).thenReturn(valueOperations)
    }

    @Test
    fun testGetByIds() {
        val personIds = arrayListOf(1L, 2L)
        val personA = Person(1, "Rick")
        `when`(valueOperations.get("1")).thenReturn(personA)

        val result = personServiceImpl.getByIds(personIds)
        assertEquals(1, result.size)
        assertTrue(result.contains(personA))
    }

    @Test
    fun testSave(){
        val personA = Person(1, "Rick")
        `when`(valueOperations.set("1", personA)).thenAnswer { invocation ->
            val key = invocation.arguments[0] as String
            val value = invocation.arguments[1] as Person

            assertEquals("1", key)
            assertEquals(personA, value)
        }

        val result = personServiceImpl.save(personA)
        assertTrue(result)
    }
}