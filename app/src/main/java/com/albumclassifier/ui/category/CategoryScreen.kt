package com.albumclassifier.ui.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.albumclassifier.data.db.entity.Category
import com.albumclassifier.ui.components.AddDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    onCategoryClick: (Long) -> Unit,
    viewModel: CategoryViewModel = viewModel()
) {
    val categories by viewModel.categories.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showMenuFor by remember { mutableStateOf<Category?>(null) }
    var showRenameDialog by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的相册") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "新建分类")
            }
        }
    ) { padding ->
        if (categories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📸", style = MaterialTheme.typography.displayLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("还没有分类", style = MaterialTheme.typography.titleMedium)
                    Text("点击右下角 + 创建第一个分类", style = MaterialTheme.typography.bodyMedium)
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
                items(categories, key = { it.id }) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { onCategoryClick(category.id) },
                        onMoreClick = { showMenuFor = category }
                    )
                }
            }
        }
    }

    // 新建分类对话框
    if (showAddDialog) {
        AddDialog(
            title = "新建分类",
            label = "分类名称",
            onConfirm = { name ->
                viewModel.createCategory(name)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    // 更多操作菜单
    showMenuFor?.let { category ->
        AlertDialog(
            onDismissRequest = { showMenuFor = null },
            title = { Text(category.name) },
            text = { Text("选择操作") },
            confirmButton = {
                Column {
                    TextButton(onClick = {
                        showMenuFor = null
                        showRenameDialog = category
                    }) { Text("重命名") }
                    TextButton(onClick = {
                        viewModel.deleteCategory(category)
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
    showRenameDialog?.let { category ->
        AddDialog(
            title = "重命名分类",
            label = "新名称",
            initialText = category.name,
            onConfirm = { newName ->
                viewModel.renameCategory(category, newName)
                showRenameDialog = null
            },
            onDismiss = { showRenameDialog = null }
        )
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    onClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 封面图
            if (category.coverUri != null) {
                AsyncImage(
                    model = category.coverUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // 渐变遮罩
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📁", style = MaterialTheme.typography.displayMedium)
                }
            }

            // 底部名称栏
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
                        text = category.name,
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
