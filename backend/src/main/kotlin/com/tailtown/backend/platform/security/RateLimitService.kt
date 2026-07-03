package com.tailtown.backend.platform.security

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.TimeUnit

@Service
class RateLimitService(
    private val redisTemplate: RedisTemplate<String, String>
) {

    /**
     * Sliding window rate limiter using a Redis sorted set.
     *
     * Each member in the set is a unique timestamp-based string; the score is the epoch-millisecond
     * timestamp. On every call we:
     *  1. Remove members whose score falls outside the current window.
     *  2. Count remaining members.
     *  3. If count < limit, add a new entry and return true (allowed); otherwise return false.
     *  4. Reset the key TTL so it expires naturally after the window closes.
     */
    fun isAllowed(key: String, limit: Int, windowSeconds: Long): Boolean {
        val ops = redisTemplate.opsForZSet()
        val now = Instant.now().toEpochMilli()
        val windowStart = now - (windowSeconds * 1000)

        // Remove entries older than the window
        ops.removeRangeByScore(key, Double.NEGATIVE_INFINITY, windowStart.toDouble())

        // Count current entries within the window
        val currentCount = ops.zCard(key) ?: 0L

        return if (currentCount < limit) {
            // Add current request — use "timestamp-nanoTime" to keep members unique even if two
            // requests arrive at the same millisecond
            val member = "$now-${System.nanoTime()}"
            ops.add(key, member, now.toDouble())
            redisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS)
            true
        } else {
            // Still refresh TTL to prevent orphaned keys when limit is constantly hit
            redisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS)
            false
        }
    }

    /**
     * Returns how many requests remain in the current window without consuming a slot.
     * Returns 0 when the limit is already reached or exceeded.
     */
    fun getRemainingRequests(key: String, limit: Int, windowSeconds: Long): Int {
        val ops = redisTemplate.opsForZSet()
        val now = Instant.now().toEpochMilli()
        val windowStart = now - (windowSeconds * 1000)

        ops.removeRangeByScore(key, Double.NEGATIVE_INFINITY, windowStart.toDouble())

        val currentCount = (ops.zCard(key) ?: 0L).toInt()
        return maxOf(0, limit - currentCount)
    }
}
