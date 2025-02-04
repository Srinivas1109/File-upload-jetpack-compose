package com.example.fileupload.domain.repository

import android.net.Uri

interface FileManager {
    suspend fun readBytesFromUri(uri: Uri): ByteArray
    suspend fun getFileTypeFromUri(uri: Uri): String
}