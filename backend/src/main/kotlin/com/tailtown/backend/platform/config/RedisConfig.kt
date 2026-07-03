package com.tailtown.backend.platform.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
class RedisConfig {

    @Bean
    fun redisCacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer {
        return RedisCacheManagerBuilderCustomizer { builder ->
            val cacheTtls = mapOf(
                "user-profile" to Duration.ofHours(24),
                "pets" to Duration.ofHours(24),
                "vet-list" to Duration.ofMinutes(30),
                "vet-profile" to Duration.ofHours(1),
                "vet-slots" to Duration.ofMinutes(5),
                "product-catalog" to Duration.ofHours(1),
                "product-detail" to Duration.ofHours(1),
                "subscription-plans" to Duration.ofHours(6),
                "referral-summary" to Duration.ofMinutes(15)
            )

            cacheTtls.forEach { (cacheName, ttl) ->
                builder.withCacheConfiguration(
                    cacheName,
                    RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(ttl)
                        .disableCachingNullValues()
                        .serializeKeysWith(
                            RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
                        )
                        .serializeValuesWith(
                            RedisSerializationContext.SerializationPair.fromSerializer(
                                GenericJackson2JsonRedisSerializer(redisObjectMapper())
                            )
                        )
                )
            }
        }
    }

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()
        template.valueSerializer = GenericJackson2JsonRedisSerializer(redisObjectMapper())
        template.hashValueSerializer = GenericJackson2JsonRedisSerializer(redisObjectMapper())
        template.afterPropertiesSet()
        return template
    }

    private fun redisObjectMapper(): ObjectMapper {
        return ObjectMapper()
            .registerModule(KotlinModule.Builder().build())
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .activateDefaultTyping(
                com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.builder()
                    .allowIfSubType(Any::class.java)
                    .build(),
                ObjectMapper.DefaultTyping.NON_FINAL
            )
    }
}
