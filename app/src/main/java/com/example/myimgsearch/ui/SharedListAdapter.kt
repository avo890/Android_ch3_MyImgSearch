package com.example.myimgsearch.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myimgsearch.R
import com.example.myimgsearch.data.KakaoImageData
import com.example.myimgsearch.databinding.LayoutItemBinding
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class SharedListAdapter : ListAdapter<KakaoImageData, SharedListAdapter.Holder>(diffUtil) {

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    inner class Holder(private val binding: LayoutItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: KakaoImageData) {
            with(binding) {
                Glide.with(itemView.context).load(data.thumbnailUrl).into(ivItemImg)
                tvSiteName.text = data.siteName
                tvDateTime.text = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(OffsetDateTime.parse(data.datetime))

                if (data.isliked) {
                    ivLiked.setImageResource(R.drawable.item_heart_filled)
                } else {
                    ivLiked.setImageDrawable(null)
                }

                itemView.setOnClickListener {
                    itemClick?.onClick(it, adapterPosition)
                }
            }
        }

    }

    companion object{
        private val diffUtil = object : DiffUtil.ItemCallback<KakaoImageData>() {
            override fun areItemsTheSame(oldItem: KakaoImageData, newItem: KakaoImageData): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: KakaoImageData, newItem: KakaoImageData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = LayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


}
