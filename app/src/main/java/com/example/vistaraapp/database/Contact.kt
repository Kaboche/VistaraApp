package com.example.vistaraapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Int?=null,
    val fullName: String,
    val email: String,
    val phoneNumber:String,
    val password: String,
    val idNumber: String,
    val emergencyNumber: String,


    )