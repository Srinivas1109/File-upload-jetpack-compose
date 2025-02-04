package com.example.fileupload.data.model

import android.net.Uri

data class FileInfo(
    val name: String,
    val type: String,
    val uri: Uri,
    val bytes: ByteArray
)
