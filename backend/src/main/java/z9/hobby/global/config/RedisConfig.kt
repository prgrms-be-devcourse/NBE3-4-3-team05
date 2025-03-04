package z9.hobby.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.RedisPassword

@Configuration
class RedisConfig(@Autowired private val environment: Environment) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {

        val redisConfig = RedisStandaloneConfiguration()

        val host = environment.getProperty("redis.host")!!.toString()
        val port = environment.getProperty("redis.port")!!.toInt()
        val password = RedisPassword.of(environment.getProperty("redis.password")!!)

        redisConfig.hostName = host
        redisConfig.port = port
        redisConfig.password = password

        return LettuceConnectionFactory(redisConfig)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = StringRedisSerializer()
        redisTemplate.connectionFactory = redisConnectionFactory()
        return redisTemplate
    }
}
