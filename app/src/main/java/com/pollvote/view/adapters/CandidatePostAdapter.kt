package com.pollvote.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pollvote.R
import com.pollvote.model.eventDetail.EventDetailPollList
import com.pollvote.model.pollCandidate.PollVoteCandidateList
import kotlinx.android.synthetic.main.list_item_event_position.view.*
import kotlin.collections.ArrayList

class CandidatePostAdapter(val mCallBack: CallBackNavigationItem?) :
    RecyclerView.Adapter<CandidatePostAdapter.MyHolder>(),
    CandidateNameAdapter.CallBackNavigationItem {

    lateinit var mContext: Context
    private var eventDetailPoll: MutableList<EventDetailPollList>? = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        mContext = parent.context
        return MyHolder(
            LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_event_position, parent, false)
        )

    }

    fun update(eventDetailPollList: MutableList<EventDetailPollList>) {

        this.eventDetailPoll = eventDetailPollList
        notifyDataSetChanged()

    }

    fun getItemAtPosition(position: Int): EventDetailPollList? {
        return eventDetailPoll?.get(position)
    }

    override fun getItemCount(): Int {


        return if (eventDetailPoll?.size != null) eventDetailPoll?.size as Int else 0

    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val notification: String
        val candidateNameList: MutableList<PollVoteCandidateList> = ArrayList()
        var candidateNameAdapter = CandidateNameAdapter()
        candidateNameList.clear()
        if (eventDetailPoll != null && eventDetailPoll!!.size > 0) {
            notification = eventDetailPoll!![position].pollName.toString()

            holder.titleTextView.text = notification

            candidateNameList.addAll(eventDetailPoll!![position].candidateList)

            /* candidateNameList.add("paul")
             candidateNameList.add("Devid")
             candidateNameList.add("Canin")*/

        }

        holder.rv_candidate_name.layoutManager = LinearLayoutManager(mContext)
        holder.rv_candidate_name.adapter = candidateNameAdapter
        candidateNameAdapter.update(candidateNameList)

        candidateNameAdapter.setCallBackSelectItem(object :
            CandidateNameAdapter.CallBackSelectItem {
            override fun callItem(pollId: Int, candidateId: Int, position: Int) {
               eventDetailPoll?.get(position)?.selectCandidateName = candidateNameList[position]
               // eventDetailPoll?.get(position)?.selectCandidateList=candidateNameList[pos]



                mCallBack!!.callBackItem(position)

            }

        })
        holder.itemView.setOnClickListener {
//            mCallBack!!.callBackItem(position)
        }
//        holder.cv_event.setOnClickListener {
//            mCallBack!!.callBackItem(position)
//        }


    }

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val titleTextView = itemView.txt_position_name
        val rv_candidate_name = itemView.rv_candidate


    }

    interface CallBackNavigationItem {
        fun callBackItem(position: Int)
    }

    override fun callBackItem(position: Int) {
    }


}