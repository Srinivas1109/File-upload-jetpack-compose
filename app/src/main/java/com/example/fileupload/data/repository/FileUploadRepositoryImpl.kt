package com.example.fileupload.data.repository

import android.net.Uri
import com.example.fileupload.data.model.FileInfo
import com.example.fileupload.domain.model.FileUploadStatus
import com.example.fileupload.domain.repository.FileManager
import com.example.fileupload.domain.repository.FileUploadApi
import com.example.fileupload.domain.repository.FileUploadRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class FileUploadRepositoryImpl(
    private val fileUploadApi: FileUploadApi,
    private val fileManager: FileManager
) : FileUploadRepository {

    override suspend fun uploadFile(uri: Uri): Flow<FileUploadStatus> {
        val bytes = fileManager.readBytesFromUri(uri)
        val filetype = fileManager.getFileTypeFromUri(uri)
        return fileUploadApi.uploadFile(
            FileInfo(
                name = UUID.randomUUID().toString(),
                bytes = bytes,
                type = filetype,
                uri = uri
            )
        )
    }
}