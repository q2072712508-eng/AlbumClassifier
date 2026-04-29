package com.albumclassifier

import android.app.Application
import com.albumclassifier.data.db.AppDatabase

class AlbumClassifierApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化数据库
        AppDatabase.getInstance(this)
    }
}
