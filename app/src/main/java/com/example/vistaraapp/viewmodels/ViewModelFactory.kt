package com.example.vistaraapp.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vistaraapp.database.ContactViewModel
import com.example.vistaraapp.database.ContactDatabase

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            // Pulls the instance via your getDatabase function
            val database = ContactDatabase.getDatabase(application)
            // Passes database.dao (matching your exact 'abstract val dao: ContactDao' declaration)
            return ContactViewModel(database.dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}