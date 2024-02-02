package com.example.myimgsearch.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myimgsearch.R
import com.example.myimgsearch.data.KakaoImageData
import com.example.myimgsearch.databinding.LayoutItemBinding
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class SharedAdapter(var kakaoItemData: MutableList<KakaoImageData>) : RecyclerView.Adapter<SharedAdapter.Holder>() {

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    inner class Holder(binding: LayoutItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivItemImg = binding.ivItemImg
        val tvSiteName = binding.tvSiteName
        val tvdateTime = binding.tvDateTime
        val ivLiked = binding.ivLiked

        fun bind(data: KakaoImageData) {
            Glide.with(itemView.context).load(data.thumbnailUrl).into(ivItemImg)
            tvSiteName.text = data.siteName
            tvdateTime.text = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(OffsetDateTime.parse(data.datetime))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = LayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int = kakaoItemData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = kakaoItemData[position]
        holder.bind(item)

        if (kakaoItemData[position].isliked) {
            holder.ivLiked.setImageResource(R.drawable.item_heart_filled)
        } else {
            holder.ivLiked.setImageResource(0)
        }

        holder.itemView.setOnClickListener {
            itemClick?.onClick(it, position)
        }
    }

    fun setData(newDataList: MutableList<KakaoImageData>) {
        kakaoItemData = newDataList
        notifyDataSetChanged()
    }

}