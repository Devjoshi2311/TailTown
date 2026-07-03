package com.tailtown.backend.platform.exception

import java.time.Instant

data class ErrorResponse(
    val code: String,
    val message: String,
    val requestId: String,
    val timestamp: String,
    val fieldErrors: Map<String, String>?
) {
    companion object {
        fun from(ex: TailTownException, requestId: String): ErrorResponse {
            val fieldErrors = if (ex is ValidationException && ex.fieldErrors.isNotEmpty()) {
                ex.fieldErrors
            } else {
                null
            }
            return ErrorResponse(
                code = ex.errorCode.name,
                message = ex.message ?: ex.errorCode.name,
                requestId = requestId,
                timestamp = Instant.now().toString(),
                fieldErrors = fieldErrors
            )
        }

        fun validation(fieldErrors: Map<String, String>, requestId: String): ErrorResponse =
            ErrorResponse(
                code = ErrorCode.VALIDATION_ERROR.name,
                message = "Validation failed",
                requestId = requestId,
                timestamp = Instant.now().toString(),
                fieldErrors = fieldErrors
            )

        fun internal(requestId: String): ErrorResponse =
            ErrorResponse(
                code = ErrorCode.INTERNAL_ERROR.name,
                message = "An unexpected error occurred",
                requestId = requestId,
                timestamp = Instant.now().toString(),
                fieldErrors = null
            )
    }
}
