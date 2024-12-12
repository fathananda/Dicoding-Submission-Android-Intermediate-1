package com.example.middleprojectinter.data


import com.example.middleprojectinter.view.detail.StoryItems
import com.google.gson.annotations.SerializedName

data class GetStoryResponse(

    @field:SerializedName("listStory")
    val listStory: List<StoryItems>? = null,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String? = null
)
data class GetDetailStoryResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("story")
    val story: StoryItems? = null
)