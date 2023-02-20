package com.pollvote.network.exceptions

import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.HttpsURLConnection

object ErrorHandler {
    fun reportError(error: Throwable): String? {
        return if (error is HttpException) {
            when (error.code()) {
                HttpsURLConnection.HTTP_UNAUTHORIZED -> "Unauthorised User"
                HttpsURLConnection.HTTP_FORBIDDEN -> "Forbidden"
                HttpsURLConnection.HTTP_INTERNAL_ERROR -> "Internal Server Error"
                HttpsURLConnection.HTTP_BAD_REQUEST -> "Bad Request"
                else -> error.getLocalizedMessage()
            }
        } else if (error is WrapperError) {
            error.message
        } else if (error is JsonSyntaxException) {
            "Something Went Wrong API is not responding properly!"
        } else if (error is SocketTimeoutException) {
            "Sorry cannot connect to the server!"
        }  else if (error is UnknownHostException) {
            "Sorry cannot connect to the server!"
        } else {
            error.message
        }
    }

    fun reportError(errorCode: Int): String {
        var errorMessage = ""
        when (errorCode) {
            HttpsURLConnection.HTTP_UNAUTHORIZED -> errorMessage = "Unauthorised User"
            HttpsURLConnection.HTTP_FORBIDDEN -> errorMessage = "Forbidden"
            HttpsURLConnection.HTTP_INTERNAL_ERROR -> errorMessage =
                "Internal Server Error"
            HttpsURLConnection.HTTP_BAD_REQUEST -> errorMessage = "Bad Request"
        }
        return errorMessage
    }
}