package com.example.fileupload.data.repository

import android.net.Uri
import com.example.fileupload.data.remote.FileUploadApi
import io.ktor.client.content.ProgressListener

class FileUploadRepository(private val fileUploadApi: FileUploadApi) {
    suspend fun uploadSingleImage(uri: Uri, listener: ProgressListener) {
        fileUploadApi.uploadSingleImage(uri, listener)
    }
}