package com.pollvote.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidbuffer.kotlinfilepicker.KotConstants
import com.androidbuffer.kotlinfilepicker.KotRequest
import com.androidbuffer.kotlinfilepicker.KotResult
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pollvote.R
import com.pollvote.annotations.Status
import com.pollvote.model.getDocumentList.DocumentData
import com.pollvote.utils.ProgressUtils
import com.pollvote.utils.Static
import com.pollvote.utils.media.FileUtils
import com.pollvote.utils.media.MediaPicker
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import com.pollvote.view.adapters.DocumentAdapter
import com.pollvote.view.adapters.DocumentUploadedAdapter
import com.pollvote.view.dialogs.ChooseMediaDialog
import com.pollvote.view.interfaces.BottomDialogListener
import com.pollvote.viewModel.DocumentViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_select_id.*
import kotlinx.android.synthetic.main.content_preview.*
import java.lang.Exception


/*Documents upload functionality is implemented in this activity*/
class DocumentListActivity : BaseActivity(), BottomDialogListener {
    private lateinit var viewModel: DocumentViewModel
    lateinit var rv_document: RecyclerView
    lateinit var documentAdapter: DocumentAdapter
    lateinit var documentUploadedAdapter: DocumentUploadedAdapter
    var docSelectedList = ArrayList<DocumentData>()
    val documentList = ArrayList<DocumentData>()
    var selectedPosition = -1
    var selectedPositionDocument = -1
    var token = ""
    var REQUEST_CAMERA = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_id)
        val setToolbarTitle = Static()
        setToolbarTitle.statusBarColor(this)

        // initialize view model
        viewModel = ViewModelProvider(this).get(DocumentViewModel::class.java)

        // initialize observer
        initObserver()

        token = SharedPrefrencesUtils.getToken().toString()
        img_back.setOnClickListener {
            finish()
        }
        documentAdapter = DocumentAdapter()
        documentUploadedAdapter = DocumentUploadedAdapter()

        // Call API to fetch uploaded documents
        viewModel.getDocumentList(token)

        btn_finish?.setOnClickListener {
            startActivity(Intent(this@DocumentListActivity, EventListActivity::class.java))
            finishAffinity()
        }

        tv_skip?.setOnClickListener {
            startActivity(Intent(this@DocumentListActivity, EventListActivity::class.java))
            finishAffinity()
        }
    }

    // set adapters to show data
    private fun initializeLists(docList: ArrayList<DocumentData>) {
        rv_document = findViewById(R.id.rv_document)

        rv_document.layoutManager = LinearLayoutManager(this)
        rv_document.adapter = documentAdapter
        documentAdapter.update(docList)
        documentAdapter.notifyDataSetChanged()
        documentAdapter.setOnItemClickListener(object : DocumentAdapter.OnItemClickListener {
            override fun onButtonClick(position: Int) {
                selectedPosition = position
                // open dialog to choose image or capture with camera
                ChooseMediaDialog.newInstance().show(supportFragmentManager, "ChooseMediaDialog")
            }

        })
        docSelectedList = ArrayList<DocumentData>()

        for (data in docList) {
            if (data.isUploaded!!)
                docSelectedList.add(data)
        }

        rv_uploaded_document?.layoutManager = LinearLayoutManager(this)
        rv_uploaded_document?.adapter = documentUploadedAdapter
        documentUploadedAdapter.update(docSelectedList)
        documentUploadedAdapter.notifyDataSetChanged()
        documentUploadedAdapter.setOnItemClickListener(object :
            DocumentUploadedAdapter.OnItemClickListener {
            override fun onPreviewClick(position: Int) {
                iv_preview_error.visibility = View.INVISIBLE
                tv_error_message.visibility = View.GONE
                progress.visibility = View.VISIBLE

                /*Picasso.get().load(docSelectedList[position].filePath)
                    .into(iv_preview_imag)*/

                Picasso.get()
                    .load(docSelectedList[position].filePath)

                    .into(iv_preview_imag, object : Callback {
                        override fun onSuccess() {
                            iv_preview_error.visibility = View.INVISIBLE
                            tv_error_message.visibility = View.GONE
                            progress.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            iv_preview_error.visibility = View.VISIBLE
                            tv_error_message.visibility = View.VISIBLE
                            progress.visibility = View.GONE
                            // Picasso.get().load(docSelectedList.get(position).filePath).centerCrop().into(iv_preview_imag)

                              iv_preview_imag.setImageResource(R.drawable.img_not_found)
                        }


                    })

/*
                Glide.with(this@DocumentListActivity)
                    .load(docSelectedList[position].filePath)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            iv_preview_error.visibility = View.VISIBLE
                            tv_error_message.visibility = View.VISIBLE
                            progress.visibility = View.GONE
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            iv_preview_error.visibility = View.INVISIBLE
                            tv_error_message.visibility = View.GONE
                            progress.visibility = View.GONE
//                            Picasso.get().load(docSelectedList.get(position).filePath).centerCrop()
//                               .into(iv_preview_imag)
                            iv_preview_imag.setImageDrawable(resource)
                            return true
                        }
                    })
                    .into(iv_preview_imag)
*/

                layPreview.visibility = View.VISIBLE
            }

            override fun onCancelClick(position: Int) {
                selectedPositionDocument = position
                deleteConfirmation(position)
            }

        })

        iv_cross_preview?.setOnClickListener {
            layPreview.visibility = View.GONE

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

        viewModel.resultDocumentsData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    btn_finish?.visibility = View.VISIBLE
                    tv_skip?.visibility = View.GONE

                    documentList.clear()

                    it?.data?.data?.let { it1 -> documentList.addAll(it1) }
                    initializeLists(documentList)

                    /*todo Check All document is Uploaded. if any one is not uploaded then show skip verification.*/
                    for (i in it?.data?.data!!.indices) {
                        if (it.data!!.data[i].isUploaded == false) {
                            tv_skip.visibility = View.VISIBLE
                            btn_finish.visibility = View.GONE
                            break
                        } else {
                            tv_skip.visibility = View.GONE
                            btn_finish.visibility = View.VISIBLE
                        }
                    }


                }
                Status.FAILURE -> {
                    it.errorMsg?.let { it1 -> makeToast(it1) }
                }
            }
        })

        viewModel.resultUploadedData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    // Update lists after successfully upload document
                    documentList[selectedPosition].filePath =
                        it?.data?.data?.documentFileName.toString()
                    documentList[selectedPosition].isUploaded = true
                    docSelectedList.remove(documentList[selectedPosition])
                    docSelectedList.add(documentList[selectedPosition])
                    documentAdapter.notifyDataSetChanged()
                    documentUploadedAdapter.notifyDataSetChanged()

                    /*todo Check All document is Uploaded or not*/
                    for (i in documentList.indices) {
                        if (documentList[i].isUploaded == false) {
                            tv_skip.visibility = View.VISIBLE
                            btn_finish.visibility = View.GONE
                            break
                        } else {
                            tv_skip.visibility = View.GONE
                            btn_finish.visibility = View.VISIBLE
                        }
                    }



                    it.data?.message?.let { it1 -> makeToast(it1) }
                }
                Status.FAILURE -> {
                    it.errorMsg?.let { it1 -> makeToast(it1) }
                }
            }
        })

        viewModel.resultDeletedDocumentData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    // Update lists after successfully delete document
                    documentAdapter.updateUploadedItem(
                        docSelectedList[selectedPositionDocument].id!!,
                        false
                    )

                    /*todo If any one document is deleted then show skip verification else show finish button*/
                    tv_skip.visibility = View.VISIBLE
                    btn_finish.visibility = View.GONE

                    docSelectedList.removeAt(selectedPositionDocument)
                    documentUploadedAdapter.notifyDataSetChanged()

                    it.data?.message?.let { it1 -> makeToast(it1) }
                }
                Status.FAILURE -> {
                    it.errorMsg?.let { it1 -> makeToast(it1) }
                }
            }
        })
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
                // Call API to upload document
                viewModel.uploadDocumentList(
                    token,
                    documentList[selectedPosition].id!!,
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

                // Call API to upload document
                viewModel.uploadDocumentList(
                    token,
                    documentList[selectedPosition].id!!, resultUri.toString()
                )
            }


        }

        if (REQUEST_CAMERA == requestCode && resultCode == Activity.RESULT_OK) {

            val result =
                data?.getParcelableArrayListExtra<KotResult>(KotConstants.EXTRA_FILE_RESULTS)

            val resultUri = result!![0].uri
            Log.e("TAG", "Crop : $resultUri")
            // Call API to upload document
            viewModel.uploadDocumentList(
                token,
                documentList[selectedPosition].id!!,
                resultUri.path.toString()
            )
            //  startGalleryView(result!!)

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
                        KotRequest.Camera(this@DocumentListActivity).setRequestCode(REQUEST_CAMERA).pick()
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


    // dialog to fetch phone number from user
    private fun deleteConfirmation(pos: Int) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.delete))
        dialog.setMessage(getString(R.string.delete_message))
        dialog.setPositiveButton(
            getString(R.string.delete_)
        ) { dialog1, _ ->
            dialog1.dismiss()
            viewModel.callDeleteDocument(token, docSelectedList[pos].id!!)

        }
        dialog.setNegativeButton(
            getString(R.string.cancel)
        ) { dialog1, _ ->
            dialog1.cancel()
        }

        dialog.create().show()
    }
}