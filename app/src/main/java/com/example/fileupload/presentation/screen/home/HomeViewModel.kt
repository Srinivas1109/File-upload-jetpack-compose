package com.example.fileupload.presentation.screen.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fileupload.data.dto.UploadFileDto
import com.example.fileupload.data.dto.UploadFileStatus
import com.example.fileupload.data.repository.FileUploadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import java.util.UUID

enum class UploadFileType {
    Image,
    Pdf,
    Video
}

data class HomeUiState(
    val expanded: Boolean = false,
    val selectedFileType: UploadFileType = UploadFileType.Image,
    val selectedUris: List<Uri> = emptyList(),
    val multipleFiles: Boolean = false,
    val progressMap: MutableMap<String, UploadFileStatus> = mutableMapOf(),
    val progress: Float = 0f
)

class HomeViewModel(private val fileUploadRepository: FileUploadRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun onFileTypeChange(fileType: UploadFileType) {
        _uiState.update {
            it.copy(selectedFileType = fileType)
        }
    }

    fun onChangeMultipleFiles(multipleFiles: Boolean) {
        _uiState.update {
            it.copy(multipleFiles = multipleFiles)
        }
    }

    fun uploadSingleImage(uri: Uri) {
        val filename = uri.lastPathSegment ?: "DefaultName"
        val id = UUID.randomUUID().toString()

        val uploadFile = UploadFileDto(
            id = id,
            filename = filename,
            uri = uri
        )

        fileUploadRepository.uploadSingleImage(uploadFile)
            .onStart { }
            .onEach { status ->
                Log.d(
                    "UPLOADING_STATUS",
                    "${uploadFile.id}: ${(status.totalBytesUploaded / status.totalBytes.toFloat()) * 100}%"
                )

                _uiState.update {
                    it.progressMap[status.id] = status
                    it.copy(progress = (status.totalBytesUploaded / status.totalBytes.toFloat()) * 100)
                }
            }
            .onCompletion { cause -> }.launchIn(viewModelScope)
    }
}