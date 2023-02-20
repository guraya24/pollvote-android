package  com.pollvote.network

import android.content.Context
import android.net.ConnectivityManager

/**
 * This class method checked that internet connection available or not
 */
@Suppress("DEPRECATION")
class NetworkUtil {

    companion object {
        @JvmStatic
        fun isNetAvail(context: Context?): Boolean {
            val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }


    }

}