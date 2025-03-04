package z9.hobby.global.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit
import z9.hobby.global.security.constant.JWTConstant.REFRESH_TOKEN_HEADER

@Repository
class RedisRepository(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    fun saveRefreshToken(userId: String, refreshToken: String, expiredTime: Long) {
        val key = keyGenerator(userId)
        save(key, refreshToken, expiredTime, TimeUnit.MILLISECONDS)
    }

    fun getRefreshToken(userId: String): String? {
        val key = keyGenerator(userId)
        return get(key) as? String
    }

    fun deleteRefreshToken(userId: String) {
        val key = keyGenerator(userId)
        delete(key)
    }

    private fun keyGenerator(userId: String): String {
        return "$REFRESH_TOKEN_HEADER:$userId"
    }


    private fun save(key: String, value: Any, duration: Long, timeUnit: TimeUnit) {
        redisTemplate.opsForValue().set(key, value, duration, timeUnit)
    }

    private fun get(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }

    private fun delete(key: String) {
        redisTemplate.delete(key)
    }
}
