package com.pollvote.view.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pollvote.R
import com.pollvote.annotations.Status
import com.pollvote.utils.ProgressUtils
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import com.pollvote.utils.Static
import com.pollvote.model.county.CountyData
import com.pollvote.model.states.StateData
import com.pollvote.viewModel.AddressViewModel
import kotlinx.android.synthetic.main.activity_change_personal_detail.*

import kotlinx.android.synthetic.main.activity_fill_detail.*
import kotlinx.android.synthetic.main.activity_fill_detail.btn_continue
import kotlinx.android.synthetic.main.activity_fill_detail.et_dob
import kotlinx.android.synthetic.main.activity_fill_detail.et_vill_city
import kotlinx.android.synthetic.main.activity_fill_detail.et_zip
import kotlinx.android.synthetic.main.activity_fill_detail.img_back
import kotlinx.android.synthetic.main.activity_fill_detail.sp_county
import kotlinx.android.synthetic.main.activity_fill_detail.sp_gender
import kotlinx.android.synthetic.main.activity_fill_detail.sp_province
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/*Save address in this activity implemented*/
class
FillDetailActivity : BaseActivity() {
    var cal = Calendar.getInstance()
    var genderList = ArrayList<String>()

    private val stateNameList = ArrayList<String>()
    private val countyNameList = ArrayList<String>()
    var countyList: List<CountyData?>? = null
    var stateList: List<StateData?>? = null

    var county: String = ""
    var state: String = ""
    var countyId: Int = 0
    var stateId: Int = 0
    var gender: String = ""
    var isLogin = false
    var setToolbarTitle = Static()
    var token = ""

    private lateinit var viewModel: AddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_detail)

        // initialize view model
        viewModel = ViewModelProvider(this).get(AddressViewModel::class.java)

        // initialize observer
        initObserver()

        setToolbarTitle.statusBarColor(this)

        genderList.add(getString(R.string.select_gender_sp))
        genderList.add(getString(R.string.male))
        genderList.add(getString(R.string.female))

        token = SharedPrefrencesUtils.getToken().toString()
        initUI()

        // call state list API to fetch list of states
        viewModel.getStateList("1", token)
        genderSpInit()
    }

    private fun initUI() {
        try {
            isLogin = intent.getBooleanExtra("isLogin", false)
            tv_id_value.setText("PV" + SharedPrefrencesUtils.getUserId())


        } catch (e: NullPointerException) {

        }
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        btn_continue.setOnClickListener {

            if (isValid()) {
                val address = et_house_number.text.toString()
                val town = et_vill_city.text.toString()
                val dob = et_dob.text.toString()
                val zip = et_zip.text.toString()
                viewModel.saveAddressDetail(
                    token,
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


        et_dob.setOnClickListener {
            val picker = DatePickerDialog(
                this@FillDetailActivity,
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

        img_back.setOnClickListener {
            finish()
        }

    }

    // check validations
    private fun isValid(): Boolean {
        var is_valid = true
        if (et_house_number.text.toString() == "") {
            et_house_number.requestFocus()
            et_house_number.error = getString(R.string.enter_address)
            is_valid = false
        }
        if (et_vill_city.text.toString() == "") {
            et_vill_city.error = getString(R.string.enter_city_town)
            is_valid = false

        }

        if (state.isEmpty()) {
            makeToast(getString(R.string.select_state))
            is_valid = false

        } else if (county.isEmpty()) {
            makeToast(getString(R.string.select_county))
            is_valid = false

        } else if (gender.isEmpty()) {
            sp_gender.requestFocus()
            makeToast(getString(R.string.select_gender))
            is_valid = false

        }


        if (et_dob.text.toString() == "") {
            et_dob.error = getString(R.string.select_dob)
            is_valid = false

        }
        if (et_zip.text.toString() == "") {
            et_zip.error = getString(R.string.enter_zip_code)
            is_valid = false

        }
        return is_valid
    }

    // Init Observer to observe API call initiate and result.
    private fun initObserver() {
        viewModel.isLoading.observe(this,
            Observer {
                if (it) {
                    ProgressUtils.showLoadingDialog(this)
                } else {
                    ProgressUtils.cancelLoading()
                }
            })

        viewModel.resultStatesData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    stateList = null
                    stateNameList.clear()
                    val stateData = StateData(0, getString(R.string.select_province), 0, true)
                    it?.data?.data?.add(0, stateData)
                    stateList = it?.data?.data

                    for (i in stateList?.indices!!) {
                        stateNameList.add(stateList?.get(i)?.state1.toString().trim())
                    }
                    stateSpInit()

                }
                Status.FAILURE -> {
                    it.errorMsg?.let { it1 -> makeToast(it1) }
                }
            }
        })

        viewModel.resultCountyData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    countyNameList.clear()
                    val countyData = CountyData(0, getString(R.string.select_county_sp), 0, true)
                    it?.data?.data?.add(0, countyData)
                    countyList = null
                    countyList = it?.data?.data

                    for (i in countyList?.indices!!) {
                        countyNameList.add(countyList?.get(i)?.county1.toString())
                    }
                    countySpInit()
                }
                Status.FAILURE -> {
                    it.errorMsg?.let { it1 -> makeToast(it1) }
                }

            }

        })

        viewModel.resultSaveAddressData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.message?.let { it1 -> makeToast(it1) }
                    SharedPrefrencesUtils.setAddress(it.data?.data?.addressLine1 + ", " + it.data?.data?.twonShip)

                    if (isLogin) {
                        startActivity(
                            Intent(this@FillDetailActivity, EventListActivity::class.java)
                        )
                    } else {
                        startActivity(
                            Intent(this@FillDetailActivity, DocumentListActivity::class.java)
                        )
                    }
                    finish()

                }
                Status.FAILURE -> {
                    it.errorMsg?.let { it1 -> makeToast(it1) }
                }
            }
        })
    }

    /*Gender Spinner Initialise*/
    private fun genderSpInit() {

        val genderAdapter = ArrayAdapter(this, R.layout.spinner_list_item, genderList)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_gender!!.adapter = genderAdapter

        sp_gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0)
                    gender = genderList.get(position).toString()
                else
                    gender=""

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }
    }

    /*State Spinner Initialise*/
    private fun stateSpInit() {

        val state_adapter = ArrayAdapter(this, R.layout.spinner_list_item, stateNameList)
        state_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_province!!.adapter = state_adapter

        sp_province.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (parent.getItemAtPosition(position)
                        .equals(stateList?.get(position)?.state1) && position > 0
                ) {

                    state = stateList?.get(position)?.state1.toString()
                    stateId = stateList?.get(position)?.id!!

                    // call state list API to fetch list of states
                    viewModel.getCountyList(stateList?.get(position)?.id.toString(), token)
                }else{
                    stateId=0
                    state=""
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }
    }

    /*County Spinner Initialise*/
    private fun countySpInit() {
        val county_adapter =
            ArrayAdapter(this, R.layout.spinner_list_item, countyNameList)
        county_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_county!!.adapter = county_adapter

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
                if (parent?.getItemAtPosition(position)!! == countyList?.get(position)?.county1
                    && position > 0
                ) {
                    //getStateList(countyList?.get(position)?.id.toString())
                    county = countyList?.get(position)?.county1.toString()
                    countyId = countyList?.get(position)?.id!!

                }else{
                    countyId=0
                    county=""
                }

            }

        }
    }

    /*Date Format*/
    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        et_dob!!.setText(sdf.format(cal.time))
    }

}