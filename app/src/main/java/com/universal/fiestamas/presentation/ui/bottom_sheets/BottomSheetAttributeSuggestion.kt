package com.universal.fiestamas.presentation.ui.bottom_sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape24
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@Composable
fun BottomSheetAttributeSuggestion(
    title: String,
    onClose: () -> Unit,
    onContinue: (List<String>) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val listOfAttributes = remember { mutableStateListOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(0.8f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PinkFiestamas)
            ) {
                TextSemiBold(
                    modifier = Modifier
                        .sidePadding()
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterStart),
                    text = "Agregar sugerencia",
                    color = Color.White,
                    fillMaxWidth = false
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = Color.White),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .height(18.dp.autoSize())
                        .sidePadding()
                        .clickable { onClose() }
                )
            }
            TextBold(
                modifier = Modifier.padding(top = 15.dp, bottom = 20.dp),
                text = "Selecciona el tipo de $title que ofreces",
                size = 20.sp,
                verticalSpace = 18.sp,
                color = PinkFiestamas
            )

            listOfAttributes.forEachIndexed { i, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sidePadding(30.dp)
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoundedEdittext(
                        modifier = Modifier.weight(0.85f),
                        isForV2 = true,
                        value = listOfAttributes[i],
                        placeholder = "Tipo",
                        textColorFocusedV2 = Color.Black,
                        textColorUnfocusedV2 = Color.DarkGray,
                        hintTextColorV2 = PinkFiestamas,
                        hintBackgroundColorV2 = Color.White,
                        focusedBorderColorV2 = PinkFiestamas,
                        unfocusedBorderColorV2 = Color.Gray,
                        onValueChange = { listOfAttributes[i] = it }
                    )
                    val (icon, filter) = if (i == listOfAttributes.size-1) {
                        Pair(R.drawable.ic_add_fiestamas, null)
                    } else {
                        Pair(R.drawable.ic_remove_filled, ColorFilter.tint(PinkFiestamas))
                    }
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        colorFilter = filter,
                        modifier = Modifier
                            .size(50.dp.autoSize())
                            .weight(0.2f)
                            .clickable {
                                if (i == listOfAttributes.size-1) {
                                    // add
                                    listOfAttributes.add("")
                                } else {
                                    // remove
                                    listOfAttributes.remove(listOfAttributes[i])
                                }
                            }
                    )
                }
            }
        }
        Column(
            modifier = Modifier.weight(0.15f),
            verticalArrangement = Arrangement.Center
        ) {
            ButtonPinkRoundedCornersV2(
                verticalPadding = 14.dp,
                horizontalPadding = 40.dp,
                shape = allRoundedCornerShape24,
                content = {
                    TextBold(
                        text = "Continuar".uppercase(),
                        size = 17.sp,
                        color = Color.White,
                        fillMaxWidth = false
                    )
                },
                onClick = {
                    onContinue(listOfAttributes.filter { it.isNotEmpty() })
                    onClose()
                }
            )
        }
    }
}

@Preview
@Composable
fun BottomSheetAttributeSuggestionPreview() {
    BottomSheetAttributeSuggestion(
        title = "pasteles",
        onClose = { },
        onContinue = { }
    )
}
