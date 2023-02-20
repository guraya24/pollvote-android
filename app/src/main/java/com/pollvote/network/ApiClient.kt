package com.pollvote.network

import com.google.gson.GsonBuilder
import com.pollvote.application.App
import com.pollvote.BuildConfig
import com.pollvote.network.exceptions.NoInternetException
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


class ApiClient {
    fun getClient(): ApiInterface {

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .addInterceptor(getHttpLoggingInterceptor()!!).addInterceptor(getNetworkInterceptor())
            .build()

        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
        val apiService = retrofit.create<ApiInterface>(ApiInterface::class.java)
        return apiService
    }

    private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor? {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        return httpLoggingInterceptor
    }

    fun getApiClient(): Retrofit? {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient().newBuilder()
                    .connectTimeout(300, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .writeTimeout(300, TimeUnit.SECONDS)
                    .build()
            )
            .build()

        // val apiService = retrofit.create<ApiInterface>(ApiInterface::class.java)

        return retrofit
    }


    private fun getNetworkInterceptor(): Interceptor {
        return object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val isConnected: Boolean = NetworkUtil.isNetAvail(App.getInstance())
                if (!isConnected) {
                    throw NoInternetException(); // Throwing our custom exception 'InternetException'
                }
                val builder = chain.request().newBuilder()
                return chain.proceed(builder.build())
            }
        }
    }
}