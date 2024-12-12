package com.example.middleprojectinter.view.signup

import com.google.gson.annotations.SerializedName

class PayloadRegister(
    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("password")
    val password: String
)