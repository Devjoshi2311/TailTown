package com.tailtown.backend.platform.exception

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.UUID

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(TailTownException::class)
    fun handleTailTownException(ex: TailTownException): ResponseEntity<ErrorResponse> {
        val requestId = extractRequestId()
        log.warn(
            "TailTownException [{}] requestId={} message={}",
            ex.errorCode,
            requestId,
            ex.message
        )
        val status = HttpStatus.valueOf(ex.errorCode.httpStatus())
        return ResponseEntity.status(status).body(ErrorResponse.from(ex, requestId))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val requestId = extractRequestId()
        val fieldErrors = ex.bindingResult.fieldErrors.associate { fieldError ->
            fieldError.field to (fieldError.defaultMessage ?: "Invalid value")
        }
        log.warn(
            "MethodArgumentNotValidException requestId={} fieldErrors={}",
            requestId,
            fieldErrors
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.validation(fieldErrors, requestId))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val requestId = extractRequestId()
        log.warn("HttpMessageNotReadableException requestId={} message={}", requestId, ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                code = ErrorCode.VALIDATION_ERROR.name,
                message = "Invalid request body",
                requestId = requestId,
                timestamp = java.time.Instant.now().toString(),
                fieldErrors = null
            )
        )
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleMissingRequestHeader(ex: MissingRequestHeaderException): ResponseEntity<ErrorResponse> {
        val requestId = extractRequestId()
        log.warn(
            "MissingRequestHeaderException requestId={} header={}",
            requestId,
            ex.headerName
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                code = ErrorCode.VALIDATION_ERROR.name,
                message = "Required header '${ex.headerName}' is missing",
                requestId = requestId,
                timestamp = java.time.Instant.now().toString(),
                fieldErrors = null
            )
        )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        val requestId = extractRequestId()
        log.warn("AccessDeniedException requestId={} message={}", requestId, ex.message)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse(
                code = ErrorCode.FORBIDDEN.name,
                message = "Access denied",
                requestId = requestId,
                timestamp = java.time.Instant.now().toString(),
                fieldErrors = null
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val requestId = extractRequestId()
        log.error("Unhandled exception requestId={}", requestId, ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.internal(requestId))
    }

    private fun extractRequestId(): String =
        MDC.get("requestId") ?: UUID.randomUUID().toString()
}
