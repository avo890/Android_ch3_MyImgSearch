package com.example.myimgsearch

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class KakaoImageData(
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String,
    @SerializedName("display_sitename")
    val siteName: String,
    val datetime: String,
    var isliked: Boolean = false
)

data class ImageResponse(
    val documents: List<KakaoImageData>
)
