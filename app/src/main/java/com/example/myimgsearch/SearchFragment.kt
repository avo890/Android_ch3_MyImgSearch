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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myimgsearch.databinding.FragmentSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchFragment : Fragment() {
    private lateinit var adapter: SharedAdapter
    private lateinit var searchWord : String
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private var dataList = mutableListOf<KakaoImageData>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SharedAdapter(dataList)
        binding.rvImgData.adapter = adapter
        binding.rvImgData.layoutManager = GridLayoutManager(context, 2)

        loadSearchWord()

        sharedViewModel.likedDataList.observe(viewLifecycleOwner, Observer {
            adapter.setData(dataList)
        })

        binding.btnSearch.setOnClickListener {
            searchWord = binding.etSearch.text.toString()
            getSearchImg(searchWord)
            Log.d("검색어검사","$searchWord")
            hideKeyboard()
            saveSearchWord(searchWord)
        }


        adapter.itemClick = object : SharedAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val data = dataList.getOrNull(position) ?: return
                data.isliked = !data.isliked
                sharedViewModel.filterDataList(dataList)
                adapter.notifyItemChanged(position)


            }
        }


    }


    private fun getSearchImg(searchWord: String) {
        RetrofitInstance.api.getImgData(query = searchWord).enqueue(object : Callback<ImageResponse> {
            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                Log.d("레트로핏검색어검사", searchWord)
                val body = response.body()
                body?.let {
                    dataList.clear()
                    dataList.addAll(it.documents)
                    adapter.notifyDataSetChanged()
                }
                Log.d("api검사", "$dataList")
            }

            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                Log.d("api검사", "네트워크오류/데이터변환오류.")
            }

        })
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