package com.pollvote.view.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.pollvote.R
import com.pollvote.annotations.Status
import com.pollvote.model.eventDetail.EventDetailPollList
import com.pollvote.model.pollCandidate.PollVoteCandidateList
import com.pollvote.utils.ProgressUtils
import com.pollvote.utils.Static
import com.pollvote.utils.TimeUtil
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import com.pollvote.view.adapters.CandidatePostAdapter
import com.pollvote.viewModel.EventDetailViewModel
import kotlinx.android.synthetic.main.activity_event_detail.*
import kotlinx.android.synthetic.main.activity_event_detail.fl_profile_image
import kotlinx.android.synthetic.main.activity_event_detail.img_back
import kotlinx.android.synthetic.main.activity_event_detail.profile_image
import kotlinx.android.synthetic.main.activity_event_detail.txt_user_nick_name
import kotlinx.android.synthetic.main.layout_casting.*
import kotlinx.android.synthetic.main.layout_go_vote.*
import kotlinx.android.synthetic.main.llist_item_cast_your_vote.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EventDetailActivity : BaseActivity(), CandidatePostAdapter.CallBackNavigationItem {

    //  val candidatePositionList: MutableList<CandidatePositionResponse> = ArrayList()
    var candidatePositionList: MutableList<EventDetailPollList> = ArrayList()
    var selectCandidateList: MutableList<PollVoteCandidateList> = ArrayList()



    private lateinit var candidatePostAdapter: CandidatePostAdapter
    var startTimeStr: String = ""
    var endTimeStr: String = ""
    var eventId = ""
    var token = ""

    private var isValidateLay = false
    private var isGoVote = false
    private var isCastYourVote = false
    private var isStartPolling = false
    private var isEventPoll = false
    private var isEventVote = false


    private var canPosition = -1
    private lateinit var eventViewModel: EventDetailViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        val setToolbarTitle = Static()
        setToolbarTitle.statusBarColor(this)
        eventViewModel = ViewModelProvider(this).get(EventDetailViewModel::class.java)

        candidatePostAdapter = CandidatePostAdapter(this)
        token = SharedPrefrencesUtils.getToken().toString()
        eventId = intent.getStringExtra(SharedPrefrencesUtils.EventId).toString()
        isEventPoll = intent.getBooleanExtra(SharedPrefrencesUtils.Poll, false)
        isEventVote = intent.getBooleanExtra(SharedPrefrencesUtils.Vote, false)

        initUI()

        eventViewModel.callEventDetail(token, eventId)
        eventViewModel.callEventPollCandidate(token, eventId)

        initEventDetailObserver()

        //  getEventDetail(token, eventId)
    }

    override fun onRestart() {
        super.onRestart()

        val name = SharedPrefrencesUtils.getUserName()

        if (SharedPrefrencesUtils.getUserName()!!.contains(" ")) {
            val strNickName = SharedPrefrencesUtils.getUserName()?.split(" ")
            val fNic = strNickName!![0].toUpperCase(Locale.ROOT)
            val lNic = strNickName[1].toUpperCase(Locale.ROOT)
            val firstChar: Char = fNic[0]
            val secondChar: Char = lNic[0]

            txt_user_nick_name.text = (firstChar + "" + secondChar)
        } else {
            txt_user_nick_name.text = name?.toUpperCase(Locale.ROOT)
        }

        if (SharedPrefrencesUtils.getUserImage() != "" && SharedPrefrencesUtils.getUserImage() != null) {

            val imageLink = SharedPrefrencesUtils.getUserImage()
            Glide.with(this).load(imageLink).into(profile_image)

            // Picasso.get().load(imageLink).into(profile_image)
        }

        // txt_address.text = SharedPrefrencesUtils.getAddress()
    }

    // init Observer to observe API call initiate and result.
    @SuppressLint("SetTextI18n")
    private fun initEventDetailObserver() {
        eventViewModel.isLoading.observe(this,
            Observer {
                if (it) {
                    ProgressUtils.showLoadingDialog(this)
                } else {
                    ProgressUtils.cancelLoading()
                }
            })

        eventViewModel.resultData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val dataEvent = it?.data?.data!!
//                    val splitDate = dataEvent.scheduleDate?.split("T")

                    txt_event_title.text = dataEvent.eventName
                    txt_event_address.text = dataEvent.address
                    txt_start_date.text =
                        dataEvent.scheduleDate?.let { TimeUtil.formatServerDateToLocal(it) }// splitDate[0]


                    startTimeStr = TimeUtil.msConvert(dataEvent.startTime?.totalMilliseconds!!)
                    endTimeStr = TimeUtil.msConvert(dataEvent.stopTime?.totalMilliseconds!!)

                    txt_start_time.text = startTimeStr
                    txt_end_time.text = endTimeStr
                    // candidatePositionList = it?.data?.data!!.pollList
                    // rv_data()
                    candidatePositionList = it.data?.data!!.pollList
                    rvData()
                    mainScroll.visibility = View.VISIBLE

                    val today = Date()
                    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                    val currentDate = formatter.format(today)

                    /*America Los Angeles Format*/

                    /* val londonZone = ZoneId.of("America/Los_Angeles")
                     val londonCurrentDateTime = ZonedDateTime.now(londonZone)
                     val londonDateAndTime = londonCurrentDateTime.format(DateTimeFormatter.ofLocalizedDateTime(
                         FormatStyle.FULL, FormatStyle.FULL))

                     val formatter = SimpleDateFormat("dd MMM yyyy")
                     val currentDate=londonDateAndTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))*/

                    //  val currentDate = formatter.format(londonDateAndTime)


                    val startDate: String = txt_start_date.text.toString()
                    val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

                    try {
                        val date1 = simpleDateFormat.parse(currentDate)
                        val date2 = simpleDateFormat.parse(startDate)

                        if (date1!!.before(date2) || date1 == date2) {
                            if (TimeUtil.printDifference(date1, date2!!) <= 0) {
                                txt_polling_status.text = getString(R.string.current_time_in_hrs)
                                refreshClock()
                            } else {
                                try {


                                    val daysLeft = TimeUtil.printDifference(date1, date2).toString()


                                    if (daysLeft == "1") {

                                        txt_time_left.text = daysLeft + "Day"
                                        d_clock.text = daysLeft + "Day"
                                    } else {
                                        d_clock.text = daysLeft + "Days"
                                        txt_time_left.text = daysLeft + "Days"
                                    }



                                    txt_polling_status.text =
                                        getString(R.string.current_time_in_days)
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }


                                // btn_validate_me.isEnabled = false
                                //   btn_validate_me.isClickable = false

                                btn_validate_me.setTextColor(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.black
                                    )
                                )
                                btn_validate_me.setBackgroundResource(R.drawable.bg_button_gray)
                                isStartPolling = false
                            }
                        } else {
                            d_clock.text = "Time's Up"
                            txt_polling_left.text = "00:00:00"
                        }

                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                }
                Status.FAILURE -> {
                    makeToast("" + it.errorMsg)

                }
            }
        })


        eventViewModel.pollVoteResultData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val dataEvent = it?.data!!
                    makeToast("" + it.data!!.message)
                    // val set: Set<String> = HashSet()

                    //  set.plus(eventId)

                    // SharedPrefrencesUtils.setPolledEventId(set as HashSet<String>)
                    votePollSuccessDialog(dataEvent.message.toString(), true)

                }
                Status.FAILURE -> {
                    makeToast("" + it.errorMsg)
                    votePollSuccessDialog(it.errorMsg.toString(), false)
                }
            }
        })

        /*poll vote observer*/
        eventViewModel.voterValidateResultData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val dataEvent = it?.data!!


                    in_go_vote.visibility = View.VISIBLE
                    btn_go_vote.startAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.button_shake
                        )
                    )
                    ll_validate_me.visibility = View.GONE
                    isGoVote = true
                    makeToast("" + dataEvent.message)


                }
                Status.FAILURE -> {
                    makeToast("" + it.errorMsg)
                }
            }
        })


    }


    fun rvData() {
        rv_event_candidate?.layoutManager = LinearLayoutManager(this)
        rv_event_candidate?.adapter = candidatePostAdapter
        candidatePostAdapter.update(candidatePositionList)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun digiClock() {

        val today = Date()
        val formatter = SimpleDateFormat("h:mm:ss a", Locale.ENGLISH)
        val current = formatter.format(today)
        val startTime: Date? = formatter.parse(startTimeStr)
        val currentTime: Date? = formatter.parse(current)
        val endTime: Date? = formatter.parse(endTimeStr)

        //  Log.e("End Time",endTime.toString())

        when {
            currentTime!!.before(startTime) -> {

                d_clock.text = TimeUtil.timeDifference(startTimeStr, endTimeStr, 1)
                txt_time_left.text = TimeUtil.timeDifference(startTimeStr, endTimeStr, 1)

                // btn_validate_me.isEnabled = false
                // btn_validate_me.isClickable = false

                btn_validate_me.setTextColor(ContextCompat.getColor(this, R.color.black))
                btn_validate_me.setBackgroundResource(R.drawable.bg_button_gray)
                isStartPolling = false
            }
            currentTime.before(endTime) -> {

                d_clock.text = TimeUtil.timeDifference(startTimeStr, endTimeStr, 2)
                txt_time_left.text = TimeUtil.timeDifference(startTimeStr, endTimeStr, 2)
                txt_polling_left.text = getString(R.string.time_left_to_stop_polling)
                btn_validate_me.setTextColor(ContextCompat.getColor(this, R.color.color_white))

                btn_validate_me.setBackgroundResource(R.drawable.bg_button)

                btn_go_vote.background = ContextCompat.getDrawable(this, R.drawable.bg_button)
                btn_go_vote.setTextColor(ContextCompat.getColor(this, R.color.white))
                // btn_validate_me.isEnabled = true
                // btn_validate_me.isClickable = true
                isStartPolling = true
            }
            else -> {

                d_clock.text = "Time's Up"
                txt_polling_left.text = "00:00:00"
                // txt_time_left.text = TimeUtil.timeDifference(startTimeStr, endTimeStr, 3)

                //   btn_validate_me.isEnabled = false
                //   btn_validate_me.isClickable = false

                btn_validate_me.setTextColor(ContextCompat.getColor(this, R.color.black))
                btn_validate_me.setBackgroundResource(R.drawable.bg_button_gray)

                isStartPolling = false
                //Toast.makeText(this,"Polling time not available",Toast.LENGTH_SHORT).show()
            }
        }

    }


    @Suppress("DEPRECATION")
    private fun refreshClock() {
        val handler = Handler()
        val runnable = object : Runnable {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                try {
                    digiClock()
                } catch (e: Exception) {
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(runnable, 1000)
    }

    /*Initilize UI*/
    fun initUI() {
        val name = SharedPrefrencesUtils.getUserName()

        if (SharedPrefrencesUtils.getUserName()!!.contains(" ")) {
            val strNickName = SharedPrefrencesUtils.getUserName()?.split(" ")
            val fNic = strNickName!![0].toUpperCase(Locale.ROOT)
            val lNic = strNickName[1].toUpperCase(Locale.ROOT)
            val firstChar: Char = fNic[0]
            val secondChar: Char = lNic[0]

            txt_user_nick_name.text = (firstChar + "" + secondChar)
        } else {
            txt_user_nick_name.text = name?.toUpperCase(Locale.ROOT)
        }
        if (SharedPrefrencesUtils.getUserImage() != "" && SharedPrefrencesUtils.getUserImage() != null) {

            val imageLink = SharedPrefrencesUtils.getUserImage()
            Glide.with(this).load(imageLink).into(profile_image)

            //Picasso.get().load(imageLink).into(profile_image)
        }
        //btn_profile.text = SharedPrefrencesUtils.getUserName()


        if (isEventPoll) {
            if (!isStartPolling) {
                btn_go_vote.background = ContextCompat.getDrawable(this, R.drawable.bg_button_gray)
                btn_go_vote.setTextColor(ContextCompat.getColor(this,R.color.black))

                ll_validate_me.visibility = View.GONE
                in_casting.visibility = View.GONE
                in_go_vote.visibility = View.VISIBLE
                isValidateLay = false
                isCastYourVote = false
                isGoVote = true
            } else {
                btn_go_vote.background = ContextCompat.getDrawable(this, R.drawable.bg_button)
                btn_go_vote.setTextColor(ContextCompat.getColor(this, R.color.white))
            }


        } else {
            isValidateLay = true
            ll_validate_me.visibility = View.VISIBLE
        }

        txt_end_time.text = endTimeStr
        txt_start_time.text = startTimeStr


        /*Back Button Validation*/
        img_back.setOnClickListener {
            if (isEventPoll) {
                if (isCastYourVote) {
                    isCastYourVote = false
                    isGoVote = true
                    in_go_vote.visibility = View.VISIBLE
                    in_casting.visibility = View.GONE
                    ll_validate_me.visibility = View.GONE
                    in_cast_your_vote.visibility = View.GONE
                    ll_circle_clock.visibility = View.VISIBLE
                    ll_horizontal_clock.visibility = View.GONE
                } else {
                    finish()
                }
            } else {
                if (isCastYourVote) {
                    in_go_vote.visibility = View.VISIBLE
                    in_casting.visibility = View.GONE
                    ll_validate_me.visibility = View.GONE
                    in_cast_your_vote.visibility = View.GONE
                    isCastYourVote = false

                    ll_circle_clock.visibility = View.VISIBLE
                    ll_horizontal_clock.visibility = View.GONE
                } else if (isGoVote) {
                    isGoVote = false
                    in_go_vote.visibility = View.GONE
                    in_casting.visibility = View.GONE
                    ll_validate_me.visibility = View.VISIBLE
                } else {
                    finish()
                }
            }

        }
        fl_profile_image.setOnClickListener {
            startActivity(Intent(this, ProfileDetail::class.java))
        }

        btn_validate_me.setOnClickListener {

            if (!isStartPolling) {
                btn_validate_me.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shaking))
                // makeToast(getString(R.string.pollvote_time_stopped))
            } else {
                //getValidateId(token, eventId)

                eventViewModel.callVoterValidate(token, eventId)


            }


        }

        /*Click Button on GoTo Vote. in Event Type "Vote" It's show after click  of validate me button . else it's show first in "Poll" Event Type*/

        btn_confirm_vote.setOnClickListener {
            if (et_voter_id.text.toString() == "") {

                makeToast(getString(R.string.enter_voter_id))
                // Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            } else {

                isGoVote = true

                in_go_vote.visibility = View.VISIBLE
                in_casting.visibility = View.GONE
                ll_validate_me.visibility = View.GONE

            }
        }

        /*Click Button on GoTo Vote. in Event Type "Vote" It's show after click  of validate me button . else it's show first in "Poll" Event Type*/
        btn_go_vote.setOnClickListener {

            if (isEventPoll) {
                if (!isStartPolling) {

                    btn_go_vote.startAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.shaking
                        )
                    )
                    // makeToast(getString(R.string.pollvote_time_stopped))
                } else {


                    //getValidateId(token, eventId)
                    in_go_vote.visibility = View.GONE
                    in_casting.visibility = View.GONE
                    ll_validate_me.visibility = View.GONE
                    in_cast_your_vote.visibility = View.VISIBLE
                    ll_circle_clock.visibility = View.GONE
                    ll_horizontal_clock.visibility = View.VISIBLE
                    isCastYourVote = true

                }
            } else {
                in_go_vote.visibility = View.GONE
                in_casting.visibility = View.GONE
                ll_validate_me.visibility = View.GONE
                in_cast_your_vote.visibility = View.VISIBLE
                ll_circle_clock.visibility = View.GONE
                ll_horizontal_clock.visibility = View.VISIBLE
                isCastYourVote = true
            }

            rvData()
        }

        /*Click Button for Poll Vote. Open Dialog if event type is vote else vote poll without match the voter id*/
        btn_vote.setOnClickListener {

            if (selectCandidateList.size == 0) {
                makeToast(getString(R.string.select_one_candidate))
            } else {

                if (isEventPoll) {

                    val voteArray = JsonArray()

                    for (listItem in candidatePositionList) {
                        if (listItem.selectCandidateName?.id == 0 || listItem.selectCandidateName?.id == null) {
                            Log.e("data", "")
                        } else {
                            val voterObj = JsonObject()
                            voterObj.addProperty("PollId", listItem.selectCandidateName?.pollId)
                            voterObj.addProperty("CandidateUserId", listItem.id)
                            voteArray.add(voterObj)
                        }
                    }

                    eventViewModel.callPollVote(token, voteArray)
                } else {
                    validateDialog()
                }
            }
        }
    }

    override fun callBackItem(position: Int) {
        canPosition = position
        selectCandidateList.add(candidatePositionList[position].selectCandidateName!!)
    }

    override fun onBackPressed() {
        // super.onBackPressed()
        if (isEventPoll) {
            if (isCastYourVote) {
                isCastYourVote = false
                isGoVote = true
                in_go_vote.visibility = View.VISIBLE
                in_casting.visibility = View.GONE
                ll_validate_me.visibility = View.GONE
                in_cast_your_vote.visibility = View.GONE
                ll_circle_clock.visibility = View.VISIBLE
                ll_horizontal_clock.visibility = View.GONE
            } else {
                finish()
            }
        } else {
            if (isCastYourVote) {
                in_go_vote.visibility = View.VISIBLE
                in_casting.visibility = View.GONE
                ll_validate_me.visibility = View.GONE
                in_cast_your_vote.visibility = View.GONE
                isCastYourVote = false

                ll_circle_clock.visibility = View.VISIBLE
                ll_horizontal_clock.visibility = View.GONE
            } else if (isGoVote) {
                isGoVote = false
                in_go_vote.visibility = View.GONE
                in_casting.visibility = View.GONE
                ll_validate_me.visibility = View.VISIBLE
            } else {
                finish()
            }
        }
    }

    private fun validateDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_validate)
        val etVoterId = dialog.findViewById(R.id.et_voter_id) as EditText

        val btnConfirmVote = dialog.findViewById(R.id.btn_confirm_vote) as Button
        val imgClose = dialog.findViewById(R.id.img_close) as ImageView

        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirmVote.setOnClickListener {

            if (etVoterId.text.isEmpty()) {

                makeToast(getString(R.string.enter_voter_id))
                btnConfirmVote.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shaking))

                //Toast.makeText(this, "Please Enter voter id", Toast.LENGTH_SHORT).show()
            } else {


                if (SharedPrefrencesUtils.getVoterId().toString() == etVoterId.text.toString()
                        .trim()
                ) {

                    if (isStartPolling) {

                        val voteArray = JsonArray()

                        for (listItem in candidatePositionList) {
                            if (listItem.selectCandidateName?.id == 0 || listItem.selectCandidateName?.id == null) {
                                Log.e("daa", "")
                            } else {
                                val voterObj = JsonObject()
                                voterObj.addProperty("PollId", listItem.selectCandidateName?.pollId)
                                voterObj.addProperty("CandidateUserId", listItem.id)
                                voteArray.add(voterObj)
                            }
                        }




                        dialog.dismiss()
                        eventViewModel.callPollVote(token, voteArray)
                    } else {
                        votePollSuccessDialog(getString(R.string.oops_time_up), false)
                    }
                } else {
                    makeToast(getString(R.string.enter_valid_voter_id))

                }
            }

        }

        dialog.show()

    }

    private fun votePollSuccessDialog(msg: String, status: Boolean) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_vote_poll)
        val txtMsg = dialog.findViewById(R.id.txt_msg) as TextView
        val imgStatus = dialog.findViewById(R.id.img_status) as ImageView


        val btnOk = dialog.findViewById(R.id.btn_OK) as Button

        txtMsg.text = msg
        if (status) {
            imgStatus.setImageResource(R.drawable.validate_icon)
        } else {

            imgStatus.setImageResource(R.drawable.ic_unhappy)
        }

        btnOk.setOnClickListener {
            startActivity(Intent(this, EventListActivity::class.java))
            finish()

        }

        dialog.show()
    }
}