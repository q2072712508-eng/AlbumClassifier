# 📸 AlbumClassifier - 相册分类APP

Android 相册分类应用，支持二级相册管理、图片/视频导入和拍照。

## 技术栈
- **语言**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **数据库**: Room (SQLite)
- **图片加载**: Coil
- **架构**: MVVM + Repository
- **导航**: Navigation Compose

## 功能
- ✅ 创建一级分类（如"旅行"、"工作"）
- ✅ 在分类下创建二级相册（如"日本2024"）
- ✅ 从相册导入图片/视频
- ✅ 拍照/录像直接添加
- ✅ 照片预览与浏览
- ✅ 本地存储，离线可用

## 项目结构
```
app/src/main/java/com/albumclassifier/
├── AlbumClassifierApp.kt          # Application
├── MainActivity.kt                 # 入口
├── data/
│   ├── db/
│   │   ├── AppDatabase.kt         # Room 数据库
│   │   ├── dao/
│   │   │   ├── CategoryDao.kt     # 分类 DAO
│   │   │   ├── AlbumDao.kt        # 相册 DAO
│   │   │   └── MediaDao.kt        # 媒体 DAO
│   │   └── entity/
│   │       ├── Category.kt        # 一级分类实体
│   │       ├── Album.kt           # 二级相册实体
│   │       └── MediaItem.kt       # 媒体项实体
│   └── repository/
│       └── AlbumRepository.kt     # 数据仓库
├── ui/
│   ├── navigation/
│   │   └── NavGraph.kt            # 导航图
│   ├── theme/
│   │   ├── Theme.kt               # Material 3 主题
│   │   ├── Color.kt
│   │   └── Type.kt
│   ├── category/
│   │   ├── CategoryScreen.kt      # 一级分类列表
│   │   └── CategoryViewModel.kt
│   ├── album/
│   │   ├── AlbumScreen.kt         # 二级相册列表
│   │   └── AlbumViewModel.kt
│   ├── media/
│   │   ├── MediaGridScreen.kt     # 照片网格
│   │   ├── MediaDetailScreen.kt   # 照片预览
│   │   └── MediaViewModel.kt
│   └── components/
│       ├── AddDialog.kt           # 新建对话框
│       └── MediaPicker.kt         # 图片选择器
└── util/
    └── FileUtil.kt                # 文件工具
```
