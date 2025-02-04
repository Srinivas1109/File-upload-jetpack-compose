package com.example.fileupload.presentation.screen.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fileupload.data.dto.UploadFileDto
import com.example.fileupload.data.dto.UploadFileStatus
import com.example.fileupload.data.repository.FileUploadRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

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
    val progressMap: Map<String, UploadFileStatus> = emptyMap(),
)

class HomeViewModel(private val fileUploadRepository: FileUploadRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState())
    val uiState = _uiState.asStateFlow()

    val jobs = mutableMapOf<String, Job>()

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
        val id = uri.toString() // Ensure the same file doesn’t get uploaded multiple times
        if (jobs.containsKey(id)) return


        val uploadFile = UploadFileDto(
            id = id,
            filename = filename,
            uri = uri
        )

        jobs[id] = fileUploadRepository.uploadSingleImage(uploadFile)
            .onStart {
                _uiState.update {
                    Log.d("UPLOADING_STATUS", "Starting Upload $filename")
                    val updatedProgressMap = it.progressMap.toMutableMap()
                    updatedProgressMap[id] = UploadFileStatus(
                        id = id,
                        filename = filename,
                        totalBytes = 0L,
                        totalBytesUploaded = 0L,
                        isUploading = true
                    )
                    it.copy(progressMap = updatedProgressMap)
                }
            }
            .onEach { status ->
                _uiState.update {
                    val updatedProgressMap = it.progressMap.toMutableMap()
                    updatedProgressMap[status.id] = status.copy(isUploading = true)
                    it.copy(progressMap = updatedProgressMap)
                }
            }
            .onCompletion { cause ->
                if (cause == null) {
                    Log.d("UPLOADING_STATUS", "Completed Upload $filename")
                    _uiState.update {
                        val prev = it.progressMap[id]
                        val updatedProgressMap = it.progressMap.toMutableMap()
                        updatedProgressMap[id] =
                            prev?.copy(isUploading = false) ?: UploadFileStatus(
                                id = id,
                                filename = filename,
                                totalBytes = 0L,
                                totalBytesUploaded = 0L,
                                isUploading = false
                            )
                        it.copy(progressMap = updatedProgressMap)
                    }
                }

                if (cause is CancellationException) {
                    _uiState.update {
                        val updatedProgressMap = it.progressMap.toMutableMap()
                        updatedProgressMap[id] =
                            it.progressMap[id]?.copy(isUploading = false) ?: return@update it
                        it.copy(progressMap = updatedProgressMap)
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun cancelUpload(id: String) {
        jobs[id]?.cancel()
        jobs.remove(id)
    }

}