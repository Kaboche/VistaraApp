package com.example.vistaraapp.api

import com.example.vistaraapp.Api_requests_responses.MpesaPushRequest
import com.example.vistaraapp.Api_requests_responses.MpesaPushResponse
import com.example.vistaraapp.ProfileNetworkRequest
import com.example.vistaraapp.api_requests_responses.BookingsResponse
import com.example.vistaraapp.database.ContactEvent
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import com.google.gson.annotations.SerializedName

// 1. REQUEST MODEL FOR BOOKING PAYLOAD
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

//  2. BOOKING ENDPOINTS SERVICE
interface ApiService {

    @Headers("ngrok-skip-browser-warning: true")
    @GET("bookings")
    suspend fun getBookings(
        @Header("Authorization") token: String
    ): BookingsResponse

    // Cleanly added the POST booking call here with its required @Body payload
    @Headers("ngrok-skip-browser-warning: true")
    @POST("bookings")
    suspend fun proceedToPayment(
        @Header("Authorization") bearerToken: String,
        @Body bookingData: BookingRequest // 👈 This passes your JSON form body to ngrok
    ): Response<BookingCreationResponse>
}

// 3. PROFILE ENDPOINTS SERVICE
interface ProfileApiService {

    // UPDATE PROFILE DATA (PUT)
    @PUT("profile")
    suspend fun saveProfileDetails(
        @Header("Authorization") bearerToken: String,
        @Body profileData: ProfileNetworkRequest,
        @Header("ngrok-skip-browser-warning") skip: String = "true"
    ): Response<ResponseBody>

    // FETCH PROFILE DATA (GET)
    @GET("profile")
    suspend fun getProfileDetails(
        @Header("Authorization") bearerToken: String,
        @Header("ngrok-skip-browser-warning") skip: String = "true"
    ): Response<Map<String, Any>>

}
//Mpesa STK push
interface  VistaraApi{
    @Headers("ngrok-skip-browser-warning: true")
    @POST("payments/mpesa/stkpush")
    suspend fun  initiateStkPush(
        @Header("Authorization") bearerToken: String,
        @Body request: MpesaPushRequest//@Body is the information one sends to the server
    ): Response<MpesaPushResponse>
}

