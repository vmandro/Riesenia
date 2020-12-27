package com.example.httpupload

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitInterface {
    @GET
    fun getJson(@Url url: String): Call<ResponseBody?>

}
