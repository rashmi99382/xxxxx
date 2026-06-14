package com.example.pharmacyinventory.subscription

import com.example.pharmacyinventory.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface SubscriptionApiService {
    @POST("auth/register")
    suspend fun registerShop(@Body request: RegisterShopRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") auth: String): Response<Unit>

    @GET("subscription/status")
    suspend fun subscriptionStatus(@Header("Authorization") auth: String): Response<SubscriptionStatusResponse>

    @POST("payments/create-subscription")
    suspend fun createSubscription(
        @Header("Authorization") auth: String,
        @Body request: CreateSubscriptionRequest
    ): Response<PaymentStartResponse>

    @POST("payments/create-subscription")
    suspend fun renewSubscription(
        @Header("Authorization") auth: String,
        @Body request: CreateSubscriptionRequest
    ): Response<PaymentStartResponse>

    @POST("subscription/cancel")
    suspend fun cancelSubscription(@Header("Authorization") auth: String): Response<Unit>

    @POST("devices/register")
    suspend fun registerDevice(
        @Header("Authorization") auth: String,
        @Body request: DeviceRegisterRequest
    ): Response<SubscriptionStatusResponse>

    @GET("devices")
    suspend fun devices(@Header("Authorization") auth: String): Response<DeviceListResponse>

    @DELETE("devices/{deviceId}")
    suspend fun deleteDevice(
        @Header("Authorization") auth: String,
        @Path("deviceId") deviceId: String
    ): Response<Unit>

    @POST("admin/login")
    suspend fun adminLogin(@Body request: AdminLoginRequest): Response<AuthResponse>

    @GET("admin/dashboard")
    suspend fun adminDashboard(@Header("Authorization") auth: String): Response<AdminDashboardResponse>
}

object SubscriptionApiClient {
    fun create(cache: SubscriptionCache): SubscriptionApiService {
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val request = if (original.header("Authorization").isNullOrBlank()) {
                val token = cache.current().accessToken
                if (token.isNullOrBlank()) original else original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                original
            }
            chain.proceed(request)
        }
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
            redactHeader("Authorization")
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logger)
            .build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SubscriptionApiService::class.java)
    }
}

open class ApiException(message: String, val statusCode: Int? = null) : IllegalStateException(message)

class UnauthorizedException(message: String) : ApiException(message, 401)

suspend fun <T> Response<T>.bodyOrError(fallback: String): T {
    if (isSuccessful) return body() ?: throw ApiException(fallback)
    val message = errorBody()?.string()?.take(180).orEmpty().ifBlank { fallback }
    if (code() == 401) throw UnauthorizedException("Session expired. Please login again.")
    throw ApiException(message, code())
}
