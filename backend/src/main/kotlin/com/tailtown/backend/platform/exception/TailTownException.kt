package com.tailtown.backend.platform.exception

import com.tailtown.backend.platform.exception.ErrorCode.*
import java.util.UUID

abstract class TailTownException(
    val errorCode: ErrorCode,
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

class ResourceNotFoundException(resource: String, id: Any) :
    TailTownException(NOT_FOUND, "$resource not found: $id")

class ValidationException(
    message: String,
    val fieldErrors: Map<String, String> = emptyMap()
) : TailTownException(VALIDATION_ERROR, message)

open class ConflictException(errorCode: ErrorCode, message: String) :
    TailTownException(errorCode, message)

class SlotUnavailableException :
    ConflictException(SLOT_UNAVAILABLE, "Slot is no longer available")

class VersionConflictException :
    ConflictException(VERSION_CONFLICT, "Version conflict — please reload and try again")

class IdempotencyConflictException :
    ConflictException(IDEMPOTENCY_CONFLICT, "Duplicate request in progress")

class OutOfStockException(productId: UUID) :
    ConflictException(OUT_OF_STOCK, "Product $productId is out of stock")

class EmailAlreadyRegisteredException :
    ConflictException(EMAIL_ALREADY_REGISTERED, "Email is already registered")

class AuthenticationException(message: String) :
    TailTownException(UNAUTHORIZED, message)

class ForbiddenException(message: String = "Access denied") :
    TailTownException(FORBIDDEN, message)

class WeightRecordDuplicateException(petId: java.util.UUID, date: java.time.LocalDate) :
    ConflictException(WEIGHT_RECORD_DUPLICATE, "Weight record already exists for pet $petId on $date")

class RateLimitExceededException :
    TailTownException(RATE_LIMITED, "Too many requests")

class ExternalServiceException(service: String, cause: Throwable) :
    TailTownException(EXTERNAL_SERVICE_ERROR, "External service failed: $service", cause)
