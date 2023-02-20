package com.pollvote.view.adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.pollvote.R
import com.pollvote.model.event.EventListData
import com.pollvote.utils.TimeUtil
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import com.pollvote.view.activities.DocumentListActivity
import com.pollvote.view.activities.EventDetailActivity
import com.pollvote.view.dialogs.FindNearBoothDialog
import kotlinx.android.synthetic.main.list_item_event_lis.view.*
import java.util.*
import kotlin.collections.ArrayList

class EventListAdapter(private val callBackNavigationItem: CallBackNavigationItem) :
    RecyclerView.Adapter<EventListAdapter.MyHolder>(), Filterable {

    lateinit var mContext: Context
    private var eventList: MutableList<EventListData>? = null
    private var mFilteredList: MutableList<EventListData>? = null

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    mFilteredList = eventList
                } else {
                    val resultList = ArrayList<EventListData>()
                    for (row in eventList!!.indices) {
                        if (eventList!![row].eventName?.toLowerCase(Locale.ROOT)!!
                                .contains(charSearch.toLowerCase(Locale.ROOT)) || eventList!![row].township.toLowerCase(
                                Locale.ROOT
                            )
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                            || eventList!![row].addressLine1?.toLowerCase(Locale.ROOT)!!
                                .contains(charSearch.toLowerCase(Locale.ROOT)) || eventList!![row].county.toLowerCase(
                                Locale.ROOT
                            )
                                .contains(charSearch.toLowerCase(Locale.ROOT)) || eventList!![row].state.toLowerCase(
                                Locale.ROOT
                            )
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(eventList!![row])
                        }
                    }
                    mFilteredList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = mFilteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                try {
                    mFilteredList = results?.values as ArrayList<EventListData>
                    notifyDataSetChanged()
                }catch (e:TypeCastException){

                }
            }

        }
    }

    init {
        mFilteredList = eventList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        mContext = parent.context
        return MyHolder(
//            if (SharedPrefrencesUtils.isPoll) {
//                LayoutInflater.from(mContext)
//                    .inflate(R.layout.list_item_event_poll_list, parent, false)
//
//
//            } else {

            LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_event_lis, parent, false)

//            }
        )

    }

    fun update(eventListData: MutableList<EventListData>) {

        this.eventList = eventListData
        this.mFilteredList = eventList
        // notifyDataSetChanged()

    }

    /* override fun getItemCount(): Int {

         try {
             callBackNavigationItem.callBackItem(mFilteredList!!.size)
         } catch (e: KotlinNullPointerException) {

         }

         return if (eventList?.size != null) eventList?.size as Int else 0
     }*/

    override fun getItemCount(): Int {
        try {
            callBackNavigationItem.callBackItem(mFilteredList!!.size)
        } catch (e: KotlinNullPointerException) {

        }

        return if (mFilteredList?.size != null) mFilteredList?.size as Int else 0
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val eventListData: EventListData


        if (mFilteredList != null && mFilteredList!!.size > 0) {
            eventListData = mFilteredList!![position]
            holder.txt_event_name.text = eventListData.eventName

//            val splitDate = eventListData.scheduleDate?.split("T")

            holder.txt_start_date.text =
                eventListData.scheduleDate?.let { TimeUtil.formatServerDateToLocal(it) }
            //splitDate!![0]

            holder.txt_end_date.text = eventListData.scheduleEndDate
            holder.txt_event_name.text = eventListData.eventName
            holder.txt_Address.text = eventListData.addressLine1


            /*Required Document List and Check eligibility to vote if all document uploaded */
            if (eventListData.eventTypeName.equals("Poll")) {
                holder.doc_lay.visibility = View.GONE
            } else {
                holder.doc_lay.visibility = View.VISIBLE
                for (i in eventListData.documentList.indices) {


                    if (eventListData.documentList[i].documentName.equals("Voter Id")) {
                        holder.img_voter.visibility = View.VISIBLE

                        if (eventListData.documentList[i].isUploaded == true) {
                            holder.img_voter.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.id_green))
                        } else {
                            holder.img_voter.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.id_grey))
                        }

                    }


                    if (eventListData.documentList[i].documentName.equals("Driving Licence")) {
                        holder.img_licence.visibility = View.VISIBLE

                        if (eventListData.documentList[i].isUploaded == true) {
                            holder.img_licence.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.license_green))
                        } else {
                            holder.img_licence.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.license_grey))
                        }
                    }

                }

            }

            /* Check event type and show layout */
            if (eventListData.eventTypeName.equals("Poll")) {
                holder.img_eligible.visibility = View.GONE
                holder.doc_lay.visibility = View.GONE
                holder.vieww.visibility = View.GONE
                holder.img_already_cast_vote.visibility = View.GONE

                if (eventListData.hasVoted == true) {
                    holder.img_already_cast_poll.visibility = View.VISIBLE

                } else {
                    holder.img_already_cast_poll.visibility = View.GONE
                }


            } else if (eventListData.eventTypeName.equals("Vote")) {
                holder.img_eligible.visibility = View.VISIBLE
                holder.doc_lay.visibility = View.VISIBLE
                holder.vieww.visibility = View.VISIBLE
                holder.img_already_cast_poll.visibility = View.GONE

                if (eventListData.hasVoted == true) {
                    holder.img_already_cast_vote.visibility = View.VISIBLE
                } else {
                    holder.img_already_cast_vote.visibility = View.GONE
                }




                if (eventListData.isEligible == true) {
                    holder.img_eligible.setImageResource(R.drawable.ic_verified)
                    // Picasso.get().load(R.drawable.ic_verified).into(holder.img_eligible)
                } else {
                    holder.img_eligible.setImageResource(R.drawable.ic_uphold)
                    // Picasso.get().load(R.drawable.non_eligible_icon).into(holder.img_eligible)

                }
            }



            holder.cv_item?.setOnClickListener {
                //  mCallBack!!.callBackItem(position)
                var continues = true

                if (eventListData.eventTypeName?.toLowerCase(Locale.ROOT).equals("poll")) {

                    if (eventListData.hasVoted == true) {
                        eligibleDialog(
                            mContext.getString(R.string.sorry_already_cast),
                            "cast"
                        )
                    } else {
                        mContext.startActivity(
                            Intent(mContext, EventDetailActivity::class.java)
                                .putExtra(
                                    SharedPrefrencesUtils.EventId,
                                    eventListData.id.toString()
                                )
                                .putExtra(SharedPrefrencesUtils.Poll, true)
                                .putExtra(SharedPrefrencesUtils.Vote, false)
                        )
                    }
                } else {


                    /*Check document is uploaded*/
                    for (k in eventListData.documentList.indices) {
                        if (eventListData.documentList[k].isUploaded == false) {
                            continues = false
                            break
                        }
                    }

                    if (continues) {
                        /*Check is already voted*/
                        if (eventListData.isEligible == true) {
                            if (eventListData.hasVoted == true) {
                                eligibleDialog(
                                    mContext.getString(R.string.sorry_already_cast),
                                    "cast"
                                )
                            } else {

                                mContext.startActivity(
                                    Intent(mContext, EventDetailActivity::class.java)
                                        .putExtra(
                                            SharedPrefrencesUtils.EventId,
                                            eventListData.id.toString()
                                        )
                                        .putExtra(SharedPrefrencesUtils.Vote, true)
                                        .putExtra(SharedPrefrencesUtils.Poll, false)
                                )

                            }


                        } else {
                            val address =
                                eventListData.addressLine1!! + "," + eventListData.township + "," + eventListData.city + "," + eventListData.zipCode
                            val dialogFragment = FindNearBoothDialog(
                                eventListData.eventName!!,
                                address,
                                eventListData.scheduleDate!!
                            )
                            dialogFragment.show(
                                (mContext as FragmentActivity).supportFragmentManager,
                                "Nearby Booth"
                            )

                            // eligibleDialog(mContext.getString(R.string.sorry_eligible), "not_eligible")
                            /*eligibleDialog(
                                mContext.getString(R.string.name_not_in_voter_list),
                                "name_not"
                            )*/
                        }

                    } else {
                        eligibleDialog(
                            mContext.getString(R.string.document_upload),
                            "document_not_uploaded"
                        )


                    }
                }
            }
        }
    }

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var txt_event_name = itemView.txt_event_name
        var txt_start_date = itemView.txt_start_date
        var txt_end_date = itemView.txt_end_date
        var txt_Address = itemView.txt_Address
        var img_eligible = itemView.img_eligible
        var cv_item = itemView.cv_item
        val doc_lay = itemView.doc_lay
        val vieww = itemView.vieww
        val img_already_cast_vote = itemView.img_already_cast_vote
        val img_already_cast_poll = itemView.img_already_cast_poll
        val img_licence = itemView.img_licence
        val img_voter = itemView.img_voter

    }

    interface CallBackNavigationItem {
        fun callBackItem(size: Int)
    }

    private fun eligibleDialog(msg: String, status: String) {

        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_vote_poll)
        val txt_msg = dialog.findViewById(R.id.txt_msg) as TextView
        val img_status = dialog.findViewById(R.id.img_status) as ImageView
        val img_close = dialog.findViewById(R.id.img_close) as ImageView
        val btn_OK = dialog.findViewById(R.id.btn_OK) as Button
        if (status == "cast") {
            img_status.setImageResource(R.drawable.ic_thumbs_up)
        } else if (status == "document_not_uploaded") {
            img_status.setImageResource(R.drawable.ic_unhappy)
            img_close.visibility = View.VISIBLE

        } else {
            img_status.setImageResource(R.drawable.ic_unhappy)
        }


        txt_msg.text = msg


        img_close.setOnClickListener {
            dialog.dismiss()
        }
        btn_OK.setOnClickListener {
            if (status == "document_not_uploaded") {
                mContext.startActivity(Intent(mContext, DocumentListActivity::class.java))
                dialog.dismiss()
            } else if (status == "name_not") {

                dialog.dismiss()
            } else {
                dialog.dismiss()
            }

        }

        dialog.show()
    }


}