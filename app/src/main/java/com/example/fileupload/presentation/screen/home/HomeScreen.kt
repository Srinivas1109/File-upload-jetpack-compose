package com.example.fileupload.presentation.screen.home

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fileupload.domain.model.MultipleFilesUploadStatus
import org.koin.androidx.compose.koinViewModel

@Composable
fun FileUploadProgress(
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    uploadingMultipleFiles: Boolean = false,
    multipleFilesUploadStatus: MultipleFilesUploadStatus? = null
) {
    val animatedValue by animateFloatAsState(progress)
    val multipleFileAnimatedValue by animateIntAsState(
        targetValue = multipleFilesUploadStatus?.noOfFilesUploaded ?: 0
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (uploadingMultipleFiles) {
                multipleFilesUploadStatus?.let {
                    LinearProgressIndicator(
                        progress = { it.noOfFilesUploaded / it.totalFiles.toFloat() },
                        modifier = modifier.weight(1f)
                    )
                    Spacer(modifier = modifier.width(16.dp))
                    Text(text = "$multipleFileAnimatedValue/${it.totalFiles}")
                }
            } else {
                LinearProgressIndicator(
                    progress = { animatedValue },
                    modifier = modifier.weight(1f)
                )
                Spacer(modifier = modifier.width(16.dp))
                Text(text = "${(animatedValue * 100).toInt()}%")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, homeViewModel: HomeViewModel = koinViewModel()) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val pickDocument =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                Log.d("DocumentPicker", "Selected Uri: $uri")
                homeViewModel.uploadFile(uri)
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
                    "Number of items selected: ${uris.size}"
                )
                homeViewModel.uploadFiles(uris)
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
            uiState.errorMessage?.let {
                Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ) {
                        Text(text = "Upload File", fontSize = 24.sp)
                    }
                }
            }

            FileUploadProgress(
                progress = uiState.progress,
                uploadingMultipleFiles = uiState.multipleFiles,
                multipleFilesUploadStatus = uiState.multipleFilesUploadStatus
            )

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

            if (uiState.isUploading) {
                Button(
                    onClick = { homeViewModel.cancelUpload() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(text = "Cancel")
                }
            } else {
                Button(onClick = {
                    if (uiState.multipleFiles) {
                        pickMultipleDocuments.launch(arrayOf("image/*"))
                    } else {
                        pickDocument.launch(arrayOf("*/*"))
                    }
                }, shape = RoundedCornerShape(4.dp)) {
                    Text(text = "Upload")
                }
            }
        }
    }
}