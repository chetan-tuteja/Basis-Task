package com.chetantuteja.basis.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chetantuteja.basis.R
import com.chetantuteja.basis.datamodels.Data
import kotlinx.android.synthetic.main.card_recycler_layout.view.*

class DataRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataList: ArrayList<Data> = ArrayList()

    //The Usual Recycler View Setup, mostly Boilerplate.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FeedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_recycler_layout,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is FeedViewHolder -> {
                holder.bind(dataList[position],dataList.size)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun submitList(listWeGet: ArrayList<Data>){
        dataList = listWeGet
    }

    class FeedViewHolder constructor(itemView: View):RecyclerView.ViewHolder(itemView){

        val cardID = itemView.id_TV
        val cardText = itemView.text_TV
        val cardCount = itemView.idCount_TV

        fun bind(dataItem: Data,size: Int){
            cardID.text = dataItem.id
            cardText.text = dataItem.text
            cardCount.text = "${dataItem.id}/$size"
        }
    }
}