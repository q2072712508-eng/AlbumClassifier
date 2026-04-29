package com.albumclassifier.ui.album

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.albumclassifier.data.db.entity.Album
import com.albumclassifier.data.repository.AlbumRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AlbumViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AlbumRepository(application)

    private val _categoryId = MutableStateFlow(0L)

    val albums = _categoryId.flatMapLatest { categoryId ->
        repo.getAlbumsByCategory(categoryId)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setCategoryId(id: Long) {
        _categoryId.value = id
    }

    fun createAlbum(name: String) {
        viewModelScope.launch {
            repo.createAlbum(_categoryId.value, name)
        }
    }

    fun deleteAlbum(album: Album) {
        viewModelScope.launch {
            repo.deleteAlbum(album)
        }
    }

    fun renameAlbum(album: Album, newName: String) {
        viewModelScope.launch {
            repo.updateAlbum(album.copy(name = newName))
        }
    }
}
