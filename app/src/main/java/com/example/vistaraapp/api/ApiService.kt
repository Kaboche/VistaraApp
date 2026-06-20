package com.example.vistaraapp.api

import com.example.vistaraapp.api_requests_responses.SosRequest
import com.example.vistaraapp.api_requests_responses.SosResponse
import com.example.vistaraapp.api_requests_responses.BookingsResponse
import com.example.vistaraapp.ProfileNetworkRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PUT


// 1. DATA MODELS FOR BOOKINGS & PAYMENTS

data class BookingRequest(
    val checkInDate: String,
    val checkOutDate: String,
    val groupSize: Int,
    val vehicleRegistration: String,
    val paymentMethod: String,
    val amount: Double
)

data class BookingCreationResponse(
    val bookingReference: String,
    val success: Boolean?,
    val message: String?
)

//Mpesa
data class MpesaPushRequest(
    val amount: Double,
    val phoneNumber: String,
    val bookingReference: String,
    val accountReference: String,
    val transactionDesc: String
)

data class MpesaPushResponse(
    val success: Boolean,
    val message: String,
    val checkoutRequestID: String?
)

// 2. BOOKING & EMERGENCY ENDPOINTS SERVICE
interface ApiService {

    @Headers("ngrok-skip-browser-warning: true")
    @GET("bookings")
    suspend fun getBookings(
        @Header("Authorization") token: String
    ): BookingsResponse

    @Headers("ngrok-skip-browser-warning: true")
    @POST("bookings")
    suspend fun proceedToPayment(
        @Header("Authorization") bearerToken: String,
        @Body bookingData: BookingRequest
    ): Response<BookingCreationResponse>

    @Headers("ngrok-skip-browser-warning: true")
    @POST("bookings/{bookingId}/cancel")
    suspend fun cancelBooking(
        @Header("Authorization") bearerToken: String,
        @Path("bookingId") bookingId: String
    ): Response<BookingCreationResponse>

    @Headers("ngrok-skip-browser-warning: true")
    @POST("emergency/sos")
    suspend fun triggerSos(
        @Header("Authorization") bearerToken: String,
        @Body sosData: SosRequest
    ): Response<SosResponse>
}

// 3. PROFILE ENDPOINTS SERVICE
interface ProfileApiService {

    @PUT("profile")
    suspend fun saveProfileDetails(
        @Header("Authorization") bearerToken: String,
        @Body profileData: ProfileNetworkRequest,
        @Header("ngrok-skip-browser-warning") skip: String = "true"
    ): Response<ResponseBody>

    @GET("profile")
    suspend fun getProfileDetails(
        @Header("Authorization") bearerToken: String,
        @Header("ngrok-skip-browser-warning") skip: String = "true"
    ): Response<Map<String, Any>>
}


// 4. M-PESA STK PUSH SERVICE
interface VistaraApi {
    @Headers("ngrok-skip-browser-warning: true")
    @POST("payments/mpesa/stkpush")
    suspend fun initiateStkPush(
        @Header("Authorization") bearerToken: String,
        @Body request: MpesaPushRequest
    ): Response<MpesaPushResponse>
}