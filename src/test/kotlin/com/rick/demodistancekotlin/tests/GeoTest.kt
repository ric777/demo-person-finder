package com.rick.demodistancekotlin.tests

import com.rick.demodistancekotlin.data.Person
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.geo.Distance
import org.springframework.data.geo.GeoResults
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.redis.connection.RedisGeoCommands
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class GeoTest {

    @Resource
    lateinit var redisTemplate: RedisTemplate<String, Any>

    /**
     *  将指定的地理空间位置（纬度、经度、名称）添加到指定的key中。
     */
    @Test
    fun redisTestAdd() {
//        val addedNum = redisTemplate.opsForGeo().add("geo", Point(116.405285, 39.904989), "北京")
        // val addedNum = redisTemplate.opsForGeo().add("geo", Point(121.47, 31.23), "上海")
        // val addedNum = redisTemplate.opsForGeo().add("Sello", Point(121.47, 31.23), "hello")
         val addedNum = redisTemplate.opsForGeo().add("geo", Point(113.27, 23.13), "广州")
//         val addedNum = redisTemplate.opsForGeo().add("geo", Point(114.05, 22.55), "深圳")
        println(addedNum)
    }

    /**
     *  从key里返回所有给定位置元素的位置（经度和纬度）。
     */
    @Test
    fun redisTestGeoGet() {
        val points = redisTemplate.opsForGeo().position("testGeo", "1")
        println(points?.get(0) == null)
    }

    @Test
    fun testDistance(){
        val distance = redisTemplate.opsForGeo()
            .distance("geo", "北京", "上海", RedisGeoCommands.DistanceUnit.KILOMETERS) //params: key, 地方名称1, 地方名称2, 距离单位

        println(distance)
    }

    //return: GeoResults: [averageDistance: 51.367149999999995 KILOMETERS, results: GeoResult [content: RedisGeoCommands.GeoLocation(name=深圳, point=Point [x=114.049998, y=22.550001]), distance: 3.0E-4 KILOMETERS, ],GeoResult [content: RedisGeoCommands.GeoLocation(name=广州, point=Point [x=113.270001, y=23.130001]), distance: 102.734 KILOMETERS, ]]
    /**
     * 以给定的城市为中心， 返回键包含的位置元素当中， 与中心的距离不超过给定最大距离的所有位置元素，并给出所有位置元素与中心的平均距离。
     */
    @Test
    fun testNearByPlace() {
        val distance = Distance(200.0, Metrics.KILOMETERS) //params: 距离量, 距离单位
        val args =
            GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates().sortAscending().limit(5)
        val results: GeoResults<RedisGeoCommands.GeoLocation<Any>>? = redisTemplate.opsForGeo().radius("geo", "深圳", distance, args) //params: key, 地方名称, Circle, GeoRadiusCommandArgs
        println(results)
    }

    @Test
    fun testRemoveLocation(){
        println(redisTemplate.opsForGeo().remove("testGeo","1"))
    }

    @Test
    fun redisTestPerson(){
        val user = Person(1,"Rick");
        redisTemplate.opsForValue().set(user.name, user);
        println(redisTemplate.opsForValue().get(user.name))
    }

    @Test
    fun redisTestGetValue(){
        println(redisTemplate.opsForValue().get("Jason"))
    }

    @Test
    fun test(){
        val list =  mutableListOf<Long>()
        println(list.size)
    }

}