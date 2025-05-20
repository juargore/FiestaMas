package com.universal.fiestamas.presentation.ui.dialogs

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.domain.models.Guest
import com.universal.fiestamas.domain.models.Tag
import com.universal.fiestamas.domain.usecases.EventUseCase
import com.universal.fiestamas.domain.usecases.GuestUseCase
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.CardTag
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.InvitationsViewModel
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.showToast

@Suppress("LocalVariableName")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ManageGuestTagsDialog(
    isVisible: Boolean,
    context: Context,
    guest: Guest,
    _guestTags: List<Tag>,
    _allTags: List<Tag>,
    idClientEvent: String,
    viewModel: InvitationsViewModel,
    onDismiss: (List<Tag>) -> Unit
) {
    if (isVisible) {
        val guestTags = remember { mutableStateListOf<Tag>() }
        val allTags = remember { mutableStateListOf<Tag>() }

        LaunchedEffect(_guestTags, _allTags) {
            guestTags.clear()
            guestTags.addAll(_guestTags)
            allTags.clear()
            allTags.addAll(_allTags)
        }

        BaseDialog(
            title = "Etiquetas - ${guest.name?.substringBefore(" ")}",
            addCloseIcon = false,
            onDismiss = { onDismiss.invoke(guestTags) },
            content = {
                TextRegular(
                    text = "Administra las etiquetas para tu invitado.",
                    color = Color.Gray
                )
                
                VerticalSpacer(height = 20.dp)
                TextMedium(text = "Etiquetas seleccionadas")
                VerticalSpacer(height = 10.dp)

                Row {
                    FlowRow(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        guestTags.forEach {
                            CardTag(
                                modifier = Modifier.height(36.dp),
                                textSize = 18.sp,
                                text = it.name,
                                onDeleteTag = {
                                    viewModel.deleteTagFromGuest(it, guest.id, idClientEvent)
                                    showToast(context, "Quitando etiqueta...")
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        guestTags.remove(it) }, 1500L)
                                }
                            )
                            HorizontalSpacer(width = 8.dp)
                        }
                    }
                }

                VerticalSpacer(height = 30.dp)
                TextMedium(text = "Etiquetas disponibles")
                VerticalSpacer(height = 10.dp)

                Row {
                    FlowRow(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        allTags.forEach {
                            if (!guestTags.contains(it)) {
                                CardTag(
                                    modifier = Modifier.height(36.dp),
                                    textSize = 18.sp,
                                    text = it.name,
                                    onAssignTag = {
                                        viewModel.addTagToGuest(it, guest.id, idClientEvent)
                                        showToast(context, "Asignando etiqueta...")
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            guestTags.add(it) }, 1500L)
                                    }
                                )
                                HorizontalSpacer(width = 8.dp)
                            }
                        }
                    }
                }
                VerticalSpacer(height = 5.dp)
            }
        )
    }
}


@Preview
@Composable
fun ManageGuestTagsDialogPreview() {
    ManageGuestTagsDialog(
        isVisible = true,
        context = LocalContext.current,
        idClientEvent = "",
        guest = Guest(
            name = "Juan Arturo Gomez Resendiz"
        ),
        _guestTags = listOf(
            Tag("Tag1"),
            Tag("Tag02"),
        ),
        _allTags = listOf(
            Tag("Tag1"),
            Tag("Tag2"),
            Tag("Tag3")
        ),
        viewModel = InvitationsViewModel(
            GuestUseCase(IGuestRepositoryTest()),
            EventUseCase(IEventRepositoryTest())
        ),
        onDismiss = { }
    )
}
