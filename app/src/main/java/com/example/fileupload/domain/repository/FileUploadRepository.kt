package com.example.fileupload.domain.repository

import android.net.Uri
import com.example.fileupload.domain.model.FileUploadStatus
import kotlinx.coroutines.flow.Flow

interface FileUploadRepository {
    suspend fun uploadFile(uri: Uri): Flow<FileUploadStatus>
}