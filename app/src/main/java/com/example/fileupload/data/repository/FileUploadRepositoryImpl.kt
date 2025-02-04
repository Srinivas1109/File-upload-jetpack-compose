package com.example.fileupload.data.repository

import android.net.Uri
import com.example.fileupload.data.model.FileInfo
import com.example.fileupload.domain.model.FileUploadStatus
import com.example.fileupload.domain.model.MultipleFilesUploadStatus
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

    override suspend fun uploadFiles(uris: List<Uri>): Flow<MultipleFilesUploadStatus> {
        val bytesList = mutableListOf<ByteArray>()
        val typesList = mutableListOf<String>()
        uris.forEach { uri ->
            val bytes = fileManager.readBytesFromUri(uri)
            val type = fileManager.getFileTypeFromUri(uri)
            typesList.add(type)
            bytesList.add(bytes)
        }

        val fileInfos = uris.mapIndexed { index, uri ->
            FileInfo(
                name = UUID.randomUUID().toString(),
                type = typesList[index],
                uri = uri,
                bytes = bytesList[index]
            )
        }

        return fileUploadApi.uploadFiles(fileInfos)
    }
}