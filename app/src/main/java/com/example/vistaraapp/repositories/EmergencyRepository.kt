package com.example.vistaraapp.repositories

import com.example.vistaraapp.api.ApiService
import com.example.vistaraapp.api_requests_responses.SosRequest
import com.example.vistaraapp.api_requests_responses.SosResponse
import retrofit2.Response

class EmergencyRepository(private val apiService: ApiService) {

    suspend fun sendSosAlert(token: String, lat: Double, lon: Double, alertType: String = "Distress"): Response<SosResponse> {
        val requestBody = SosRequest(latitude = lat, longitude = lon, alertType = alertType)
        return apiService.triggerSos(token, requestBody)
    }
}