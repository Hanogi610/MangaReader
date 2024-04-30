package com.example.mangareader.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import com.example.mangareader.model.Chapter

class BottomSheetRvAdapter(private var chapters: List<Chapter>, private val chapterPost : Int, private val onItemClick : (Chapter, Int) -> Unit): RecyclerView.Adapter<BottomSheetRvAdapter.viewHolder>(){
    class viewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.chapterTitle)
        val date = view.findViewById<TextView>(R.id.chapterDate)
        val status = view.findViewById<View>(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bottom_sheet_rv_item, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return chapters.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.title.text = chapters[position].chapterTitle
        holder.date.text = chapters[position].chapterDate
        if(position == chapterPost) {
            holder.status.visibility = View.VISIBLE
        }else{
            holder.status.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            onItemClick(chapters[position], position)
        }
    }

    fun updateData(newData: List<Chapter>){
        chapters = newData
        notifyDataSetChanged()
    }
}