package com.example.myimgsearch.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myimgsearch.data.KakaoImageData
import com.example.myimgsearch.data.RetrofitInstance
import com.example.myimgsearch.data.SharedViewModel
import com.example.myimgsearch.databinding.FragmentSearchBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.random.Random


class SearchFragment : Fragment() {
    private lateinit var adapter: SharedListAdapter
    private lateinit var searchWord: String
    private var dataList = mutableListOf<KakaoImageData>()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SharedListAdapter()
        binding.rvImgData.adapter = adapter
        binding.rvImgData.layoutManager = GridLayoutManager(context, 2)

        sharedViewModel.searchDataList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        loadSearchWord()
        checkDeletedUrls()
        searchClick()
        clickLiked()

    }


    private fun getSearchImg(searchWord: String, sharedPrefList: List<String>) {
        lifecycleScope.launch {
            val responseList = withContext(Dispatchers.IO) {
                RetrofitInstance.api.getImgData(query = searchWord).documents
            }

            for (data in responseList) {
                val thumbnailUrl = data.thumbnailUrl
                if (sharedPrefList.any { it.contains(thumbnailUrl) }) {
                    data.isliked = true
                }
            }

            dataList.clear()
            dataList.addAll(responseList)
            adapter.submitList(dataList)
        }

    }

    private fun searchClick() {
        binding.btnSearch.setOnClickListener {
            searchWord = binding.etSearch.text.toString()
            getSearchImg(searchWord, getSharedPref())
            sharedViewModel.addDataList(dataList)
            Log.d("검색어검사", "$searchWord")
            hideKeyboard()
            saveSearchWord(searchWord)
        }
    }


    private fun clickLiked() {
        adapter.itemClick = object : SharedListAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                if (!adapter.currentList[position].isliked) {
                    Log.d("서치프래그먼트클릭검사", "${adapter.currentList[position]}")
                    adapter.currentList[position].isliked = true
                    sharedViewModel.addFavorite(adapter.currentList[position])
                    saveSharedPref(position)
                } else {
                    adapter.currentList[position].isliked = false
                    sharedViewModel.addDeletedItemUrls(adapter.currentList[position].thumbnailUrl)
                    sharedViewModel.removeFavorite(adapter.currentList[position].thumbnailUrl)
                    removeSharedPref(adapter.currentList[position].thumbnailUrl)
                }
                adapter.notifyItemChanged(position)
            }
        }
    }

    private fun saveSharedPref(position: Int) {
        val randomNum = Random.nextInt(1, 101)
        val pref = requireContext().getSharedPreferences("favorite_prefs", 0)
        val editor = pref?.edit()
        val likedDataJson = Gson().toJson(dataList[position])
        editor?.putString("favorite_data_$position$randomNum", likedDataJson)
        editor?.apply()
    }

    private fun removeSharedPref(thumbnailUrl: String) {
        val pref = requireContext().getSharedPreferences("favorite_prefs", 0)
        val editor = pref.edit()
        val allData: Map<String, *> = pref.all
        for ((key, value) in allData) {
            if (value is String && value.contains(thumbnailUrl)) {
                editor.remove(key)
            }
        }
        editor.apply()
    }

    fun getSharedPref(): List<String> {
        val pref = requireContext().getSharedPreferences("favorite_prefs", 0)
        val allItems: Map<String, *> = pref.all
        val sharedPrefList = mutableListOf<String>()

        for ((_, value) in allItems) {
            if (value is String) {
                val imageData = Gson().fromJson(value, KakaoImageData::class.java)
                sharedPrefList.add(imageData.thumbnailUrl)
            }
        }
        return sharedPrefList
    }

    fun checkDeletedUrls() {
        sharedViewModel.checkDeletedUrls.observe(viewLifecycleOwner) {
            it?.forEach { url ->
                val targetItem = adapter.currentList.find { it.thumbnailUrl == url }

                targetItem?.let {
                    it.isliked = false
                    val itemIndex = adapter.currentList.indexOf(it)
                    adapter.notifyItemChanged(itemIndex)
                }
                sharedViewModel.clearCheckDeletedUrls()
            }
        }

    }

    private fun saveSearchWord(searchWord: String) {
        val pref = requireContext().getSharedPreferences("pref", 0)
        val edit = pref.edit()
        edit.putString("lastSearchWord", searchWord)
        edit.apply()
    }

    private fun loadSearchWord() {
        val pref = requireContext().getSharedPreferences("pref", 0)
        binding.etSearch.setText(pref.getString("lastSearchWord", ""))
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}