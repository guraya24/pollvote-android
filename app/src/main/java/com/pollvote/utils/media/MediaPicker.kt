package com.pollvote.utils.media

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider.getUriForFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File

/**
 * Media Picker is used to fetch image from gallery or capture with camera
 * Permissions added with dexter library.
 * Options list dialog
 */


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MediaPicker {

    companion object {
        var fileName: String = ""

        val REQUEST_IMAGE_CAPTURE = 0
        val REQUEST_GALLERY_IMAGE = 1
        val REQUEST_DOC_PICK = 2

        var fragment: Fragment? = null

        // open camera
        fun takeCameraImage(activity: FragmentActivity?) {
            Dexter.withContext(activity)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {

                            fileName = System.currentTimeMillis().toString() + ".jpg"
                            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            takePictureIntent.putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                getCacheImagePath(activity, fileName)
                            )


                            if (fragment != null)
                                fragment?.startActivityForResult(
                                    takePictureIntent,
                                    REQUEST_IMAGE_CAPTURE
                                )
                            else {
//                            if (activity?.packageManager?.let { takePictureIntent.resolveActivity(it) } != null) {
                                activity?.startActivityForResult(
                                    takePictureIntent,
                                    REQUEST_IMAGE_CAPTURE
                                )
                            }
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

//        @Throws(IOException::class)
//        private fun createImageFile(): : Uri?{
//            // Create an image file name
//            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//            val imageFileName = "JPEG_" + timeStamp + "_"
//            val storageDir: File = File(Environment.getStorageDirectory(),"PollVote")
//            if(!storageDir.exists())
//                storageDir.createNewFile()
//            val image = File.createTempFile(
//                imageFileName,  // prefix
//                ".jpg",  // suffix
//                storageDir // directory
//            )
//
//            // Save a file: path for use with ACTION_VIEW intents
//            mCurrentPhotoPath = "file:" + image.absolutePath
//            return image
//        }

        // created file to store
        fun getCacheImagePath(activity: FragmentActivity?, fileName: String): Uri? {
            val path = File(activity?.externalCacheDir, "camera")
            if (!path.exists()) path.mkdirs()
            val image = File(path, fileName)
            return activity?.let {
                getUriForFile(
                    it,
                    "com.pollvote.provider",
                    image
                )
            }
        }


        // show options in dialog
        fun showImagePickerOptions(activity: FragmentActivity?, fragment1: Fragment?) {
            fragment = fragment1
            // setup the alert builder
            val builder: AlertDialog.Builder? = activity?.let { AlertDialog.Builder(it) }
            builder?.setTitle("Select")

            // add a list
            val list = arrayOf<String>(
                "Camera",
                "Choose from Gallery",
                "Cancel"
            )
            builder?.setItems(list) { dialog, which ->
                when (which) {
                    0 -> takeCameraImage(activity)
                    1 -> chooseImageFromGallery(activity)
                    2 -> dialog.dismiss()
                }
            }

            // create and show the alert dialog
            val dialog: AlertDialog? = builder?.create()
            dialog?.show()
        }

        // choose from gallery
        fun chooseImageFromGallery(activity: FragmentActivity?) {
            Dexter.withContext(activity)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            val pickPhoto = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            if (fragment != null)
                                fragment?.startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE)
                            else
                                activity?.startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }

        // get destination path from storage
        @SuppressLint("Recycle")
        private fun queryName(resolver: ContentResolver, uri: Uri): String? {
            val returnCursor: Cursor = resolver.query(uri, null, null, null, null)!!
            val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            val name: String = returnCursor.getString(nameIndex)
            returnCursor.close()
            return name
        }

        // clear used cache
        private fun clearCache(context: Context) {
            val path = File(context.cacheDir, "camera")
            if (path.exists() && path.isDirectory) {
               val files=path.listFiles()
                if (files != null)
                    for (child in files) {
                        child.delete()
                    }
            }
        }

        // get destination URI
        fun getFileImagePath(activity: FragmentActivity?, sourceUri: Uri): Uri {
            val destinationUri = Uri.fromFile(
                File(
                    activity?.externalCacheDir,
                    queryName(activity?.getContentResolver()!!, sourceUri)
                )
            )
            return destinationUri
        }
    }

}