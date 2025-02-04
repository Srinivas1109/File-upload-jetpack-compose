package com.example.fileupload.domain.model

data class FileUploadStatus(
    val filename: String,
    val totalBytes: Long,
    val totalBytesUploaded: Long,
    val isUploading: Boolean = false,
)
