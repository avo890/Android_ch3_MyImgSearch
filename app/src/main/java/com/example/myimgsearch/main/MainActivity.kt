package com.example.myimgsearch.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.myimgsearch.ui.SearchFragment
import com.example.myimgsearch.ui.StorageFragment
import com.example.myimgsearch.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViewPager()
    }

    private fun initViewPager() {
        val tabTextList = listOf("이미지 검색", "내 보관함")

        var viewPager2Adapter = ViewPager2Adapter(this)
        viewPager2Adapter.addFragment(SearchFragment())
        viewPager2Adapter.addFragment(StorageFragment())

        binding.mainViewPager.apply {
            adapter = viewPager2Adapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){})
        }

        TabLayoutMediator(binding.mainTab, binding.mainViewPager) { tab, position ->
            tab.text = tabTextList[position]
        }.attach()

    }
}