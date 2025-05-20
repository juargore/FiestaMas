package com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.photos

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.universal.fiestamas.presentation.utils.extensions.AllowLandscapeOrientation
import com.universal.fiestamas.presentation.utils.extensions.SetBlackStatusBar
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

@Suppress("DEPRECATION")
@SuppressLint("OpaqueUnitKey")
@Composable
fun VideoViewerScreen(
    url: String,
    onBackClicked: () -> Unit
) {
    SetBlackStatusBar()
    AllowLandscapeOrientation()

    val context = LocalContext.current
    val i = MediaItem.fromUri(url)

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            this.setMediaItem(i)
            this.prepare()
            this.playWhenReady = true
        }
    }

    BackHandler {
        exoPlayer.release()
        onBackClicked()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        DisposableEffect(
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                        layoutParams =
                            FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                    }
                }
            )
        ) {
            onDispose {
                exoPlayer.release()
            }
        }
    }
}
