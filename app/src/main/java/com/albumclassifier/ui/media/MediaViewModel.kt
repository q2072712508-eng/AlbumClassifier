package com.albumclassifier.ui.media

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.albumclassifier.data.db.entity.MediaItem
import com.albumclassifier.data.repository.AlbumRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MediaViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AlbumRepository(application)

    private val _albumId = MutableStateFlow(0L)

    val mediaItems = _albumId.flatMapLatest { albumId ->
        repo.getMediaByAlbum(albumId)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setAlbumId(id: Long) {
        _albumId.value = id
    }

    fun importMedia(uris: List<Uri>) {
        viewModelScope.launch {
            repo.importMedia(_albumId.value, uris)
        }
    }

    fun saveCapturedPhoto(uri: Uri) {
        viewModelScope.launch {
            repo.saveCapturedPhoto(_albumId.value, uri)
        }
    }

    fun deleteMedia(items: List<MediaItem>) {
        viewModelScope.launch {
            repo.deleteMedia(items)
        }
    }

    suspend fun getMediaById(id: Long): MediaItem? {
        return repo.getMediaById(id)
    }
}
