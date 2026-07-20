package com.example.vistaraapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistaraapp.api.RetrofitClient
import com.example.vistaraapp.data.SessionManager
import com.example.vistaraapp.repositories.AlertsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertsViewModel(
    private val alertsRepository: AlertsRepository = AlertsRepository(RetrofitClient.bookingInstance)
) : ViewModel() {

    // Alerts state
    private val _alerts = MutableStateFlow<List<RangerAlert>>(emptyList())
    val alerts: StateFlow<List<RangerAlert>> = _alerts.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error message state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Stats state
    private val _stats = MutableStateFlow<AlertStatistics?>(null)
    val stats: StateFlow<AlertStatistics?> = _stats.asStateFlow()

    fun clearError() {
        _errorMessage.value = null
    }

    // Fetch ALL alerts (all distresses plus assigned rangers)
    fun loadAllAlerts(token: String) {
        Log.d("AlertsViewModel", "loadAllAlerts called")
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = alertsRepository.getAllAlerts(token)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        val alertsList = body.data?.map { it.toRangerAlert() } ?: emptyList()
                        Log.d("AlertsViewModel", "loadAllAlerts success: ${alertsList.size} alerts found")
                        _alerts.value = alertsList
                    } else {
                        _alerts.value = emptyList()
                        _errorMessage.value = body.message ?: "Failed to load all alerts"
                        Log.e("AlertsViewModel", "loadAllAlerts success=false: ${body.message}")
                    }
                } else {
                    _alerts.value = emptyList()
                    _errorMessage.value = "Server Error ${response.code()}: ${response.message()}"
                    Log.e("AlertsViewModel", "loadAllAlerts failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _alerts.value = emptyList()
                _errorMessage.value = e.localizedMessage ?: "Network Error"
                Log.e("AlertsViewModel", "Network Error in loadAllAlerts", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fetch all alerts assigned to the ranger
    fun loadAssignedAlerts(token: String) {
        Log.d("AlertsViewModel", "loadAssignedAlerts called")
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = alertsRepository.getAssignedAlerts(token)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        val alertsList = body.data ?: emptyList()
                        Log.d("AlertsViewModel", "loadAssignedAlerts success: ${alertsList.size} alerts found")
                        _alerts.value = alertsList
                    } else {
                        _alerts.value = emptyList()
                        _errorMessage.value = body.message ?: "Failed to load assigned alerts"
                        Log.e("AlertsViewModel", "getAssignedAlerts success=false: ${body.message}")
                    }
                } else {
                    _alerts.value = emptyList()
                    _errorMessage.value = "Server Error ${response.code()}: ${response.message()}"
                    Log.e("AlertsViewModel", "getAssignedAlerts failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _alerts.value = emptyList()
                _errorMessage.value = e.localizedMessage ?: "Network Error"
                Log.e("AlertsViewModel", "Network Error in loadAssignedAlerts", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Refresh alerts using the saved token
    fun refreshAlerts(sessionManager: SessionManager) {
        viewModelScope.launch {
            val token = sessionManager.getToken()
            if (!token.isNullOrEmpty()) {
                loadAllAlerts(token)
                loadRangerStats(token)
            }
        }
    }

    // Fetch statistics for ranger alerts
    fun loadRangerStats(token: String) {
        Log.d("AlertsViewModel", "loadRangerStats called")
        viewModelScope.launch {
            try {
                val response = alertsRepository.getRangerStats(token)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        val statsData = body.data
                        Log.d("AlertsViewModel", "loadRangerStats success: $statsData")
                        _stats.value = statsData
                    } else {
                        Log.e("AlertsViewModel", "Failed to fetch stats: ${body.message}")
                    }
                } else {
                    Log.e("AlertsViewModel", "Failed to fetch stats: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AlertsViewModel", "Failed to fetch stats", e)
            }
        }
    }

    // Fetch alerts by status
    fun fetchAlertsByStatus(
        token: String,
        status: String
    ) {
        Log.d("AlertsViewModel", "fetchAlertsByStatus called for status: $status")
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = alertsRepository.getAlertsByStatus(token, status)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        val alertsList = body.data ?: emptyList()
                        Log.d("AlertsViewModel", "fetchAlertsByStatus success: ${alertsList.size} alerts found")
                        _alerts.value = alertsList
                    } else {
                        _alerts.value = emptyList()
                        _errorMessage.value = body.message ?: "Failed to load alerts for status: $status"
                        Log.e("AlertsViewModel", "fetchAlertsByStatus success=false: ${body.message}")
                    }
                } else {
                    _alerts.value = emptyList()
                    _errorMessage.value = "Server Error ${response.code()}: ${response.message()}"
                    Log.e("AlertsViewModel", "getAlertsByStatus failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _alerts.value = emptyList()
                _errorMessage.value = e.localizedMessage ?: "Network Error"
                Log.e("AlertsViewModel", "Network Error in fetchAlertsByStatus", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fetch all unassigned pending emergency alerts
    fun fetchPendingEmergencies(token: String) {
        Log.d("AlertsViewModel", "fetchPendingEmergencies called")
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = alertsRepository.getPendingEmergencies(token)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        val alertsList = body.data ?: emptyList()
                        Log.d("AlertsViewModel", "fetchPendingEmergencies success: ${alertsList.size} alerts")
                        _alerts.value = alertsList
                    } else {
                        _alerts.value = emptyList()
                        _errorMessage.value = body.message ?: "Failed to load pending emergencies"
                        Log.e("AlertsViewModel", "fetchPendingEmergencies success=false: ${body.message}")
                    }
                } else {
                    _alerts.value = emptyList()
                    _errorMessage.value = "Server Error ${response.code()}: ${response.message()}"
                    Log.e("AlertsViewModel", "fetchPendingEmergencies failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _alerts.value = emptyList()
                _errorMessage.value = e.localizedMessage ?: "Network Error"
                Log.e("AlertsViewModel", "fetchPendingEmergencies network error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Claim an alert
    fun claimAlert(token: String, alertId: Long, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = alertsRepository.assignToMe(token, alertId)
                if (response.success) {
                    Log.d("AlertsViewModel", "Claim alert success: ${response.message}")
                    onSuccess()
                } else {
                    _errorMessage.value = response.message ?: "Failed to claim alert"
                    Log.e("AlertsViewModel", "Claim alert failed: ${response.message}")
                }
            } catch (e: Exception) {
                if (e is retrofit2.HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    _errorMessage.value = "HTTP ${e.code()}: $errorBody"
                    Log.e("AlertsViewModel", "Claim alert failed with HTTP ${e.code()}: $errorBody", e)
                } else {
                    _errorMessage.value = e.localizedMessage ?: "Failed to claim alert"
                    Log.e("AlertsViewModel", "Claim alert failed with exception", e)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Resolve an alert
    fun resolveAlert(token: String, alertId: Long, notes: String?, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = alertsRepository.resolveAlert(token, alertId, notes)
                if (response.success) {
                    Log.d("AlertsViewModel", "Resolve alert success: ${response.message}")
                    onSuccess()
                } else {
                    _errorMessage.value = response.message ?: "Failed to resolve alert"
                    Log.e("AlertsViewModel", "Resolve alert failed: ${response.message}")
                }
            } catch (e: Exception) {
                if (e is retrofit2.HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    _errorMessage.value = "HTTP ${e.code()}: $errorBody"
                    Log.e("AlertsViewModel", "Resolve alert failed with HTTP ${e.code()}: $errorBody", e)
                } else {
                    _errorMessage.value = e.localizedMessage ?: "Failed to resolve alert"
                    Log.e("AlertsViewModel", "Resolve alert failed with exception", e)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}
