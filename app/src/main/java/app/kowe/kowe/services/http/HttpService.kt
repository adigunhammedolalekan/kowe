package app.kowe.kowe.services.http

import app.kowe.kowe.data.models.Record
import app.kowe.kowe.data.models.SaveResponse
import app.kowe.kowe.data.models.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Part

internal interface HttpService {

    @POST("upload")
    fun upload(@Part body: MultipartBody.Part): Call<UploadResponse>

    @POST("save")
    @Headers("Content-Type: application/json")
    fun saveRecord(@Body record: Record): Call<SaveResponse>

}