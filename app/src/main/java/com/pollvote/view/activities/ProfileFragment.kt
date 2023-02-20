package com.pollvote.view.activities//package com.app.doclivecare.profile.view
//
//import android.Manifest
//import android.app.Activity.RESULT_OK
//import android.content.ContentResolver
//import android.content.Context
//import android.content.Intent
//import android.database.Cursor
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.provider.OpenableColumns
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.appcompat.app.AlertDialog
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider.getUriForFile
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
//import com.app.doclivecare.R
//import com.app.doclivecare.annotation.BroadCast
//import com.app.doclivecare.annotation.Status
//import com.app.doclivecare.base.BaseFragment
//import com.app.doclivecare.helper.SharedPrefHelper
//import com.app.doclivecare.interfaces.HomeFragmentSelectedListener
//import com.app.doclivecare.network.LiveCareApplication
//import com.app.doclivecare.network.WebUrl
//import com.app.doclivecare.profile.viewmodel.ProfileViewModel
//import com.karumi.dexter.Dexter
//import com.karumi.dexter.MultiplePermissionsReport
//import com.karumi.dexter.PermissionToken
//import com.karumi.dexter.listener.PermissionRequest
//import com.karumi.dexter.listener.multi.MultiplePermissionsListener
//import com.squareup.picasso.Picasso
//import com.yalantis.ucrop.UCrop
//import kotlinx.android.synthetic.main.fragment_profile.*
//import java.io.File
//
//
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
//class ProfileFragment : BaseFragment() {
//    private var param1: String? = null
//    private var param2: String? = null
//
//    var homeFragmentSelectedListener: HomeFragmentSelectedListener? = null
//
//    var mBankInfoFragment: Fragment? = null
//    var mProfessionalInfoFragment: Fragment? = null
//    var mBasicInfoFragment: Fragment? = null
//    var fileName: String? = null
//    val REQUEST_IMAGE_CAPTURE = 0
//    val REQUEST_GALLERY_IMAGE = 1
//    private var setBitmapMaxWidthHeight: kotlin.Boolean = false
//    private val ASPECT_RATIO_X = 1
//    private var ASPECT_RATIO_Y: Int = 1
//    private var bitmapMaxWidth: Int = 1000
//    private var bitmapMaxHeight: Int = 1000
//    private val IMAGE_COMPRESSION = 80
//    private lateinit var viewModel: ProfileViewModel
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        mBasicInfoFragment = PersonalFragment.newInstance()
//        mProfessionalInfoFragment = WorkFragment.newInstance()
//        mBankInfoFragment = BankFragment.newInstance()
//
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
//
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        homeFragmentSelectedListener = activity as HomeFragmentSelectedListener?
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false)
//    }
//
//    companion object {
//        @JvmStatic
//        fun newInstance() =
//            ProfileFragment().apply {
//                arguments = Bundle().apply {
////                    putString(ARG_PARAM1, param1)
////                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initObserver()
//        //@ initialization
//
//        val data = LiveCareApplication.getInstance()?.getSharedPrefInstance()
//            ?.read(SharedPrefHelper.KEY_USER_DATA)
//        tv_name.text = "Dr. " + data?.firstName + " " + data?.lastName
//        tv_email.text = data?.email
//        tv_speciality.text = LiveCareApplication.getInstance()?.getSharedPrefInstance()
//            ?.read(SharedPrefHelper.KEY_SPECIALITY, "")
//
//        if (data?.profilePic?.isNotEmpty()!!)
//            Picasso.get().load(WebUrl.IMG_URL + data.profilePic)
//                .placeholder(R.drawable.placeholder_wh)
//                .error(R.drawable.placeholder_wh).into(iv_profile)
//
//        setViewChanges(true, false, false)
//
//        tv_personal_info?.setOnClickListener {
//            setViewChanges(true, false, false)
//        }
//        tv_banking_info?.setOnClickListener {
//            setViewChanges(false, true, false)
//        }
//        tv_working_info?.setOnClickListener {
//            setViewChanges(false, false, true)
//        }
//
//        iv_edit_profile_pic?.setOnClickListener {
//            context?.let { it1 -> showImagePickerOptions(it1) }
//        }
//
//
//    }
//
//
//    fun setViewChanges(isPersonal: Boolean, isBanking: Boolean, isWorking: Boolean) {
//
//        tv_personal_info.isSelected = isPersonal
//        tv_banking_info.isSelected = isBanking
//        tv_working_info.isSelected = isWorking
//
//        if (isPersonal) {
//            tv_personal_info.setTextColor(activity?.resources!!.getColor(R.color.colorWhite))
//            tv_banking_info.setTextColor(activity?.resources!!.getColor(R.color.colorPrimary))
//            tv_working_info.setTextColor(activity?.resources!!.getColor(R.color.colorPrimary))
//            showFragment("TAG_BASIC")
//        }
//
//        if (isWorking) {
//            tv_personal_info.setTextColor(activity?.resources!!.getColor(R.color.colorPrimary))
//            tv_banking_info.setTextColor(activity?.resources!!.getColor(R.color.colorPrimary))
//            tv_working_info.setTextColor(activity?.resources!!.getColor(R.color.colorWhite))
//            showFragment("TAG_PROFESSIONAL")
//
//        }
//
//        if (isBanking) {
//            tv_personal_info.setTextColor(activity?.resources!!.getColor(R.color.colorPrimary))
//            tv_banking_info.setTextColor(activity?.resources!!.getColor(R.color.colorWhite))
//            tv_working_info.setTextColor(activity?.resources!!.getColor(R.color.colorPrimary))
//            showFragment("TAG_BANK")
//
//        }
//    }
//
//
//    private fun showFragment(tag: String) {
//        val ft =
//            childFragmentManager.beginTransaction()
//        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//        when (tag) {
//            "TAG_BASIC" -> {
//                if (mBasicInfoFragment?.isAdded()!!)
//                    mBasicInfoFragment?.let { ft.show(it) } else mBasicInfoFragment?.let {
//                    ft.add(
//                        R.id.fl_container, it
//                    )
//                }
//                if (mProfessionalInfoFragment?.isAdded()!!) mProfessionalInfoFragment?.let {
//                    ft.hide(
//                        it
//                    )
//                }
//                if (mBankInfoFragment?.isAdded()!!) mBankInfoFragment?.let { ft.hide(it) }
//                ft.commit()
//            }
//            "TAG_PROFESSIONAL" -> {
//                if (mProfessionalInfoFragment?.isAdded()!!)
//                    mProfessionalInfoFragment?.let { ft.show(it) } else mProfessionalInfoFragment?.let {
//                    ft.add(
//                        R.id.fl_container, it
//                    )
//                }
//                if (mBasicInfoFragment?.isAdded()!!) mBasicInfoFragment?.let { ft.hide(it) }
//                if (mBankInfoFragment?.isAdded()!!) mBankInfoFragment?.let { ft.hide(it) }
//                ft.commit()
//            }
//            "TAG_BANK" -> {
//                if (mBankInfoFragment?.isAdded()!!)
//                    mBankInfoFragment?.let { ft.show(it) } else mBankInfoFragment?.let {
//                    ft.add(
//                        R.id.fl_container, it
//                    )
//                }
//                if (mBasicInfoFragment?.isAdded()!!) mBasicInfoFragment?.let { ft.hide(it) }
//                if (mProfessionalInfoFragment?.isAdded()!!) mProfessionalInfoFragment?.let {
//                    ft.hide(
//                        it
//                    )
//                }
//                ft.commit()
//            }
//        }
//    }
//
//    private fun initObserver() {
//        viewModel.isLoading.observe(viewLifecycleOwner,
//            Observer {
//                if (it) progress_bar?.visibility = View.VISIBLE
//                else progress_bar?.visibility = View.GONE
//            })
//
//        viewModel.isViewEnable.observe(viewLifecycleOwner,
//            Observer { iv_edit_profile_pic?.isClickable = it })
//
//        viewModel.resultUploadImage.observe(viewLifecycleOwner, Observer {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    showSnackBar(it.data?.message.toString())
//                    val data = LiveCareApplication.getInstance()?.getSharedPrefInstance()
//                        ?.read(SharedPrefHelper.KEY_USER_DATA)
//                    data?.profilePic = it.data?.data?.profilepicurl.toString()
//
//                    data?.let { it1 ->
//                        LiveCareApplication.getInstance()?.getSharedPrefInstance()
//                            ?.write(SharedPrefHelper.KEY_USER_DATA, it1)
//                    }
//
//                    val intent = Intent(BroadCast.REFRESH_HOME) //action: "msg"
//                    intent.putExtra("url", it.data?.data?.profilepicurl.toString())
//                    activity?.sendBroadcast(intent)
//                }
//
//                Status.FAILURE -> showSnackBar(it.errorMsg.toString(), R.color.google_red)
//            }
//        })
//    }
//
//    private fun takeCameraImage() {
//        Dexter.withActivity(activity)
//            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            .withListener(object : MultiplePermissionsListener {
//                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                    if (report.areAllPermissionsGranted()) {
//                        fileName = System.currentTimeMillis().toString() + ".jpg"
//                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                        takePictureIntent.putExtra(
//                            MediaStore.EXTRA_OUTPUT,
//                            getCacheImagePath(fileName!!)
//                        )
//                        if (activity?.packageManager?.let { takePictureIntent.resolveActivity(it) } != null) {
//                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//                        }
//                    }
//                }
//
//                override fun onPermissionRationaleShouldBeShown(
//                    permissions: List<PermissionRequest?>?,
//                    token: PermissionToken
//                ) {
//                    token.continuePermissionRequest()
//                }
//            }).check()
//    }
//
//    private fun getCacheImagePath(fileName: String): Uri? {
//        val path = File(activity?.externalCacheDir, "camera")
//        if (!path.exists()) path.mkdirs()
//        val image = File(path, fileName)
//        return activity?.let {
//            getUriForFile(
//                it,
//                "com.app.doclivecare.provider",
//                image
//            )
//        }
//    }
//
//    fun showImagePickerOptions(context: Context) {
//        // setup the alert builder
//        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
//        builder.setTitle(context.getString(R.string.lbl_set_profile_photo))
//
//        // add a list
//        val list = arrayOf<String>(
//            context.getString(R.string.lbl_take_camera_picture),
//            context.getString(R.string.lbl_choose_from_gallery),
//            context.getString(R.string.cancel)
//        )
//        builder.setItems(list) { dialog, which ->
//            when (which) {
//                0 -> takeCameraImage()
//                1 -> chooseImageFromGallery()
//                2 -> dialog.dismiss()
//            }
//        }
//
//        // create and show the alert dialog
//        val dialog: AlertDialog = builder.create()
//        dialog.show()
//    }
//
//    private fun chooseImageFromGallery() {
//        Dexter.withActivity(activity)
//            .withPermissions(
//                Manifest.permission.CAMERA,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            )
//            .withListener(object : MultiplePermissionsListener {
//                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                    if (report.areAllPermissionsGranted()) {
//                        val pickPhoto = Intent(
//                            Intent.ACTION_PICK,
//                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                        )
//                        startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE)
//                    }
//                }
//
//                override fun onPermissionRationaleShouldBeShown(
//                    permissions: List<PermissionRequest>,
//                    token: PermissionToken
//                ) {
//                    token.continuePermissionRequest()
//                }
//            }).check()
//    }
//
//    override fun getRootView(): View {
//        return cl_parent
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        when (requestCode) {
//            REQUEST_IMAGE_CAPTURE -> if (resultCode == RESULT_OK) {
//                cropImage(getCacheImagePath(fileName!!))
//            }
//            REQUEST_GALLERY_IMAGE -> if (resultCode == RESULT_OK) {
//                val imageUri = data?.data
//                cropImage(imageUri)
//            }
//            UCrop.REQUEST_CROP -> if (resultCode == RESULT_OK) {
//                handleUCropResult(data)
//            }
//            UCrop.RESULT_ERROR -> {
//                val cropError = UCrop.getError(data!!)
//            }
//        }
//    }
//
//    private fun cropImage(sourceUri: Uri?) {
//        val destinationUri = Uri.fromFile(
//            File(
//                activity?.getCacheDir(),
//                queryName(activity?.getContentResolver()!!, sourceUri!!)
//            )
//        )
//        val options = UCrop.Options()
//        options.setCompressionQuality(IMAGE_COMPRESSION)
//        options.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
//        options.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
//        options.setActiveWidgetColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
//        options.withAspectRatio(ASPECT_RATIO_X.toFloat(), ASPECT_RATIO_Y.toFloat())
//        if (setBitmapMaxWidthHeight) options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight)
//        UCrop.of(sourceUri!!, destinationUri)
//            .withOptions(options)
//            .start(requireActivity(), this)
//    }
//
//    private fun handleUCropResult(data: Intent?) {
//        if (data == null) {
////            setResultCancelled()
//            return
//        }
//        val resultUri = UCrop.getOutput(data)
//        Log.e("TAG", "Crop : $resultUri")
//        iv_profile.setImageURI(resultUri)
//        viewModel.uploadProfilePic(resultUri?.path)
////        setResultOk(resultUri)
//    }
//
//    private fun queryName(resolver: ContentResolver, uri: Uri): String? {
//        val returnCursor: Cursor = resolver.query(uri, null, null, null, null)!!
//        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//        returnCursor.moveToFirst()
//        val name: String = returnCursor.getString(nameIndex)
//        returnCursor.close()
//        return name
//    }
//
//    fun clearCache(context: Context) {
//        val path = File(context.externalCacheDir, "camera")
//        if (path.exists() && path.isDirectory) {
//            for (child in path.listFiles()) {
//                child.delete()
//            }
//        }
//    }
//}
