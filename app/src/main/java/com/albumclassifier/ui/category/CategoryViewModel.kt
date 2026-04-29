package com.albumclassifier.ui.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.albumclassifier.data.db.entity.Category
import com.albumclassifier.data.repository.AlbumRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AlbumRepository(application)

    val categories = repo.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun createCategory(name: String) {
        viewModelScope.launch {
            repo.createCategory(name)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repo.deleteCategory(category)
        }
    }

    fun renameCategory(category: Category, newName: String) {
        viewModelScope.launch {
            repo.updateCategory(category.copy(name = newName))
        }
    }
}
