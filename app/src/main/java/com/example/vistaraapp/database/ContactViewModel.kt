package com.example.vistaraapp.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistaraapp.api.BookingRequest
import com.example.vistaraapp.api.MpesaPushRequest
import com.example.vistaraapp.api.RetrofitClient
import com.example.vistaraapp.utils.TokenManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class ContactViewModel(
    private val dao: ContactDao
) : ViewModel() {

    private val _sortType = MutableStateFlow(SortType.FULL_NAME)
    private val _state = MutableStateFlow(ContactState())

    private val _contacts = _sortType
        .flatMapLatest { sortType ->
            when (sortType) {
                SortType.FULL_NAME -> dao.getContactsOrderedByFullName()
                SortType.EMAIL -> dao.getContactsOrderedByEmail()
                SortType.PHONE_NUMBER -> dao.getContactsOrderedByPhoneNumber()
                SortType.ID_NUMBER -> dao.getContactsOrderedByIdNumber()
                SortType.EMERGENCY_NUMBER -> dao.getContactsOrderedByEmergencyNumber()
                SortType.PASSWORD -> dao.getContactsOrderedByFullName()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _userProfile = dao.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val state = combine(
        _state,
        _sortType,
        _contacts,
        _userProfile
    ) { state, sortType, contacts, userProfile ->
        state.copy(
            contacts = contacts,
            sortType = sortType,
            userProfile = userProfile
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState())

    init {
        viewModelScope.launch {
            dao.getUserProfile().collect { profile ->
                if (profile != null) {
                    _state.update {
                        it.copy(
                            fullName = profile.fullName,
                            email = profile.email,
                            phoneNumber = profile.phoneNumber,
                            password = profile.password,
                            idNumber = profile.idNumber,
                            emergencyNumber = profile.emergencyNumber
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: ContactEvent) {
        when (event) {
            is ContactEvent.DeleteContact -> {
                viewModelScope.launch {
                    dao.deleteContact(event.contact)
                }
            }
            ContactEvent.HideDialog -> {
                _state.update { it.copy(isAddingContact = false) }
            }
            ContactEvent.ShowDialog -> {
                _state.update { it.copy(isAddingContact = true) }
            }
            is ContactEvent.SortContacts -> {
                _sortType.value = event.sortType
            }
            is ContactEvent.SetFullName -> {
                _state.update { it.copy(fullName = event.fullName) }
            }
            is ContactEvent.SetEmail -> {
                _state.update { it.copy(email = event.email) }
            }
            is ContactEvent.SetPhoneNumber -> {
                _state.update { it.copy(phoneNumber = event.phoneNumber) }
            }
            is ContactEvent.SetPassword -> {
                _state.update { it.copy(password = event.password) }
            }
            is ContactEvent.SetIdNumber -> {
                _state.update { it.copy(idNumber = event.idNumber) }
            }
            is ContactEvent.SetEmergencyNumber -> {
                _state.update { it.copy(emergencyNumber = event.emergencyNumber) }
            }
            ContactEvent.SaveContact -> {
                val fullName = _state.value.fullName
                val email = _state.value.email
                val phoneNumber = _state.value.phoneNumber
                val password = _state.value.password
                val idNumber = _state.value.idNumber
                val emergencyNumber = _state.value.emergencyNumber

                if (fullName.isBlank() || phoneNumber.isBlank() || emergencyNumber.isBlank()) {
                    return
                }

                val sanitizedPhone = phoneNumber.replace(Regex("[^0-9]"), "")
                val sanitizedEmergencyPhone = emergencyNumber.replace(Regex("[^0-9]"), "")

                val contact = Contact(
                    id = 1,
                    fullName = fullName,
                    email = email,
                    phoneNumber = sanitizedPhone,
                    password = password,
                    idNumber = idNumber,
                    emergencyNumber = sanitizedEmergencyPhone,
                    isCurrentUser = true
                )

                viewModelScope.launch {
                    dao.upsertContact(contact)
                }

                _state.update {
                    it.copy(isAddingContact = false)
                }
            }
            ContactEvent.PrepareEditProfile -> {
                viewModelScope.launch {
                    try {
                        val profile = dao.getUserProfile().first()
                            ?: dao.getContactsOrderedByFullName().first().firstOrNull()
                        if (profile != null) {
                            _state.update {
                                it.copy(
                                    fullName = profile.fullName,
                                    email = profile.email,
                                    phoneNumber = profile.phoneNumber,
                                    password = profile.password,
                                    idNumber = profile.idNumber,
                                    emergencyNumber = profile.emergencyNumber
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // BOOKING FORM EVENTS
            is ContactEvent.EnteredCheckInDate -> {
                _state.update { it.copy(checkInDate = event.checkInDate) }
            }
            is ContactEvent.EnteredCheckOutDate -> {
                _state.update { it.copy(checkOutDate = event.checkOutDate) }
            }
            is ContactEvent.EnteredGroupSize -> {
                _state.update { it.copy(groupSize = event.groupSize) }
            }
            is ContactEvent.EnteredVehicleRegistration -> {
                _state.update { it.copy(vehicleRegistration = event.vehicleRegistration) }
            }
            is ContactEvent.EnteredPaymentMethod -> {
                _state.update { it.copy(paymentMethod = event.paymentMethod) }
            }
            is ContactEvent.EnteredAmount -> {
                _state.update { it.copy(amount = event.amount) }
            }
            is ContactEvent.EnteredPhoneNumber -> {
                _state.update { it.copy(phoneNumber = event.phoneNumber) }
            }

            ContactEvent.DismissPaymentDialog -> {
                _state.update { it.copy(showPaymentDialog = false) }
            }

            is ContactEvent.CreateBooking -> {
                viewModelScope.launch {
                    _state.update { it.copy(isBookingLoading = true, bookingErrorMessage = null) }
                    try {
                        val current = _state.value
                        val token = TokenManager.getToken() ?: ""
                        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

                        val request = BookingRequest(
                            checkInDate = current.checkInDate,
                            checkOutDate = current.checkOutDate,
                            groupSize = current.groupSize,
                            vehicleRegistration = current.vehicleRegistration,
                            paymentMethod = current.paymentMethod,
                            amount = current.amount
                        )

                        // Step 1: Register the core booking record in your system and get the booking reference
                        val response = RetrofitClient.bookingInstance.proceedToPayment(bearerToken, request)

                        if (response.isSuccessful) {
                            val bookingRef = response.body()?.bookingReference ?: run {
                                val timestamp = SimpleDateFormat(
                                    "yyyyMMdd-HHmm",
                                    Locale.getDefault()
                                ).format(Date())
                                "VST-$timestamp"
                            }

                            _state.update {
                                it.copy(
                                    isBookingLoading = false,
                                    bookingReference = bookingRef,
                                    showPaymentDialog = true
                                )
                            }
                        } else {
                            // Booking registration failed early
                            _state.update {
                                it.copy(
                                    isBookingLoading = false,
                                    bookingErrorMessage = "Booking validation failed: Server code ${response.code()}"
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _state.update {
                            it.copy(
                                isBookingLoading = false,
                                bookingErrorMessage = e.localizedMessage ?: "An unexpected connection error occurred"
                            )
                        }
                    }
                }
            }

            is ContactEvent.ConfirmBookingPayment -> {
                viewModelScope.launch {
                    _state.update { it.copy(isBookingLoading = true, bookingErrorMessage = null) }
                    try {
                        val current = _state.value
                        val token = TokenManager.getToken() ?: ""
                        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

                        var formattedPhone = event.phoneNumber.replace(Regex("[^0-9]"), "")
                        if (formattedPhone.startsWith("0")) {
                            formattedPhone = "254" + formattedPhone.substring(1)
                        } else if (formattedPhone.startsWith("+254")) {
                            formattedPhone = formattedPhone.substring(1)
                        } else if (!formattedPhone.startsWith("254")) {
                            formattedPhone = "254$formattedPhone"
                        }

                        // Step 2: Use the booking reference to initiate the M-Pesa STK Push
                        val mpesaPayload = MpesaPushRequest(
                            amount = current.amount.coerceAtLeast(10.0),
                            phoneNumber = formattedPhone,
                            bookingReference = current.bookingReference ?: "VST-Fallback",
                            accountReference = current.bookingReference ?: "VST-Fallback",
                            transactionDesc = "Booking Payment"
                        )

                        // Pointing to the mpesa api reference wrapper service instance inside RetrofitClient
                        val mpesaResponse = RetrofitClient.mpesaInstance.initiateStkPush(bearerToken, mpesaPayload)

                        if (mpesaResponse.isSuccessful && mpesaResponse.body()?.success == true) {
                            // Daraja request creation completed perfectly!
                            _state.update {
                                it.copy(isBookingLoading = false, isBookingSuccessful = true, showPaymentDialog = false)
                            }
                        } else {
                            // M-Pesa API rejected details (e.g. invalid safaricom number format)
                            val mpesaErrorReason = mpesaResponse.body()?.message ?: "M-Pesa validation rejection."
                            _state.update {
                                it.copy(
                                    isBookingLoading = false,
                                    bookingErrorMessage = "Payment initialization failed: $mpesaErrorReason"
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _state.update {
                            it.copy(
                                isBookingLoading = false,
                                bookingErrorMessage = e.localizedMessage ?: "An unexpected connection error occurred"
                            )
                        }
                    }
                }
            }
        }
    }
}