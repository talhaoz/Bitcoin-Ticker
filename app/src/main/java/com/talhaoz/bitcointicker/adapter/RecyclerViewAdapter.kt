package com.talhaoz.bitcointicker.adapter

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.squareup.picasso.Picasso
import com.talhaoz.bitcointicker.R
import com.talhaoz.bitcointicker.models.CryptoAllCoinsModelItem
import com.talhaoz.bitcointicker.ui.MainActivity
import com.talhaoz.bitcointicker.ui.SingleCoinActivity
import kotlinx.android.synthetic.main.row_layout.view.*


class RecyclerViewAdapter() : RecyclerView.Adapter<RecyclerViewAdapter.TabViewHolder>() {

    private var items: ArrayList<CryptoAllCoinsModelItem> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val myHolder = TabViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_layout,
                parent,
                false
            )
        )

        myHolder.itemView.setOnClickListener {
            val pos = myHolder.adapterPosition

            // create a dialog box to show clicked item data
            if (pos != NO_POSITION) {

                val intent = Intent(parent.context, SingleCoinActivity::class.java)
                // To pass any data to next activity
                intent.putExtra("coinId", items[pos].id)
                // start your next activity
                parent.context.startActivity(intent)


            }
        }
        return myHolder
    }

    fun addAll(list : ArrayList<CryptoAllCoinsModelItem>?) {

        items.clear()

        if(list!=null)
            items.addAll(list)

        notifyDataSetChanged()


    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {

        val item = items[position]

        holder.itemView.coinName.text= item.name + " (${item.symbol.toUpperCase()})"

    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun getItemCount(): Int {
        return items.size
    }


    inner class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
