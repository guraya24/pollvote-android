package com.pollvote.view.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pollvote.R
import com.pollvote.annotations.Status
import com.pollvote.model.event.EventListData
import com.pollvote.utils.ProgressUtils
import com.pollvote.utils.Static
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import com.pollvote.view.adapters.EventListAdapter
import com.pollvote.view.dialogs.EventFilterDialog
import com.pollvote.view.interfaces.CallBackListener
import com.pollvote.viewModel.EventDetailViewModel
import kotlinx.android.synthetic.main.activity_event_list.*
import kotlinx.android.synthetic.main.activity_profile.profile_image
import kotlinx.android.synthetic.main.activity_profile.txt_user_nick_name
import kotlinx.android.synthetic.main.dialog_vote_success.*
import java.util.*


class EventListActivity : BaseActivity(), CallBackListener,
    EventListAdapter.CallBackNavigationItem {
    var vote: Boolean = false
    var token = ""
    private lateinit var eventViewModel: EventDetailViewModel
    private lateinit var rv_event_list: RecyclerView
    private lateinit var eventListAdapter: EventListAdapter

    var filter = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)
        val setToolbarTitle = Static()
        setToolbarTitle.statusBarColor(this)
        token = SharedPrefrencesUtils.getToken().toString()
        eventViewModel = ViewModelProvider(this).get(EventDetailViewModel::class.java)

        eventListAdapter = EventListAdapter(this)
        initUI()
        // rv_data()
        //getEventList(token)

        initEventDetailObserver()
        eventViewModel.callEventList(token, filter)
    }

    fun rvData(eventList: MutableList<EventListData>) {

        rv_event_list = findViewById(R.id.rv_event_list)
        rv_event_list.layoutManager = LinearLayoutManager(this)
        rv_event_list.adapter = eventListAdapter
        eventListAdapter.update(eventList)
        eventListAdapter.notifyDataSetChanged()
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

        if (SharedPrefrencesUtils.getUserImage() != ""&&SharedPrefrencesUtils.getUserImage()!=null){

            val imageLink =SharedPrefrencesUtils.getUserImage()
            Glide.with(this).load(imageLink).into(profile_image)

           // Picasso.get().load(imageLink).into(profile_image)
        }

        txt_address.text = SharedPrefrencesUtils.getAddress()
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

        eventViewModel.eventListResultData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val dataEvent = it?.data?.data!!

                    // dataEvent.clear()

                    rvData(dataEvent)
                    if (filter == "Poll")
                        txt_event_type.text = "Poll Events"
                    if (filter == "Vote")
                        txt_event_type.text = "Vote Events"
                    if (filter == "All")
                        txt_event_type.text = "All Events"

                    if (dataEvent.size == 0) {
                        ll_null_msg.visibility = View.VISIBLE
                        ll_list.visibility = View.GONE
                    } else {
                        ll_null_msg.visibility = View.GONE
                        ll_list.visibility = View.VISIBLE
                    }

                }
                Status.FAILURE -> {
                    makeToast("" + it.errorMsg)


                }
            }
        })
    }


    @SuppressLint("SetTextI18n")
    fun initUI() {

        item_refresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )
        )
        item_refresh.setColorSchemeColors(Color.WHITE)

        item_refresh.setOnRefreshListener {
            et_search.setText("")
            filter = "All"
            eventViewModel.callEventList(token, filter)
            item_refresh.isRefreshing = false
            txt_event_type.text = "All Events"
        }

        txt_event_type.text = "All Events"
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

        if (SharedPrefrencesUtils.getUserImage() != ""&&SharedPrefrencesUtils.getUserImage()!=null){

            val imageLink =SharedPrefrencesUtils.getUserImage()
            Glide.with(this).load(imageLink).into(profile_image)

         //   Picasso.get().load(imageLink).into(profile_image)
        }


        txt_address.text = SharedPrefrencesUtils.getAddress()

        ibtn_filter.setOnClickListener {
            val dialogFragment = EventFilterDialog(this, filter)
            dialogFragment.show(supportFragmentManager, "filter")

        }

        fl_profile_image.setOnClickListener {
            startActivity(Intent(this, ProfileDetail::class.java))
        }

        vote = intent.getBooleanExtra("vote_complete", false)
        if (vote) {
            showDialog()
        }
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                eventListAdapter.filter.filter(s.toString())
            }
        })


    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_vote_success)

        dialog.btn_continue.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }


    override fun callBack(data: String, isStatus: Boolean) {
        et_search.setText("")
        if (SharedPrefrencesUtils.isPoll) {
            filter = "Poll"
            //getEventList(token)
            eventViewModel.callEventList(token, filter)
            //eventListAdapter.filter.filter("Poll")
        } else {
            filter = "Vote"
            eventViewModel.callEventList(token, filter)
            //eventListAdapter.filter.filter("Vote")
            //  getEventList(token)
        }

    }

    override fun callBackItem(size: Int) {


    }
}