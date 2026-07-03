package com.tailtown.pawcare.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Universal envelope for every API response.
 *
 * Success:  { "success": true,  "data": {...} }
 * Failure:  { "success": false, "error": {...} }
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null,
    val meta: PaginationMeta? = null,
)

/** Standard error body returned by the backend. */
@Serializable
data class ApiError(
    val code: String,           // e.g. "VET_NOT_FOUND", "INVALID_OTP"
    val message: String,        // human-readable, shown in UI
)

/**
 * Pagination metadata embedded in list responses.
 * Used by: GET /vets, GET /bookings, GET /pets
 */
@Serializable
data class PaginationMeta(
    val page: Int = 1,
    @SerialName("page_size") val pageSize: Int = 20,
    val total: Int = 0,
    @SerialName("has_more") val hasMore: Boolean = false,
)
