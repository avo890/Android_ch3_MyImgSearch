package com.example.myimgsearch

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myimgsearch.databinding.FragmentSearchBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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


        loadSearchWord()

        sharedViewModel.searchDataList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })


        searchClick()
        clickLiked()

    }


    private fun getSearchImg(searchWord: String) {
        lifecycleScope.launch {
            val responseList = withContext(Dispatchers.IO) {
                RetrofitInstance.api.getImgData(query = searchWord).documents
            }
            dataList.clear()
            dataList.addAll(responseList)
            adapter.submitList(dataList)
        }

    }

    private fun searchClick() {
        binding.btnSearch.setOnClickListener {
            searchWord = binding.etSearch.text.toString()
            getSearchImg(searchWord)
            sharedViewModel.addDataList(dataList)
            Log.d("검색어검사", "$searchWord")
            hideKeyboard()
            saveSearchWord(searchWord)
        }

    }


//    private fun clickLiked() {
//        adapter.itemClick = object : SharedListAdapter.ItemClick {
//            override fun onClick(view: View, position: Int) {
//                if (!dataList[position].isliked) {
//                    Log.d("서치프래그먼트클릭검사","${dataList[position]}")
//                    sharedViewModel.addFavorite(dataList[position])
//                    dataList[position].isliked = true
//                    saveFavorite()
//                } else {
//                    dataList[position].isliked = false
//                    sharedViewModel.removeFavorite(dataList[position])
//                }
//                adapter.notifyItemChanged(position)
//            }
//        }
//    }

    private fun clickLiked() {
        adapter.itemClick = object : SharedListAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                if (!dataList[position].isliked) {
                    Log.d("서치프래그먼트클릭검사","${dataList[position]}")
                    dataList[position].isliked = true
                    sharedViewModel.addFavorite(dataList[position])
                    saveFavorite(position)
                } else {
                    dataList[position].isliked = false
                    sharedViewModel.removeFavorite(dataList[position])
                    removeFavorite(dataList[position].thumbnailUrl)
                }
                adapter.notifyItemChanged(position)
            }
        }
    }

    private fun saveFavorite(position: Int) {
        val pref = requireContext().getSharedPreferences("favorite_prefs", 0)
        val editor = pref?.edit()
        val likedDataJson = Gson().toJson(dataList[position])
        editor?.putString("FavoriteData$position", likedDataJson)
        editor?.apply()
    }

    private fun removeFavorite(thumbnailUrl: String) {
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