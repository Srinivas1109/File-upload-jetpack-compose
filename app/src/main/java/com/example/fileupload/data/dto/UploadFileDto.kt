package com.example.fileupload.data.dto

import android.net.Uri
import java.util.UUID

data class UploadFileDto(
    val id: String = UUID.randomUUID().toString(),
    val filename: String,
    val uri: Uri
)

data class UploadFileStatus(
    val id: String = UUID.randomUUID().toString(),
    val filename: String,
    val totalBytes: Long,
    val totalBytesUploaded: Long,
    val isUploading: Boolean = false,
)