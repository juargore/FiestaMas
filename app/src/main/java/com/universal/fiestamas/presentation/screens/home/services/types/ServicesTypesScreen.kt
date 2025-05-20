package com.universal.fiestamas.presentation.screens.home.services.types

import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass
import com.universal.fiestamas.presentation.ui.CardServiceType
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.utils.Constants.GRID_THREE_CELLS
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.isProvider
import com.universal.fiestamas.presentation.utils.extensions.itemThreeColumns
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@Composable
fun ServicesTypesScreen (
    vm: ServicesTypesViewModel = hiltViewModel(),
    vma: AuthViewModel = hiltViewModel(),
    screenInfo: ScreenInfo,
    onNavigateSubServicesClicked: (ScreenInfo) -> Unit,
    onNavigateServicesOptionsClicked: (ScreenInfo) -> Unit,
    onAuthProcessStarted: () -> Unit,
    onBackClicked: () -> Unit
) {
    vma.getFirebaseUserDb(MainParentClass.userId)

    val firebaseUserDb by vma.firebaseUserDb.collectAsState()

    GradientBackground(
        content = {
            ProgressDialog(
                isVisible = vm.showProgressDialog.collectAsState().value,
                message = stringResource(R.string.progress_getting_info)
            )

            vm.getServicesByCategoryId(screenInfo.serviceCategory!!.id)

            vm.servicesByCategory.collectAsState().value?.let { services ->
                if (services.isEmpty()) {
                    onNavigateSubServicesClicked(vm.getNewScreenInfo(screenInfo, null))
                } else {
                    LazyVerticalGrid(columns = GRID_THREE_CELLS, modifier = Modifier.sidePadding()) {
                        itemThreeColumns {
                            TextMedium(
                                text = screenInfo.serviceCategory.name,
                                align = TextAlign.Start,
                                size = 20.sp.autoSize()
                            )
                        }
                        itemsIndexed(services) { i, item ->
                            CardServiceType(item = item, index = i) {
                                if (item?.hasSubServices == true || firebaseUserDb?.role.isProvider()) {
                                    onNavigateSubServicesClicked(vm.getNewScreenInfo(screenInfo, it))
                                } else {
                                    onNavigateServicesOptionsClicked(vm.getNewScreenInfo(screenInfo, it))
                                }
                            }
                        }
                    }
                }
            }
        },
        titleScreen = screenInfo.event.name,
        showLogoFiestamas = false,
        addBottomPadding = false,
        onNavigateAuthClicked = { onAuthProcessStarted() },
        onBackButtonClicked = { onBackClicked() },
        onTitleScreenClicked = { onBackClicked() }
    )
}
