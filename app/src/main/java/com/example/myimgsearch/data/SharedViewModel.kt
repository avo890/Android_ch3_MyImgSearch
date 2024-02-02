package com.example.myimgsearch.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {


    private val _likedDataList = MutableLiveData<Set<KakaoImageData>>(mutableSetOf())
    val likedDataList: LiveData<Set<KakaoImageData>> get() = _likedDataList

    private val _searchDataList: MutableLiveData<MutableList<KakaoImageData>> = MutableLiveData()
    val searchDataList: LiveData<MutableList<KakaoImageData>> get() = _searchDataList

    private val _checkDeletedUrls = MutableLiveData<List<String>?>(mutableListOf())
    val checkDeletedUrls : LiveData<List<String>?> get() = _checkDeletedUrls


    fun addDataList(dataList: MutableList<KakaoImageData>) {
        _searchDataList.value = dataList.toMutableList()
    }

    fun addFavorite(kakaoImageData: KakaoImageData) {
        _likedDataList.value = _likedDataList.value?.toMutableSet()?.apply {
            add(kakaoImageData)
        } ?: mutableSetOf()

        Log.d("뷰모델검사", "${_likedDataList.value}")
        Log.d("뷰모델사이즈검사", "${_likedDataList.value?.count()}")
    }


    fun removeFavorite(thumbnailUrl: String?) {
        _likedDataList.value = _likedDataList.value?.toMutableSet()?.apply {
            val removeItem = find { it.thumbnailUrl == thumbnailUrl }
            remove(removeItem)
        } ?: mutableSetOf()
        Log.d("리무브검사","${_likedDataList.value}")
        Log.d("리무브사이즈검사", "${_likedDataList.value?.count()}")

    }

    fun clearLikedDataList() {
        _likedDataList.value = emptySet()
    }

    fun addDeletedItemUrls(url: String) {
        val deletedLikeUrls = _checkDeletedUrls.value?.toMutableList() ?: mutableListOf()
        deletedLikeUrls.add(url)
        _checkDeletedUrls.value = deletedLikeUrls

    }

    fun clearCheckDeletedUrls() {
        _checkDeletedUrls.value = emptyList()
    }


}