package com.example.fileupload.data.repository

import com.example.fileupload.data.dto.UploadFileDto
import com.example.fileupload.data.dto.UploadFileStatus
import com.example.fileupload.data.remote.FileUploadApi
import kotlinx.coroutines.flow.Flow

class FileUploadRepository(private val fileUploadApi: FileUploadApi) {

    fun uploadSingleImage(uploadFile: UploadFileDto) : Flow<UploadFileStatus>{
        return fileUploadApi.uploadSingleImage(uploadFile)
    }
}