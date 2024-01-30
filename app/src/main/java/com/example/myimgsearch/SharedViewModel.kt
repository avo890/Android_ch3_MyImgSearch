package com.example.myimgsearch

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
   // val likedDataList = MutableLiveData<List<KakaoImageData>>()

    private val _likedDataList = MutableLiveData<List<KakaoImageData>>()
    val likedDataList: LiveData<List<KakaoImageData>> get() = _likedDataList

    fun filterDataList(dataList: MutableList<KakaoImageData>) {
        val likedList = dataList.filter { it.isliked }
        _likedDataList.value = likedList.toMutableList()
        Log.d("뷰모델검사","${likedDataList.value}")
        Log.d("뷰모델사이즈검사","${likedDataList.value?.count()}")
    }

    fun getFilterDataList(): MutableList<KakaoImageData> = likedDataList.value?.toMutableList() ?: mutableListOf()

}