package com.example.fileupload.data.remote

import android.util.Log
import com.example.fileupload.data.model.FileInfo
import com.example.fileupload.domain.model.FileUploadStatus
import com.example.fileupload.domain.model.MultipleFilesUploadStatus
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
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
                "FileUpload",
                "Status: ${response.status}, Name: ${fileInfo.name}"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun uploadFiles(fileInfos: List<FileInfo>): Flow<MultipleFilesUploadStatus> =
        channelFlow {
            try {
                val responses = fileInfos.mapIndexed { index, fileInfo ->
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
                                        "filename=\"${fileInfo.name}.png\""
                                    )
                                })
                        },
                    )

                    async {
                        delay((index + 1) * 2000L)
                        Log.d(
                            "MultipleFileUpload",
                            "Uploading ${index + 1} after delay ${(index + 1) * 2000L}"
                        )
                        client.post("https://tmpfiles.org/api/v1/upload") {
                            setBody(content)
                        }
                    }
                }

                responses.forEachIndexed { index, response ->
                    val res = response.await()
                    Log.d(
                        "MultipleFileUpload",
                        "Status: ${res.status}"
                    )
                    if (res.status == HttpStatusCode.OK) {
                        trySend(
                            MultipleFilesUploadStatus(
                                totalFiles = fileInfos.size,
                                noOfFilesUploaded = index + 1
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

}