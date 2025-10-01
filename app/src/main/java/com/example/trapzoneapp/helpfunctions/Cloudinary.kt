package com.example.trapzoneapp.helpfunctions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.cloudinary.json.JSONObject

suspend fun uploadToCloudinary(imageBytes: ByteArray): String? {
    return withContext(Dispatchers.IO) {
        val url = "https://api.cloudinary.com/v1_1/dl2vplait/image/upload"
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("upload_preset", "profile_pics")
            .addFormDataPart(
                "file", "photo.jpg",
                imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Cloudinary upload failed")
            val json = JSONObject(response.body.string())
            json.getString("secure_url")
        }
    }
}