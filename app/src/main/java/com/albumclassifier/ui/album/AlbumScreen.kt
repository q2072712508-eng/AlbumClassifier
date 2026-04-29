package com.albumclassifier.ui.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.albumclassifier.data.db.entity.Album
import com.albumclassifier.ui.components.AddDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    categoryId: Long,
    onAlbumClick: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: AlbumViewModel = viewModel()
) {
    LaunchedEffect(categoryId) {
        viewModel.setCategoryId(categoryId)
    }

    val albums by viewModel.albums.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showMenuFor by remember { mutableStateOf<Album?>(null) }
    var showRenameDialog by remember { mutableStateOf<Album?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("相册") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "新建相册")
            }
        }
    ) { padding ->
        if (albums.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📂", style = MaterialTheme.typography.displayLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("还没有相册", style = MaterialTheme.typography.titleMedium)
                    Text("点击右下角 + 创建第一个相册", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(albums, key = { it.id }) { album ->
                    AlbumCard(
                        album = album,
                        onClick = { onAlbumClick(album.id) },
                        onMoreClick = { showMenuFor = album }
                    )
                }
            }
        }
    }

    // 新建相册对话框
    if (showAddDialog) {
        AddDialog(
            title = "新建相册",
            label = "相册名称",
            onConfirm = { name ->
                viewModel.createAlbum(name)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    // 更多操作菜单
    showMenuFor?.let { album ->
        AlertDialog(
            onDismissRequest = { showMenuFor = null },
            title = { Text(album.name) },
            confirmButton = {
                Column {
                    TextButton(onClick = {
                        showMenuFor = null
                        showRenameDialog = album
                    }) { Text("重命名") }
                    TextButton(onClick = {
                        viewModel.deleteAlbum(album)
                        showMenuFor = null
                    }) { Text("删除", color = MaterialTheme.colorScheme.error) }
                }
            },
            dismissButton = {
                TextButton(onClick = { showMenuFor = null }) { Text("取消") }
            }
        )
    }

    // 重命名对话框
    showRenameDialog?.let { album ->
        AddDialog(
            title = "重命名相册",
            label = "新名称",
            initialText = album.name,
            onConfirm = { newName ->
                viewModel.renameAlbum(album, newName)
                showRenameDialog = null
            },
            onDismiss = { showRenameDialog = null }
        )
    }
}

@Composable
private fun AlbumCard(
    album: Album,
    onClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (album.coverUri != null) {
                AsyncImage(
                    model = album.coverUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🖼️", style = MaterialTheme.typography.displayMedium)
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = album.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onMoreClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "更多",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
