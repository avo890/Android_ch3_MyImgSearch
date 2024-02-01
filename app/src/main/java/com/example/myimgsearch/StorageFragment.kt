package com.example.myimgsearch

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myimgsearch.databinding.FragmentStorageBinding
import com.google.gson.Gson

class StorageFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var adapter: SharedListAdapter

    private var _binding: FragmentStorageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStorageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SharedListAdapter()
        binding.rvLikedData.adapter = adapter
        binding.rvLikedData.layoutManager = GridLayoutManager(context, 2)

        val pref = requireContext().getSharedPreferences("favorite_prefs", 0)
        val allKeys = pref.all.keys

        for (key in allKeys) {
            val favoriteItemsJson = pref.getString(key, "")
            if (favoriteItemsJson != null) {
                val favoriteItems = Gson().fromJson(favoriteItemsJson, KakaoImageData::class.java)
                sharedViewModel.addFavorite(favoriteItems)
            }
        }

        sharedViewModel.likedDataList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it.toList())
        })

        adapter.itemClick = object : SharedListAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {

                if (position < adapter.currentList.size) {
                    adapter.currentList[position].isliked = false
                    sharedViewModel.removeFavorite(adapter.currentList[position])
                    removeFavorite(adapter.currentList[position].thumbnailUrl)
                }

                Log.d("보관함검사", "${adapter.currentList}")
                Log.d("보관함인덱스검사", "${adapter.currentList.size},$position")

            }
        }

        binding.btnStorageClear.setOnClickListener {
            removeAllFavorite()
            sharedViewModel.clearLikedDataList()
        }

    }

    private fun removeAllFavorite() {
        val pref = requireContext().getSharedPreferences("favorite_prefs", 0)
        val editor = pref.edit()
        editor.clear()
        editor.apply()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}