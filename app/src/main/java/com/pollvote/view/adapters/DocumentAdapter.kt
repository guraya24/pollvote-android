package com.pollvote.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pollvote.R
import com.pollvote.model.getDocumentList.DocumentData
import com.pollvote.network.Constant
import kotlinx.android.synthetic.main.llist_iten_document_adapter.view.*

class DocumentAdapter : RecyclerView.Adapter<DocumentAdapter.MyHolder>() {

    lateinit var mContext: Context
    private var documentData: ArrayList<DocumentData>? = null
    private var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        mContext = parent.context
        return MyHolder(
            LayoutInflater.from(mContext)
                .inflate(R.layout.llist_iten_document_adapter, parent, false)
        )

    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener

    }

    fun update(notificationList: ArrayList<DocumentData>) {

        this.documentData = notificationList
        notifyDataSetChanged()

    }

    fun updateUploadedItem(id: Int, isUploaded: Boolean) {
        var count = -1;
        for (data in documentData!!) {
            count += 1
            if (data.id == id) {
                this.documentData?.get(count)?.isUploaded = isUploaded
                documentData?.get(count)?.filePath = ""
                notifyDataSetChanged()
            }
        }


    }


    override fun getItemCount(): Int {
        return if (documentData?.size != null) documentData?.size as Int else 0
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        holder.btn_doc.text = documentData?.get(position)?.documentName

        if (documentData?.get(position)?.isUploaded == false) {
            holder.lay_doc.setBackgroundResource(R.drawable.bg_button_gray)
            holder.btn_doc.setTextColor(Color.BLACK)
            holder.iv_done.visibility = View.GONE
        } else {
            holder.lay_doc.setBackgroundResource(R.drawable.bg_button)
            holder.btn_doc.setTextColor(Color.WHITE)
            holder.iv_done.visibility = View.VISIBLE
        }

        holder.lay_doc.setOnClickListener {
            itemClickListener?.onButtonClick(position)
//            mContext.startActivity(Intent(mContext, EventListActivity::class.java))
//            (mContext as Activity).finishAffinity()
        }

    }


    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btn_doc = itemView.btn_doc
        val lay_doc = itemView.lay_doc
        val iv_done = itemView.iv_done
    }

    interface OnItemClickListener {
        fun onButtonClick(position: Int)
    }
}