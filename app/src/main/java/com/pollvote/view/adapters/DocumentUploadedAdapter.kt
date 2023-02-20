package com.pollvote.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pollvote.R
import com.pollvote.model.getDocumentList.DocumentData
import kotlinx.android.synthetic.main.list_item_document_uploaded.view.*

class DocumentUploadedAdapter : RecyclerView.Adapter<DocumentUploadedAdapter.MyHolder>() {

    lateinit var mContext: Context
    private var documentData: ArrayList<DocumentData>? = null
    private var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        mContext = parent.context
        return MyHolder(
            LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_document_uploaded, parent, false)
        )

    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener

    }

    fun update(notificationList: ArrayList<DocumentData>) {

        this.documentData = notificationList
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return if (documentData?.size != null) documentData?.size as Int else 0
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        val list = documentData?.get(position)?.filePath?.split("/")
        holder.tv_identity_name.text = list?.get(list.lastIndex).toString()

        holder.tv_preview_identity.setOnClickListener {
            itemClickListener?.onPreviewClick(position)
        }

        holder.iv_cancel_identity_proof.setOnClickListener {
            itemClickListener?.onCancelClick(position)
        }
    }


    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_preview_identity = itemView.tv_preview_identity
        val tv_identity_name = itemView.tv_identity_name
        val iv_cancel_identity_proof = itemView.iv_cancel_identity_proof
    }

    interface OnItemClickListener {
        fun onPreviewClick(position: Int)
        fun onCancelClick(position: Int)

    }
}