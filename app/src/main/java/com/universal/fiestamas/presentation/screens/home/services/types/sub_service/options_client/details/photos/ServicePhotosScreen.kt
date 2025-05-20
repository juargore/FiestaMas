package com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.photos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.MediaItemService
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.DetailsServiceViewModel
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape8
import com.universal.fiestamas.presentation.ui.MiniVideoPlayer
import com.universal.fiestamas.presentation.ui.backgrounds.PhotosBackground
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.utils.Constants.GRID_TWO_CELLS
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.getIntentShareSheet

@Composable
fun ServicePhotosScreen(
    vma: AuthViewModel = hiltViewModel(),
    vm: DetailsServiceViewModel = hiltViewModel(),
    mediaList: List<MediaItemService>,
    service: Service,
    onNavigatePhotoViewerClicked: (List<String>, String) -> Unit,
    onNavigateVideoViewerClicked: (String) -> Unit,
    onAuthProcessStarted: () -> Unit,
    onBackClicked: () -> Unit
) {
    vma.checkIfUserIsSignedIn()
    vma.getFirebaseUserDb(MainParentClass.userId)

    val user by vma.firebaseUserDb.collectAsState()
    val activeUser by vma.firebaseUser.collectAsState()

    val context = LocalContext.current
    var isUserSignedIn by remember { mutableStateOf(false) }
    var showProgressDialog by remember { mutableStateOf(false) }

    LaunchedEffect(activeUser) {
        isUserSignedIn = activeUser != null && activeUser?.email != null
    }

    ProgressDialog(showProgressDialog)

    PhotosBackground(
        content = {
            LazyVerticalGrid(
                columns = GRID_TWO_CELLS,
                contentPadding = PaddingValues(16.dp.autoSize()),
                verticalArrangement = Arrangement.spacedBy(5.dp.autoSize()),
                horizontalArrangement = Arrangement.spacedBy(5.dp.autoSize())
            ) {
                itemsIndexed(mediaList) { _, media ->
                    if (media.isVideo) {
                        MiniVideoPlayer(
                            modifier = Modifier
                                .height(100.dp.autoSize())
                                .fillMaxWidth()
                                .padding(1.dp.autoSize())
                                .clip(allRoundedCornerShape8)
                                .background(color = Color.Black, shape = allRoundedCornerShape8),
                            url = media.url
                        ) {
                            onNavigateVideoViewerClicked(it)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .height(100.dp.autoSize())
                                .fillMaxWidth()
                                .clip(allRoundedCornerShape8)
                                .clickable {
                                    val imagesList: List<String> = mediaList
                                        .filter { !it.isVideo }
                                        .map { it.url }
                                    onNavigatePhotoViewerClicked(imagesList, media.url)
                                }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(media.url),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        },
        titleScreen = stringResource(id = R.string.service_photos),
        service = service,
        user = user,
        onHeartButtonClicked = {
            if (isUserSignedIn) {
                activeUser?.uid?.let { userId ->
                    showProgressDialog = true
                    vm.likeService(userId, service.id) {
                        showProgressDialog = false
                        vm.alreadyLikedService = false
                    }
                }
            } else {
                onAuthProcessStarted()
            }
        },
        onShareButtonClicked = {
            context.startActivity(getIntentShareSheet("Share service"))
        },
        onBackButtonClicked = { onBackClicked() }
    )
}
