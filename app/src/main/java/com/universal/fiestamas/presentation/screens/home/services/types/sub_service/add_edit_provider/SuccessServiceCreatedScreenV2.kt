package com.universal.fiestamas.presentation.screens.home.services.types.sub_service.add_edit_provider

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape20
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape24
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape30
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.resetApplication

@Composable
fun SuccessServiceCreatedScreenV2(
    viewModel: AuthViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    GradientBackground(
        content = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White, allRoundedCornerShape30)
                        .padding(horizontal = 35.dp, vertical = 25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_balloon),
                        modifier = Modifier.size(120.dp.autoSize()),
                        contentDescription = null
                    )
                    TextBold(
                        text = "¡Enhorabuena!",
                        fillMaxWidth = false,
                        color = PinkFiestamas,
                        size = 21.sp
                    )
                    TextMedium(
                        modifier = Modifier.padding(vertical = 20.dp),
                        text = "Tu servicio ha sido\ncreado exitosamente",
                        fillMaxWidth = false,
                        size = 15.sp
                    )

                    Box(
                        modifier = Modifier
                            .background(Color.White, allRoundedCornerShape20)
                            .border(1.dp, PinkFiestamas, allRoundedCornerShape20)
                            .padding(horizontal = 15.dp, vertical = 9.dp)
                            .clickable {
                                viewModel.setProviderShouldBeRedirectedToServices(true)
                                context.resetApplication()
                            }
                    ) {
                        TextSemiBold(
                            text = "Ver mis servicios",
                            size = 16.sp,
                            color = PinkFiestamas,
                            fillMaxWidth = false,
                            horizontalSpace = (0).sp
                        )
                    }

                    VerticalSpacer(height = 10.dp)
                    ButtonPinkRoundedCornersV2(
                        verticalPadding = 8.dp,
                        horizontalPadding = 20.dp,
                        shape = allRoundedCornerShape24,
                        content = {
                            TextBold(
                                text = "Dashboard".uppercase(),
                                size = 17.sp,
                                color = Color.White,
                                fillMaxWidth = false
                            )
                        },
                        onClick = {
                            viewModel.setProviderShouldBeRedirectedToServices(true)
                            context.resetApplication()
                        }
                    )
                }
            }
        },
        isPinkBackground = true,
        showBackButton = false,
        showLogoFiestamas = false,
        showUserName = false,
        addBottomPadding = false
    )
}

@Preview
@Composable
fun SuccessServiceCreatedScreenV2Preview() {
    SuccessServiceCreatedScreenV2()
}