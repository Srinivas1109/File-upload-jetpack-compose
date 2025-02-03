package com.example.fileupload.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.content.ProgressListener
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import java.util.UUID

class FileUploadApi(private val client: HttpClient, private val context: Context) {

    data class FileUploadProgress(
        val progress: Int,
        val uri: Uri,
        val total: Int,
        val name: String
    )

    suspend fun uploadSingleImage(uri: Uri, listener: ProgressListener) {
        try {

//            val filePath = uri.path ?: throw IllegalArgumentException("Invalid file path")
//            val file = File(filePath)
//            require(file.exists()) { "File not found: $filePath" }
            val fileName = UUID.randomUUID().toString()
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes() ?: return

            val content = MultiPartFormDataContent(formData {
                append("description", "user uploaded image")
                append(
                    "file",
                    bytes,
                    Headers.build {
                        append(HttpHeaders.ContentType, "image/png")
                        append(
                            HttpHeaders.ContentDisposition,
                            "filename=\"${fileName}.png\""
                        )
                    })
            })

            val response = client.post("https://tmpfiles.org/api/v1/upload") {
                setBody(content)
                onUpload(listener)
            }

            Log.d("UploadSingleImage", "Status: ${response.status}, Name: $fileName.png")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}