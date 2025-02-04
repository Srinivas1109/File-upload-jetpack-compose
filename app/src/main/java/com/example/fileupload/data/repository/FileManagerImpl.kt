package com.example.fileupload.data.repository

import android.content.Context
import android.net.Uri
import com.example.fileupload.domain.repository.FileManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileManagerImpl(
    private val context: Context,
) :
    FileManager {
        val dispatcher: CoroutineDispatcher = Dispatchers.IO
    override suspend fun readBytesFromUri(uri: Uri): ByteArray {
        return withContext(dispatcher){
            context.contentResolver.openInputStream(uri)?.use {
                it.readBytes()
            } ?: ByteArray(0)
        }
    }

    override suspend fun getFileTypeFromUri(uri: Uri): String {
        return withContext(dispatcher){
            context.contentResolver.getType(uri) ?: ""
        }
    }
}