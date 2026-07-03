package com.tailtown.pawcare.data.remote

import com.tailtown.pawcare.data.remote.dto.*
import retrofit2.http.*

interface ApiService {

    // ── Auth ─────────────────────────────────────────────────────────────────
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequestDto): ApiResponseDto<AuthResponseDto>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequestDto): ApiResponseDto<AuthResponseDto>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body body: RefreshTokenRequestDto): ApiResponseDto<AuthResponseDto>

    @POST("auth/firebase")
    suspend fun firebaseAuth(@Body body: FirebaseAuthRequestDto): ApiResponseDto<AuthResponseDto>

    @POST("auth/logout")
    suspend fun logout(@Body body: LogoutRequestDto): ApiResponseDto<Unit>

    // ── User / Profile ────────────────────────────────────────────────────────
    @GET("profile/me")
    suspend fun getMe(): ApiResponseDto<UserResponseDto>

    @PATCH("profile/me")
    suspend fun updateMe(@Body body: UpdateProfileRequestDto): ApiResponseDto<UserResponseDto>

    @GET("profile/addresses")
    suspend fun getAddresses(): ApiResponseDto<List<AddressResponseDto>>

    @POST("profile/addresses")
    suspend fun addAddress(@Body body: AddressRequestDto): ApiResponseDto<AddressResponseDto>

    @PATCH("profile/addresses/{addressId}")
    suspend fun updateAddress(
        @Path("addressId") addressId: String,
        @Body body: AddressRequestDto,
    ): ApiResponseDto<AddressResponseDto>

    @DELETE("profile/addresses/{addressId}")
    suspend fun deleteAddress(@Path("addressId") addressId: String): ApiResponseDto<Unit>

    @GET("users/me/payment-methods")
    suspend fun getPaymentMethods(): ApiResponseDto<List<PaymentMethodResponseDto>>

    @PATCH("users/me/payment-methods/{id}/default")
    suspend fun setDefaultPayment(@Path("id") id: String): ApiResponseDto<Unit>

    @DELETE("users/me/payment-methods/{id}")
    suspend fun deletePayment(@Path("id") id: String): ApiResponseDto<Unit>

    // ── Referral ──────────────────────────────────────────────────────────────
    @GET("referral")
    suspend fun getReferral(): ApiResponseDto<ReferralResponseDto>

    @POST("referral/claim")
    suspend fun claimReferral(@Body body: ClaimReferralRequestDto): ApiResponseDto<Unit>

    // ── Promotions ────────────────────────────────────────────────────────────
    @GET("promotions")
    suspend fun getPromotions(): ApiResponseDto<List<PromotionResponseDto>>

    // ── Vets ──────────────────────────────────────────────────────────────────
    @GET("vets")
    suspend fun getVets(
        @Query("city") city: String? = null,
        @Query("specialty") specialty: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): ApiResponseDto<List<VetResponseDto>>

    @GET("vets/{id}")
    suspend fun getVet(@Path("id") id: String): ApiResponseDto<VetResponseDto>

    @GET("vets/{vetId}/slots")
    suspend fun getVetSlots(
        @Path("vetId") vetId: String,
        @Query("from") from: String,
        @Query("to") to: String,
    ): ApiResponseDto<List<SlotResponseDto>>

    // ── Bookings ──────────────────────────────────────────────────────────────
    @GET("bookings")
    suspend fun getBookings(): ApiResponseDto<List<BookingResponseDto>>

    @POST("bookings")
    suspend fun createBooking(
        @Body body: CreateBookingRequestDto,
        @Header("Idempotency-Key") idempotencyKey: String,
    ): ApiResponseDto<BookingResponseDto>

    @PATCH("bookings/{id}/cancel")
    suspend fun cancelBooking(
        @Path("id") id: String,
        @Body body: CancelBookingRequestDto,
    ): ApiResponseDto<BookingResponseDto>

    // ── Shop — Products ───────────────────────────────────────────────────────
    @GET("categories")
    suspend fun getCategories(): ApiResponseDto<List<CategoryResponseDto>>

    @GET("products")
    suspend fun getProducts(
        @Query("categoryId") categoryId: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): ApiResponseDto<List<ProductResponseDto>>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: String): ApiResponseDto<ProductResponseDto>

    // ── Shop — Cart ───────────────────────────────────────────────────────────
    @GET("cart")
    suspend fun getCart(): ApiResponseDto<CartResponseDto>

    @POST("cart/items")
    suspend fun addToCart(@Body body: AddToCartRequestDto): ApiResponseDto<CartResponseDto>

    @PUT("cart/items/{id}")
    suspend fun updateCartItem(
        @Path("id") itemId: String,
        @Body body: UpdateCartItemRequestDto,
    ): ApiResponseDto<CartResponseDto>

    @DELETE("cart/items/{id}")
    suspend fun removeFromCart(@Path("id") itemId: String): ApiResponseDto<CartResponseDto>

    // ── Shop — Orders ─────────────────────────────────────────────────────────
    @POST("orders")
    suspend fun checkout(
        @Body body: CreateOrderRequestDto,
        @Header("Idempotency-Key") idempotencyKey: String,
    ): ApiResponseDto<OrderResponseDto>

    @GET("orders")
    suspend fun getOrders(): ApiResponseDto<List<OrderResponseDto>>

    @GET("orders/{orderId}")
    suspend fun getOrder(@Path("orderId") orderId: String): ApiResponseDto<OrderResponseDto>

    @POST("orders/{orderId}/reorder")
    suspend fun reorder(@Path("orderId") orderId: String): ApiResponseDto<OrderResponseDto>

    // ── Payments ─────────────────────────────────────────────────────────────
    @POST("payments/verify")
    suspend fun verifyPayment(@Body body: VerifyPaymentRequestDto): ApiResponseDto<OrderResponseDto>

    // ── Subscriptions ─────────────────────────────────────────────────────────
    @GET("subscriptions")
    suspend fun getSubscriptions(): ApiResponseDto<List<SubscriptionResponseDto>>

    @POST("subscriptions")
    suspend fun createSubscription(
        @Body body: CreateSubscriptionRequestDto,
        @Header("Idempotency-Key") idempotencyKey: String,
    ): ApiResponseDto<SubscriptionResponseDto>

    @PATCH("subscriptions/{id}/pause")
    suspend fun pauseSubscription(
        @Path("id") id: String,
        @Body body: PauseSubscriptionRequestDto,
    ): ApiResponseDto<SubscriptionResponseDto>

    @PATCH("subscriptions/{id}/resume")
    suspend fun resumeSubscription(
        @Path("id") id: String,
        @Body body: ResumeSubscriptionRequestDto,
    ): ApiResponseDto<SubscriptionResponseDto>

    @DELETE("subscriptions/{id}")
    suspend fun cancelSubscription(
        @Path("id") id: String,
        @Body body: CancelSubscriptionRequestDto,
    ): ApiResponseDto<Unit>

    // ── Inbox ─────────────────────────────────────────────────────────────────
    @GET("conversations")
    suspend fun getConversations(): ApiResponseDto<List<ConversationResponseDto>>

    @POST("conversations")
    suspend fun createConversation(@Body body: CreateConversationRequestDto): ApiResponseDto<ConversationResponseDto>

    @GET("conversations/{id}/messages")
    suspend fun getMessages(@Path("id") conversationId: String): ApiResponseDto<List<MessageResponseDto>>

    @POST("conversations/{id}/messages")
    suspend fun sendMessage(
        @Path("id") conversationId: String,
        @Body body: SendMessageRequestDto,
    ): ApiResponseDto<MessageResponseDto>

    @PATCH("conversations/{id}/read")
    suspend fun markRead(@Path("id") conversationId: String): ApiResponseDto<Unit>

    // ── Health — Pets ─────────────────────────────────────────────────────────
    @GET("pets")
    suspend fun getPets(): ApiResponseDto<List<PetResponseDto>>

    @POST("pets")
    suspend fun createPet(@Body body: CreatePetRequestDto): ApiResponseDto<PetResponseDto>

    @GET("pets/{petId}")
    suspend fun getPet(@Path("petId") petId: String): ApiResponseDto<PetResponseDto>

    @PATCH("pets/{petId}")
    suspend fun updatePet(
        @Path("petId") petId: String,
        @Body body: UpdatePetRequestDto,
    ): ApiResponseDto<PetResponseDto>

    @DELETE("pets/{petId}")
    suspend fun deletePet(@Path("petId") petId: String): ApiResponseDto<Unit>

    @GET("pets/{petId}/prescriptions")
    suspend fun getPrescriptions(@Path("petId") petId: String): ApiResponseDto<List<PrescriptionResponseDto>>

    @POST("prescriptions/{prescriptionId}/doses")
    suspend fun markDose(@Path("prescriptionId") prescriptionId: String): ApiResponseDto<Unit>

    @GET("pets/{petId}/weight-records")
    suspend fun getWeightLogs(@Path("petId") petId: String): ApiResponseDto<List<WeightLogResponseDto>>

    @POST("pets/{petId}/weight-records")
    suspend fun logWeight(
        @Path("petId") petId: String,
        @Body body: LogWeightRequestDto,
    ): ApiResponseDto<WeightLogResponseDto>

    @GET("pets/{petId}/vaccines")
    suspend fun getVaccines(
        @Path("petId") petId: String,
        @Query("status") status: String? = null,
    ): ApiResponseDto<List<VaccineResponseDto>>

    @POST("pets/{petId}/vaccines")
    suspend fun addVaccine(
        @Path("petId") petId: String,
        @Body body: CreateVaccineRequestDto,
    ): ApiResponseDto<VaccineResponseDto>

    // ── Notifications ─────────────────────────────────────────────────────────
    @GET("notifications")
    suspend fun getNotifications(): ApiResponseDto<List<NotificationResponseDto>>

    @PATCH("notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: String): ApiResponseDto<Unit>

    @GET("notifications/preferences")
    suspend fun getNotifPrefs(): ApiResponseDto<NotifPrefsResponseDto>

    @PUT("notifications/preferences")
    suspend fun updateNotifPrefs(@Body body: NotifPrefsRequestDto): ApiResponseDto<NotifPrefsResponseDto>

    @POST("notifications/push-token")
    suspend fun registerPushToken(@Body body: PushTokenRequestDto): ApiResponseDto<Unit>
}
