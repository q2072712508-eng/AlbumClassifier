package com.albumclassifier.ui.media

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.albumclassifier.data.db.entity.MediaItem
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MediaGridScreen(
    albumId: Long,
    onMediaClick: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: MediaViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(albumId) { viewModel.setAlbumId(albumId) }

    val mediaItems by viewModel.mediaItems.collectAsState()
    var showAddMenu by remember { mutableStateOf(false) }
    var selectedItems by remember { mutableStateOf(setOf<Long>()) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // 从相册选图
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) viewModel.importMedia(uris)
    }

    // 拍照
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            viewModel.saveCapturedPhoto(photoUri!!)
        }
    }

    fun createTempUri(): Uri {
        val tmpFile = File.createTempFile("capture_", ".jpg", context.cacheDir)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tmpFile
        )
    }

    val isSelecting = selectedItems.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isSelecting) "已选 ${selectedItems.size}" else "照片")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSelecting) selectedItems = emptySet() else onBack()
                    }) {
                        Icon(
                            if (isSelecting) Icons.Default.Close else Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    if (isSelecting) {
                        IconButton(onClick = {
                            val toDelete = mediaItems.filter { it.id in selectedItems }
                            viewModel.deleteMedia(toDelete)
                            selectedItems = emptySet()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "删除")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            if (!isSelecting) {
                Column(horizontalAlignment = Alignment.End) {
                    // 拍照按钮
                    FloatingActionButton(
                        onClick = {
                            photoUri = createTempUri()
                            cameraLauncher.launch(photoUri!!)
                        },
                        modifier = Modifier.padding(bottom = 12.dp),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "拍照")
                    }
                    // 从相册选择按钮
                    FloatingActionButton(onClick = { galleryLauncher.launch("image/*") }) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "从相册选择")
                    }
                }
            }
        }
    ) { padding ->
        if (mediaItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📷", style = MaterialTheme.typography.displayLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("还没有照片", style = MaterialTheme.typography.titleMedium)
                    Text("点击右下角按钮添加照片", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(mediaItems, key = { it.id }) { item ->
                    val isSelected = item.id in selectedItems
                    MediaThumbnail(
                        item = item,
                        isSelected = isSelected,
                        onClick = {
                            if (isSelecting) {
                                selectedItems = if (isSelected) selectedItems - item.id
                                else selectedItems + item.id
                            } else {
                                onMediaClick(item.id)
                            }
                        },
                        onLongClick = {
                            selectedItems = selectedItems + item.id
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MediaThumbnail(
    item: MediaItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        AsyncImage(
            model = item.uri,
            contentDescription = item.fileName,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 视频标记
        if (item.isVideo) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp),
                shape = MaterialTheme.shapes.extraSmall,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            ) {
                Text(
                    text = "▶",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    color = MaterialTheme.colorScheme.onError,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        // 选中标记
        if (isSelected) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            ) {}
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "已选中",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
