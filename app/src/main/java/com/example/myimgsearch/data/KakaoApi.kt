package com.example.myimgsearch.data

import com.example.myimgsearch.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoApi {
    @GET("v2/search/image")
    suspend fun getImgData(
        @Header("Authorization") apiKey: String = "KakaoAK ${BuildConfig.KAKAOAPI}",
        @Query("query") query: String
    ) : ImageResponse

}