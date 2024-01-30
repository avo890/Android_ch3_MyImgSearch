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

class StorageFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var adapter: SharedAdapter

    private var _binding: FragmentStorageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStorageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val likedData = sharedViewModel.getFilterDataList()
        adapter = SharedAdapter(likedData ?: mutableListOf())
        binding.rvLikedData.adapter = adapter
        binding.rvLikedData.layoutManager = GridLayoutManager(context, 2)


        sharedViewModel.likedDataList.observe(viewLifecycleOwner, Observer {
            adapter.setData(it.toMutableList())
        })

        adapter.itemClick = object : SharedAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {

                if (position < likedData.size) { // 인덱스가 유효한 범위 내에 있는지 확인
                    likedData[position].isliked = false
                    likedData.removeAt(position)
                    sharedViewModel.filterDataList(likedData)
                    adapter.notifyDataSetChanged()
                }
                Log.d("보관함인덱스검사", "${likedData.size},$position")


//                likedData[position].isliked = false
//                likedData.remove(likedData[position])
//                sharedViewModel.filterDataList(likedData)
//                adapter.notifyItemChanged(position)
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}