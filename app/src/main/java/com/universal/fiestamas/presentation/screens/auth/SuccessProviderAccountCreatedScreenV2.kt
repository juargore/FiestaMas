package com.universal.fiestamas.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape24
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape30
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.utils.extensions.autoSize

@Composable
fun SuccessProviderAccountCreatedScreenV2(
    onAddServiceCategoryV2: () -> Unit
) {
    GradientBackground(
        content = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White, allRoundedCornerShape30)
                        .padding(horizontal = 14.dp, vertical = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_balloon),
                        modifier = Modifier.size(120.dp.autoSize()),
                        contentDescription = null
                    )
                    TextSemiBold(
                        text = "¡Bienvenido!",
                        fillMaxWidth = false,
                        color = PinkFiestamas,
                        size = 20.sp
                    )
                    TextMedium(
                        modifier = Modifier.padding(vertical = 20.dp),
                        text = "Tu cuenta ha sido creada\nexitosamente",
                        fillMaxWidth = false,
                        size = 15.sp
                    )
                    ButtonPinkRoundedCornersV2(
                        verticalPadding = 8.dp,
                        horizontalPadding = 20.dp,
                        shape = allRoundedCornerShape24,
                        content = {
                            TextBold(
                                text = "Crea tu servicio".uppercase(),
                                size = 17.sp,
                                color = Color.White,
                                fillMaxWidth = false
                            )
                        },
                        onClick = {
                            onAddServiceCategoryV2()
                        }
                    )
                }
            }
        },
        isPinkBackground = true,
        showBackButton = false,
        showLogoFiestamas = false,
        showUserName = false,
        addBottomPadding = false,
    )
}
