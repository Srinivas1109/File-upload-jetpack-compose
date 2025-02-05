package com.example.fileupload.domain.repository

import com.example.fileupload.data.model.FileInfo
import com.example.fileupload.domain.model.FileUploadStatus
import com.example.fileupload.domain.model.MultipleFilesUploadStatus
import kotlinx.coroutines.flow.Flow

interface FileUploadApi {
    fun uploadFile(fileInfo: FileInfo): Flow<FileUploadStatus>
    fun uploadFiles(fileInfos: List<FileInfo>): Flow<MultipleFilesUploadStatus>
}