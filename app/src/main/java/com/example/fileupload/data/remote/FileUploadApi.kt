package com.example.fileupload.data.remote

import android.util.Log
import com.example.fileupload.data.model.FileInfo
import com.example.fileupload.domain.model.FileUploadStatus
import com.example.fileupload.domain.repository.FileUploadApi
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

class FileUploadApiImpl(private val client: HttpClient) : FileUploadApi {

    override fun uploadFile(fileInfo: FileInfo): Flow<FileUploadStatus> = channelFlow {
        try {
            val content = MultiPartFormDataContent(
                formData {
                    append("description", "user uploaded file")
                    append(
                        "file",
                        fileInfo.bytes,
                        Headers.build {
                            append(HttpHeaders.ContentType, fileInfo.type)
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=\"${fileInfo.name}\""
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
                                FileUploadStatus(
                                    filename = fileInfo.name,
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
                "Status: ${response.status}, Name: ${fileInfo.name}"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}