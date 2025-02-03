package com.example.fileupload.presentation.screen.home

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fileupload.utils.Constants.MAXIMUM_FILES_TO_UPLOAD
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, homeViewModel: HomeViewModel = koinViewModel()) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val pickDocument =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                Log.d("DocumentPicker", "Selected Uri: $uri")
            } else {
                Log.d("DocumentPicker", "No media selected")
            }
        }

    val pickMultipleDocuments =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            if (uris.isEmpty()) {
                Log.d("DocumentPicker", "No documents selected")
            } else {
                Log.d(
                    "DocumentPicker",
                    "Number of items selected: ${uris.take(MAXIMUM_FILES_TO_UPLOAD).size}"
                )
            }
        }

    val pickMedia =
        rememberLauncherForActivityResult(
            PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected Uri: $uri")
//                homeViewModel.uploadSingleImage(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    val pickMultipleMedia =
        rememberLauncherForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia(
                MAXIMUM_FILES_TO_UPLOAD
            )
        ) { uris ->
            if (uris.isNotEmpty()) {
                Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    Box(
        modifier = modifier
            .fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                UploadFileType.entries.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = it.name == uiState.selectedFileType.name,
                            onClick = {
                                homeViewModel.onFileTypeChange(
                                    it
                                )
                            })
                        Text(text = it.name)
                    }

                }
            }

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = uiState.multipleFiles, onCheckedChange = {
                    homeViewModel.onChangeMultipleFiles(it)
                })
                Text(text = "Multiple Files")
            }

            Button(onClick = {
                when (uiState.selectedFileType) {
                    UploadFileType.Image -> {
                        if (uiState.multipleFiles) {
                            pickMultipleMedia.launch(
                                PickVisualMediaRequest(
                                    PickVisualMedia.ImageOnly
                                )
                            )
                        } else {
                            pickMedia.launch(
                                PickVisualMediaRequest(
                                    PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    }

                    UploadFileType.Pdf -> {
                        if (uiState.multipleFiles) {
                            pickMultipleDocuments.launch(arrayOf("application/pdf"))
                        } else {
                            pickDocument.launch(arrayOf("application/pdf"))
                        }
                    }

                    UploadFileType.Video -> {
                        if (uiState.multipleFiles) {
                            pickMultipleMedia.launch(
                                PickVisualMediaRequest(
                                    PickVisualMedia.VideoOnly
                                )
                            )
                        } else {
                            pickMedia.launch(
                                PickVisualMediaRequest(
                                    PickVisualMedia.VideoOnly
                                )
                            )
                        }
                    }
                }
            }, shape = RoundedCornerShape(4.dp)) {
                Text(text = "Upload")
            }
        }
    }
}