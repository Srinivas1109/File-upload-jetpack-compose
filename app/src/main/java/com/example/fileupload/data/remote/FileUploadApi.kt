package com.example.fileupload.data.remote

import android.content.Context
import android.util.Log
import com.example.fileupload.data.dto.UploadFileDto
import com.example.fileupload.data.dto.UploadFileStatus
import io.ktor.client.HttpClient
import io.ktor.client.content.ProgressListener
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class FileUploadApi(private val client: HttpClient, private val context: Context) {

    fun uploadSingleImage(uploadFile: UploadFileDto): Flow<UploadFileStatus> = channelFlow {
        try {

            val bytes = context.contentResolver.openInputStream(uploadFile.uri)?.readBytes()
                ?: byteArrayOf()

            val content = MultiPartFormDataContent(
                formData {
                    append("description", "user uploaded image")
                    append(
                        "file",
                        bytes,
                        Headers.build {
                            append(HttpHeaders.ContentType, "image/png")
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=\"temp.png\""
                            )
                        })
                },
            )

            val response = client.post("https://tmpfiles.org/api/v1/upload") {
                setBody(content)
                onUpload(
                    object : ProgressListener {
                        override suspend fun onProgress(
                            bytesSentTotal: Long,
                            contentLength: Long?
                        ) {
                            trySend(
                                UploadFileStatus(
                                    id = uploadFile.id,
                                    filename = uploadFile.filename,
                                    totalBytes = contentLength ?: 0L,
                                    totalBytesUploaded = bytesSentTotal
                                )
                            )
                        }
                    }
                )
            }

            Log.d(
                "UploadSingleImage",
                "Status: ${response.status}, Name: ${uploadFile.filename}.png, Id: ${uploadFile.id}"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}