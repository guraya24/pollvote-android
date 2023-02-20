package com.pollvote.utils.sharedPreference

import android.content.Context
import android.content.SharedPreferences
import android.location.Address

object SharedPrefrencesUtils {

    private const val SHARED_PREFERENCES = "POLLVOTE"
    private const val SHARED_PREFERENCES_REMEMBER = "REMEMBERPOLLVOTE"
    private var sPreferences: SharedPreferences? = null
    private var sPreferencesRemember: SharedPreferences? = null
    private const val IS_USER_LOGIN = "IS_USER_LOGIN"
    private const val USER_ID = "USER_ID"
    private const val TOKEN = "Token"
    private const val USER_NAME = "USER_NAME"
    private const val USER_EMAIL = "USER_EMAIL"
    private const val USER_PHONE = "USER_PHONE"
    private const val voterId = "voterId"
    private const val DOB = "DOB"
    private const val Country = "Country"
    private const val ADDRESS = "address"
    private const val State = "State"
    private const val City = "City"
    private const val Pin = "Pin"
    private const val Gender = "Gender"
    private const val LastName = "LastName"
    private const val FcmToken = "FcmToken"
    private const val USER_IMAGE = "UserImage"
    private const val DeviceId = "DeviceId"
    private const val drivingLicenseNO = "DrivingLicense"
    const val DeviceType = "Android"
    const val Bearer = "Bearer "
    const val Vote = "vote "
    const val Poll = "poll "
    const val EventId = "eventId "
    const val eventType = "EventType "
    private const val rememberUserId = "rememberUserId "
    private const val rememberUserPassword = "rememberUserPassword "
    // private const val polledEventId = "polledEventId"


    var isVote = true
    var isPoll = false

    fun init(context: Context?) {
        sPreferences = context?.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }
    fun initRemember(context: Context?) {
        sPreferencesRemember = context?.getSharedPreferences(SHARED_PREFERENCES_REMEMBER, Context.MODE_PRIVATE)
    }

    fun isUserLogin(): Boolean? {
        return sPreferences?.getBoolean(IS_USER_LOGIN, false)
    }


    /*fun getPolledEventId(): MutableSet<String>? {
        return sPreferences?.getStringSet(polledEventId, null)
    }

    fun setPolledEventId(Ids:HashSet<String>) {
        sPreferences?.edit()
            ?.putStringSet(polledEventId, Ids)
            ?.apply()
    }*/

    //Remember user pass
    fun getUserPassword(): String? {
        return sPreferencesRemember?.getString(rememberUserPassword, "")
    }

    fun setUserPassword(pass: String) {
        sPreferencesRemember?.edit()
            ?.putString(rememberUserPassword, pass)
            ?.apply()
    }

    //Remember user Id
    fun getUserID(): String? {
        return sPreferencesRemember?.getString(rememberUserId, "")
    }

    fun setUserID(pass: String) {
        sPreferencesRemember?.edit()
            ?.putString(rememberUserId, pass)
            ?.apply()
    }

    fun getUserId(): String? {
        return sPreferences?.getString(USER_ID, "")
    }

    fun getUserName(): String? {
        return sPreferences?.getString(USER_NAME, "")
    }

    fun getUserEmail(): String? {
        return sPreferences?.getString(USER_EMAIL, "")
    }

    fun getUserNumber(): String? {
        return sPreferences?.getString(USER_PHONE, "")
    }

    fun getLicenceNo(): String? {
        return sPreferences?.getString(drivingLicenseNO, "")
    }

    fun getAddress(): String? {
        return sPreferences?.getString(ADDRESS, "")
    }

    fun getVoterId(): String? {
        return sPreferences?.getString(voterId, "")
    }

    fun getPin(): String? {
        return sPreferences?.getString(Pin, "")
    }

    fun getCountry(): String? {
        return sPreferences?.getString(Country, "")
    }

    fun getState(): String? {
        return sPreferences?.getString(State, "")
    }

    fun getCity(): String? {
        return sPreferences?.getString(City, "")
    }

    fun getDob(): String? {
        return sPreferences?.getString(DOB, "")
    }

    fun getGender(): String? {
        return sPreferences?.getString(Gender, "")
    }

    fun getLast(): String? {
        return sPreferences?.getString(LastName, "")
    }

    fun getToken(): String? {
        return sPreferences?.getString(TOKEN, "")
    }

    fun getFcmToken(): String? {
        return sPreferences?.getString(FcmToken, "")
    }

    fun setFcmToken(token: String) {
        sPreferences?.edit()
            ?.putString(FcmToken, token)
            ?.apply()
    }


    fun getUserImage(): String? {
        return sPreferences?.getString(USER_IMAGE, "")
    }

    fun setUserImage(token: String) {
        sPreferences?.edit()
            ?.putString(USER_IMAGE, token)
            ?.apply()
    }
    fun getDeviceId(): String? {
        return sPreferences?.getString(DeviceId, "")
    }

    fun setDeviceId(device: String) {
        sPreferences?.edit()
            ?.putString(DeviceId, device)
            ?.apply()
    }


    fun setAddress(address: String) {
        sPreferences?.edit()
            ?.putString(ADDRESS, address)
            ?.apply()
    }


    fun setIsUserLogin(status: Boolean) {
        sPreferences?.edit()
            ?.putBoolean(IS_USER_LOGIN, status)
            ?.apply()
    }

    fun setUserLogin(status: Boolean, id: String?) {
        sPreferences?.edit()
            ?.putBoolean(IS_USER_LOGIN, status)
            ?.putString(USER_ID, id)
            ?.apply()
    }

    fun setUserLogin(
        status: Boolean,
        id: String?,
        name: String?,
        email: String?,
        number: String?,
        licenceNo: String,
        voterid: String,
        address: String,
        token: String
    ) {
        sPreferences?.edit()
            ?.putBoolean(IS_USER_LOGIN, status)
            ?.putString(USER_ID, id)
            ?.putString(USER_NAME, name)
            ?.putString(USER_EMAIL, email)
            ?.putString(USER_PHONE, number)
            ?.putString(drivingLicenseNO, licenceNo)
            ?.putString(voterId, voterid)
            ?.putString(ADDRESS, address)
            ?.putString(TOKEN, Bearer + token)
            ?.apply()
    }

    fun setUserLogin(
        status: Boolean,
        id: String?,
        name: String?,
        number: String?,
        licenceNo: String,
        voterid: String,
        token: String
    ) {
        sPreferences?.edit()
            ?.putBoolean(IS_USER_LOGIN, status)
            ?.putString(USER_ID, id)
            ?.putString(USER_NAME, name)
            ?.putString(USER_PHONE, number)
            ?.putString(drivingLicenseNO, licenceNo)
            ?.putString(voterId, voterid)
            ?.putString(TOKEN, Bearer + token)
            ?.apply()
    }


    fun setUserLogin(
        name: String?,
        Address: String?,
        state: String?,
        city: String,
        dob: String,
        gender: String,
        zip: String
    ) {
        sPreferences?.edit()
            ?.putString(USER_NAME, name)
            ?.putString(ADDRESS, Address)
            ?.putString(State, state)
            ?.putString(City, city)
            ?.putString(DOB, dob)
            ?.putString(Gender, gender)
            ?.putString(Pin, zip)
            ?.apply()
    }


    fun clearUser() {
        sPreferences?.edit()
            ?.clear()
            ?.apply()
    }


    fun clearUserRemember() {
        sPreferencesRemember?.edit()
            ?.clear()
            ?.apply()
    }
}