package com.example.middleprojectinter.view.addstory

import com.google.gson.annotations.SerializedName

class AddStoryResponse (
    @field:SerializedName("error")
    val error: Boolean = false,

    @field:SerializedName("message")
    val message: String = ""
)