package com.example.fileupload.domain.repository

import android.net.Uri
import com.example.fileupload.domain.model.FileUploadStatus
import com.example.fileupload.domain.model.MultipleFilesUploadStatus
import kotlinx.coroutines.flow.Flow

interface FileUploadRepository {
    suspend fun uploadFile(uri: Uri): Flow<FileUploadStatus>
    suspend fun uploadFiles(uris: List<Uri>): Flow<MultipleFilesUploadStatus>
}