package com.pollvote.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.androidbuffer.kotlinfilepicker.KotConstants
import com.androidbuffer.kotlinfilepicker.KotRequest
import com.androidbuffer.kotlinfilepicker.KotResult
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pollvote.R
import com.pollvote.annotations.Status
import com.pollvote.utils.ProgressUtils
import com.pollvote.utils.Static
import com.pollvote.utils.media.FileUtils
import com.pollvote.utils.media.MediaPicker
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import com.pollvote.view.dialogs.ChooseMediaDialog
import com.pollvote.view.dialogs.LogoutDialog
import com.pollvote.view.interfaces.BottomDialogListener
import com.pollvote.viewModel.UserProfileViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_event_detail.img_back
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.progress
import kotlinx.android.synthetic.main.content_preview.*
import java.lang.Exception
import java.util.*


class ProfileDetail : BaseActivity(), BottomDialogListener {
    val static = Static()
    var REQUEST_CAMERA = 101
    private lateinit var viewModel: UserProfileViewModel
    var token = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val setToolbarTitle = Static()
        setToolbarTitle.statusBarColor(this)
        viewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)
        token = SharedPrefrencesUtils.getToken().toString()
        initUI()
        initObserver()
    }

    @SuppressLint("SetTextI18n")
    override fun onRestart() {
        super.onRestart()
        val name = SharedPrefrencesUtils.getUserName()
        txt_user_id.text = "ID-PV" + SharedPrefrencesUtils.getUserId()
        txt_user_name.text = SharedPrefrencesUtils.getUserName()

        if (SharedPrefrencesUtils.getUserName()!!.contains(" ")) {
            val strNickName = SharedPrefrencesUtils.getUserName()?.split(" ")
            val fNic = strNickName!![0].toUpperCase(Locale.ROOT)
            val lNic = strNickName[1].toUpperCase(Locale.ROOT)
            val firstChar: Char = fNic[0]
            val secondChar: Char = lNic[0]

            txt_user_nick_name.text = (firstChar + "" + secondChar).toString()
        } else {
            txt_user_nick_name.text = name?.toUpperCase(Locale.ROOT)
        }

        if (SharedPrefrencesUtils.getUserImage() != "" && SharedPrefrencesUtils.getUserImage() != null) {

            val imageLink = SharedPrefrencesUtils.getUserImage()
            // profile_image.setImageResource(imageLink)
            //Picasso.get().load(imageLink).into(profile_image)
            Glide.with(this).load(imageLink).into(profile_image)
        }
    }

    @SuppressLint("SetTextI18n")
    fun initUI() {
        val name = SharedPrefrencesUtils.getUserName()
        txt_user_id.text = "ID-PV" + SharedPrefrencesUtils.getUserId()
        txt_user_name.text = SharedPrefrencesUtils.getUserName()



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
            // profile_image.setImageResource(imageLink)
            //Picasso.get().load(imageLink).into(profile_image)
            Glide.with(this).load(imageLink).into(profile_image)
        }

        img_back.setOnClickListener {
            finish()
        }

        rl_change_password.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
        rl_personal_detail.setOnClickListener {
            startActivity(Intent(this, ChangePersonalDetailActivity::class.java))
        }
        rl_mobile.setOnClickListener {
            startActivity(Intent(this, ChangeMobileNumberActivity::class.java))
        }

        rl_logout.setOnClickListener {

            val dialogFragment = LogoutDialog()
            dialogFragment.show(supportFragmentManager, "Logout")
            //static.staticDialog(this, "Logout", "Are you sure to logout!")

        }

        fl_profile_image.setOnClickListener {
            ChooseMediaDialog.newInstance().show(supportFragmentManager, "ChooseMediaDialog")
        }
    }

    override fun onCamera() {
//        MediaPicker.takeCameraImage(this)
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        KotRequest.Camera(this@ProfileDetail).setRequestCode(REQUEST_CAMERA).pick()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()

    }

    override fun onGallery() {
        MediaPicker.chooseImageFromGallery(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {


            MediaPicker.REQUEST_IMAGE_CAPTURE -> if (resultCode == Activity.RESULT_OK) {

                val resultUri = MediaPicker.getCacheImagePath(this, MediaPicker.fileName)?.let {
                    MediaPicker.getFileImagePath(
                        this,
                        it
                    )
                }

//                val resultUri =
//                    MediaPicker.fileName?.let { MediaPicker.getFileImagePath(this, it) }

                Log.e("TAG", "Crop : $resultUri")
                // Call API to upload profile image
                viewModel.uploadProfileImage(
                    token,
                    resultUri?.path.toString()
                )
            }
            MediaPicker.REQUEST_GALLERY_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                val imageUri = data?.data
                FileUtils.getPath(this, imageUri)
                val resultUri = FileUtils.getPath(
                    this,
                    imageUri
                )// imageUri?.let { MediaPicker.getFileImagePath(this, it) }
                Log.e("TAG", "Crop : $resultUri")

                // Call API to upload profile image
                viewModel.uploadProfileImage(
                    token,
                    resultUri.toString()
                )
            }


        }

        if (REQUEST_CAMERA == requestCode && resultCode == Activity.RESULT_OK) {

            val result =
                data?.getParcelableArrayListExtra<KotResult>(KotConstants.EXTRA_FILE_RESULTS)

            val resultUri = result!![0].uri
            Log.e("TAG", "Crop : $resultUri")
            // Call API to upload profile image
            viewModel.uploadProfileImage(
                token,
                resultUri.path.toString()
            )
            //  startGalleryView(result!!)

        }
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

        viewModel.resultProfileImageData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progress.visibility = View.VISIBLE
                    SharedPrefrencesUtils.setUserImage(it.data?.data.toString())
                    val imageLink = it.data?.data
                    Picasso.get()
                        .load(imageLink)
                        .into(profile_image, object : Callback {
                            override fun onSuccess() {
                                progress.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                progress.visibility = View.GONE

                            }


                        })
/*
                    try {
                        Glide.with(this)
                            .asBitmap()
                            .load(imageLink)
                            .listener(object : RequestListener<Bitmap> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Bitmap>?,
                                    isFirstResource: Boolean
                                ): Boolean {

                                    progress.visibility = View.GONE
                                    return true
                                }

                                override fun onResourceReady(
                                    resource: Bitmap?,
                                    model: Any?,
                                    target: Target<Bitmap>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    progress.visibility = View.GONE

                                    return true
                                }
                            })

                            .into(profile_image)
                    }catch (e:Exception){

                    }
*/
                    //    Picasso.get().load(imageLink).into(profile_image)

                }
                Status.FAILURE -> {
                    it.errorMsg?.let { it1 -> makeToast(it1) }
                }
            }
        })
    }
}