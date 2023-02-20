package com.pollvote.view.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pollvote.R
import com.pollvote.annotations.Status
import com.pollvote.model.county.CountyData
import com.pollvote.model.states.StateData
import com.pollvote.utils.ProgressUtils
import com.pollvote.utils.Static
import com.pollvote.utils.TimeUtil
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import com.pollvote.viewModel.AddressViewModel
import com.pollvote.viewModel.UserProfileViewModel
import kotlinx.android.synthetic.main.activity_change_password.btn_continue
import kotlinx.android.synthetic.main.activity_change_password.img_back
import kotlinx.android.synthetic.main.activity_change_personal_detail.*
import kotlinx.android.synthetic.main.activity_change_personal_detail.et_first_name
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChangePersonalDetailActivity : BaseActivity() {
    var token = ""
    var setToolbarTitle = Static()

    var cal = Calendar.getInstance()
    var gender_list = ArrayList<String>()


    var countryId = "1"
    var county: String = ""
    var state: String = ""

    var countyId: Int = 0
    var stateId: Int = 0
    var gender: String = ""
    var dob: String = ""
    val stateNameList = ArrayList<String>()
    val countyNameList = ArrayList<String>()

    var countyList: MutableList<CountyData?>? = null
    var stateList: MutableList<StateData?>? = null
    private lateinit var userProfileViewModel: UserProfileViewModel
    private lateinit var addressViewModel: AddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_personal_detail)
        userProfileViewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)
        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
        setToolbarTitle.statusBarColor(this)
        token = SharedPrefrencesUtils.getToken().toString()
        initUI()
        gender_list.add(getString(R.string.select_gender_sp))
        gender_list.add(getString(R.string.male))
        gender_list.add(getString(R.string.female))
        //  getProfileDetail(token)
        initPersonalDetailObserver()
        initProvinceCountyObserver()

        userProfileViewModel.getProfileDetail(token)
        //  getStateList("1")

    }

    fun initUI() {
        btn_continue.setOnClickListener {

            if (et_first_name.text.toString() == "") {
                et_first_name.error = getString(R.string.enter_first_name)
            } else if (et_first_name.text.toString().split(" ").size == 1) {
                et_first_name.error = getString(R.string.enter_full_name)
            } else if (et_first_name.text.toString()
                    .split(" ").size == 2 && et_first_name.text.toString().split(" ")[1].isEmpty()
            ) {
                et_first_name.error = getString(R.string.enter_full_name)
            } else if (et_address.text.toString() == "") {
                et_address.error = getString(R.string.enter_address)
            } else if (et_vill_city.text.toString() == "") {
                et_vill_city.error = getString(R.string.please_enter_city)
            } else if (state.isEmpty()) {
                makeToast(getString(R.string.select_state))
                Toast.makeText(this, getString(R.string.select_state), Toast.LENGTH_SHORT).show()
            } else if (county.isEmpty()) {
                makeToast(getString(R.string.select_county))
            } else if (et_dob.text.toString() == "") {
                et_dob.error = getString(R.string.please_enter_dob)
            } else if (et_zip.text.toString() == "") {
                et_zip.error = getString(R.string.enter_zip_code)
            } else if (gender.isEmpty()) {
                makeToast(getString(R.string.select_gender))
            } else {
                val address = et_address.text.toString()
                val town = et_vill_city.text.toString()
                val zip = et_zip.text.toString()
                val userName = et_first_name.text.toString()
                userProfileViewModel.callUpdatePersonalDetail(
                    token,
                    userName,
                    address,
                    town,
                    stateId,
                    countyId,
                    zip,
                    dob,
                    gender
                )
            }
        }
        img_back.setOnClickListener {
            finish()
        }


        et_dob.setOnFocusChangeListener { _, _ ->
            val picker = DatePickerDialog(
                this@ChangePersonalDetailActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            val calMiax = Calendar.getInstance()
            calMiax.set(Calendar.YEAR, calMiax.get(Calendar.YEAR))

            val calMin = Calendar.getInstance()
            calMin.set(Calendar.YEAR, 1920)

            picker.datePicker.minDate = calMin.time.time
            picker.datePicker.maxDate = calMiax.time.time

            picker.show()
        }

        et_dob.setOnClickListener {
            val picker = DatePickerDialog(
                this@ChangePersonalDetailActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            val calMax = Calendar.getInstance()
            calMax.set(Calendar.YEAR, calMax.get(Calendar.YEAR))

            val calMin = Calendar.getInstance()
            calMin.set(Calendar.YEAR, 1920)

            picker.datePicker.minDate = calMin.time.time
            picker.datePicker.maxDate = calMax.time.time

            picker.show()

        }
    }

    // init Observer to observe API call initiate and result.
    private fun initPersonalDetailObserver() {
        userProfileViewModel.isLoading.observe(this,
            Observer {
                if (it) {
                    ProgressUtils.showLoadingDialog(this)
                } else {
                    ProgressUtils.cancelLoading()
                }
            })

        userProfileViewModel.resultUpdatePesonalDetailData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val dataEvent = it?.data!!


                    SharedPrefrencesUtils.setUserLogin(
                        et_first_name.text.toString(),
                        et_address.text.toString() + ", " + dataEvent.data?.twonShip + ", " + county + ", " + state
                            .toString(),
                        county + ", " + state,
                        dataEvent.data?.twonShip.toString(),
                        dataEvent.data?.dob.toString(),
                        dataEvent.data?.gender.toString(),
                        dataEvent.data?.zipCode.toString()

                    )
                    startActivity(
                        Intent(
                            this@ChangePersonalDetailActivity,
                            ProfileDetail::class.java
                        )
                    )
                    finish()
                    makeToast("" + dataEvent.message)
                }
                Status.FAILURE -> {

                    makeToast("" + it?.errorMsg)

                }
            }
        })




        userProfileViewModel.resultProfileDetailData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val dataEvent = it?.data!!
                    SharedPrefrencesUtils.setUserImage(dataEvent.data?.logofile!!)
                    et_first_name.setText(dataEvent.data.firstName)
                    et_address.setText(dataEvent.data.address1)
                    et_vill_city.setText(dataEvent.data.townShip)
                    et_zip.setText(dataEvent.data.zipcode)
                    et_dob.setText(TimeUtil.formatServerDateToLocal(dataEvent.data.dob!!))
                    dob = dataEvent.data.dobString.toString()
                    state = dataEvent.data.stateName.toString()
                    county = dataEvent.data.county.toString()
                    gender = dataEvent.data.gender.toString()
                    SharedPrefrencesUtils.setUserLogin(
                        dataEvent.data.firstName,
                        dataEvent.data.address1 + ", " + dataEvent.data.townShip.toString() + ", " + dataEvent.data.county + ", " + dataEvent.data.stateName,
                        dataEvent.data.county + ", " + dataEvent.data.stateName,
                        dataEvent.data.townShip.toString(),
                        dataEvent.data.dobString.toString(),
                        dataEvent.data.gender.toString(),
                        dataEvent.data.zipcode.toString()

                    )

                    addressViewModel.getStateList(countryId, token)
                    genderSpInit()
                }
                Status.FAILURE -> {
                    makeToast("" + it?.errorMsg)
                }
            }
        })
    }


    // init Observer to observe API call initiate and result.
    private fun initProvinceCountyObserver() {
        /* addressViewModel.isLoading.observe(this,
             Observer {
                 if (it) {
                     ProgressUtils.showLoadingDialog(this)
                 } else {
                     ProgressUtils.cancelLoading()
                 }
             })*/

        addressViewModel.resultStatesData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val dataEvent = it?.data!!
                    stateList = null
                    stateNameList.clear()


                    val stateData = StateData(0, getString(R.string.select_province), 1, true)
                    dataEvent.data.add(0, stateData)

                    stateList = dataEvent.data

                    for (i in stateList?.indices!!) {
                        stateNameList.add(stateList?.get(i)?.state1.toString())
                    }
                    stateSpInit()


                }
                Status.FAILURE -> {
                    makeToast("" + it?.errorMsg)
                }
            }
        })


        addressViewModel.resultCountyData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val dataEvent = it?.data!!
                    countyNameList.clear()
                    countyList = null

                    val countyData = CountyData(0, getString(R.string.select_county_sp), 1, true)
                    dataEvent.data.add(0, countyData)

                    countyList = dataEvent.data
                    // countyNameList.add("Select county")
                    for (i in countyList?.indices!!) {
                        countyNameList.add(countyList?.get(i)?.county1.toString())
                    }
                    countySpInit()

                }
                Status.FAILURE -> {
                    makeToast("" + it?.errorMsg)
                }
            }
        })
    }

    val dateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

    /*Date Format*/
    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        et_dob!!.setText(sdf.format(cal.time))
        dob = sdf.format(cal.time)
    }

    /*Gender Spinner Initialise*/
    private fun genderSpInit() {

        val genderAdapter =
            ArrayAdapter(this, R.layout.spinner_list_item, gender_list)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_gender!!.adapter = genderAdapter
        val spinnerPosition: Int = genderAdapter.getPosition(gender)
        sp_gender.setSelection(spinnerPosition)
        sp_gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0)
                    gender = gender_list.get(position).toString()
                else
                    gender = ""
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }
    }

    /*State Spinner Initialise*/
    private fun stateSpInit() {

        val state_adapter =
            ArrayAdapter(this, R.layout.spinner_list_item, stateNameList)
        state_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_province!!.adapter = state_adapter
        val spinnerPosition: Int = state_adapter.getPosition(state)
        sp_province.setSelection(spinnerPosition)
        sp_province.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (parent.getItemAtPosition(position) == stateList?.get(position)?.state1 && position > 0) {

                    state = stateList?.get(position)?.state1.toString()
                    stateId = stateList?.get(position)?.id!!


                    addressViewModel.getCountyList(stateId.toString(), token)
                    //  getCountyList(stateList?.get(position)?.id.toString())
                } else {
                    state = ""
                    stateId = 0
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }
    }

    /*county Spinner Initialise*/
    private fun countySpInit() {
        val county_adapter =
            ArrayAdapter(this, R.layout.spinner_list_item, countyNameList)
        county_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_county!!.adapter = county_adapter
        val spinnerPosition: Int = county_adapter.getPosition(county)
        sp_county.setSelection(spinnerPosition)
        sp_county.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent?.getItemAtPosition(position)!! == (countyList?.get(position)?.county1) && position > 0) {
                    //getStateList(countyList?.get(position)?.id.toString())
                    county = countyList?.get(position)?.county1.toString()
                    countyId = countyList?.get(position)?.id!!

                } else {
                    county = ""
                    countyId = 0
                }

            }

        }
    }


}