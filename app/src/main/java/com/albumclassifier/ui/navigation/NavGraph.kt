package com.albumclassifier.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.albumclassifier.ui.album.AlbumScreen
import com.albumclassifier.ui.category.CategoryScreen
import com.albumclassifier.ui.media.MediaGridScreen
import com.albumclassifier.ui.media.MediaDetailScreen

object Routes {
    const val CATEGORIES = "categories"
    const val ALBUMS = "albums/{categoryId}"
    const val MEDIA_GRID = "media_grid/{albumId}"
    const val MEDIA_DETAIL = "media_detail/{mediaId}"

    fun albums(categoryId: Long) = "albums/$categoryId"
    fun mediaGrid(albumId: Long) = "media_grid/$albumId"
    fun mediaDetail(mediaId: Long) = "media_detail/$mediaId"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.CATEGORIES
    ) {
        // 一级分类列表
        composable(Routes.CATEGORIES) {
            CategoryScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate(Routes.albums(categoryId))
                }
            )
        }

        // 二级相册列表
        composable(
            route = Routes.ALBUMS,
            arguments = listOf(navArgument("categoryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: return@composable
            AlbumScreen(
                categoryId = categoryId,
                onAlbumClick = { albumId ->
                    navController.navigate(Routes.mediaGrid(albumId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 媒体网格
        composable(
            route = Routes.MEDIA_GRID,
            arguments = listOf(navArgument("albumId") { type = NavType.LongType })
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getLong("albumId") ?: return@composable
            MediaGridScreen(
                albumId = albumId,
                onMediaClick = { mediaId ->
                    navController.navigate(Routes.mediaDetail(mediaId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 媒体详情/预览
        composable(
            route = Routes.MEDIA_DETAIL,
            arguments = listOf(navArgument("mediaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val mediaId = backStackEntry.arguments?.getLong("mediaId") ?: return@composable
            MediaDetailScreen(
                mediaId = mediaId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
