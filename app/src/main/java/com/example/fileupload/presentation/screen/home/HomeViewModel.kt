package com.example.fileupload.presentation.screen.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fileupload.data.model.FileInfo
import com.example.fileupload.domain.model.FileUploadStatus
import com.example.fileupload.domain.model.MultipleFilesUploadStatus
import com.example.fileupload.domain.repository.FileManager
import com.example.fileupload.domain.repository.FileUploadRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class HomeUiState(
    val expanded: Boolean = false,
    val selectedUris: List<Uri> = emptyList(),
    val multipleFiles: Boolean = false,
    val errorMessage: String? = null,
    val progress: Float = 0f,
    val isUploading: Boolean = false,
    val multipleFilesUploadStatus: MultipleFilesUploadStatus? = null,
)

class HomeViewModel(
    private val fileUploadRepository: FileUploadRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun onChangeMultipleFiles(multipleFiles: Boolean) {
        _uiState.update {
            it.copy(multipleFiles = multipleFiles)
        }
    }

    private var job: Job? = null

    private var multipleFilesJob: Job? = null

    fun uploadFile(uri: Uri) {
        viewModelScope.launch(context = Dispatchers.IO) {
            fileUploadRepository.uploadFile(uri)
                .onStart {
                    _uiState.update {
                        it.copy(progress = 0f, isUploading = true)
                    }
                }
                .onEach { status ->
                    _uiState.update {
                        it.copy(progress = (status.totalBytesUploaded / status.totalBytes.toFloat()))
                    }
                }
                .onCompletion { cause ->
                    if (cause == null) {
                        Log.d("UPLOADING_STATUS", "Completed Upload")
                        job?.cancel()
                        _uiState.update {
                            it.copy(progress = 0f, isUploading = false)
                        }
                    }

                    if (cause is CancellationException) {
                        _uiState.update {
                            it.copy(progress = 0f, isUploading = false)
                        }
                    }

                    if (cause is OutOfMemoryError) {
                        _uiState.update { it.copy(errorMessage = "Out of memory") }
                    }
                }.launchIn(this)
        }
    }

    fun uploadFiles(uris: List<Uri>) {
        viewModelScope.launch(context = Dispatchers.IO) {
            multipleFilesJob = fileUploadRepository.uploadFiles(uris)
                .onStart {
                    _uiState.update { it.copy(isUploading = true, multipleFilesUploadStatus = MultipleFilesUploadStatus(uris.size, 0)) }
                }.onEach { status ->
                    _uiState.update { it.copy(multipleFilesUploadStatus = status) }
                }.onCompletion { cause ->
                    if (cause == null) {
                        _uiState.update { it.copy(isUploading = false) }
                    }
                    if (cause is CancellationException) {
                        _uiState.update {
                            it.copy(isUploading = false)
                        }
                    }
                    if (cause is OutOfMemoryError) {
                        _uiState.update { it.copy(errorMessage = "Out of memory") }
                    }
                }.launchIn(viewModelScope)
        }
    }

    fun cancelUpload() {
        if (uiState.value.multipleFiles) {
            multipleFilesJob?.cancel()
        } else {
            job?.cancel()
        }
    }

}