package com.example.fileupload.presentation.screen.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.fileupload.data.repository.FileUploadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class UploadFileType {
    Image,
    Pdf,
    Video
}

data class HomeUiState(
    val expanded: Boolean = false,
    val selectedFileType: UploadFileType = UploadFileType.Image,
    val selectedUri: Uri? = null,
    val selectedUris: List<Uri> = emptyList(),
    val multipleFiles: Boolean = false
)

class HomeViewModel(private val fileUploadRepository: FileUploadRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun onExpandedChange(expanded: Boolean) {
        _uiState.update {
            it.copy(expanded = expanded)
        }
    }

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
}