package com.pollvote.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pollvote.R
import com.pollvote.model.pollCandidate.PollVoteCandidateList
import com.pollvote.view.interfaces.CallBackSelectItem
import kotlinx.android.synthetic.main.list_item_candidate_name.view.*

class CandidateNameAdapter() :
    RecyclerView.Adapter<CandidateNameAdapter.MyHolder>() {
    private var callBackSelectItem: CallBackSelectItem? = null
    private var isClickItem: HashMap<Int, Boolean>? = HashMap<Int, Boolean>()
    private var isClick: Boolean = false
    var selectPos: Int = 0
    lateinit var mContext: Context
    private var pollVoteCandidateList: MutableList<PollVoteCandidateList>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        mContext = parent.context
        return MyHolder(
            LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_candidate_name, parent, false)
        )

    }

    fun setCallBackSelectItem(callBackSelectItem: CallBackSelectItem) {
        this.callBackSelectItem = callBackSelectItem
    }

    fun update(notificationList: MutableList<PollVoteCandidateList>) {

        this.pollVoteCandidateList = notificationList
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return if (pollVoteCandidateList?.size != null) pollVoteCandidateList?.size as Int else 0
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val candidateName: String

        if (pollVoteCandidateList != null && pollVoteCandidateList!!.size > 0) {
            candidateName = pollVoteCandidateList!![position].firstName.toString()

            holder.txt_cn.text = candidateName


        }
       /*Select Candidate*/
        if (isClick) {
            if (selectPos == position) {
                holder.ll_item.setBackgroundResource(R.drawable.bg_selection)
                holder.txt_cn.setTextColor(ContextCompat.getColor(mContext,R.color.color_white))
                holder.txt_party.setTextColor(ContextCompat.getColor(mContext,R.color.color_white))

                callBackSelectItem?.callItem(
                    pollVoteCandidateList!![position].id!!,
                    pollVoteCandidateList!![position].pollId!!, position
                )
                //selectId.put(pollVoteCandidateList!![position].id!!,pollVoteCandidateList!![position].pollId!!)
            } else {
                holder.txt_cn.setTextColor(ContextCompat.getColor(mContext,R.color.color_black))
                holder.txt_party.setTextColor(ContextCompat.getColor(mContext,R.color.color_black))
                holder.ll_item.setBackgroundResource(R.drawable.bg_un_selection)
                // selectId.remove(position)
            }
        }

        holder.ll_item?.setOnClickListener {
            isClick = true
            selectPos = position
            notifyDataSetChanged()
        }
    }

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val txt_party = itemView.txt_party
        var txt_cn = itemView.txt_cn
        var ll_item = itemView.ll_item


    }

    interface CallBackNavigationItem {
        fun callBackItem(position: Int)
    }

    interface CallBackSelectItem {

        fun callItem(pollId: Int, candidateId: Int, position: Int)

    }

}


