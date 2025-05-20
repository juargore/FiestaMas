package com.universal.fiestamas.presentation.ui.dialogs

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.domain.models.Guest
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.InvitationsViewModel
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.isValidEmail
import com.universal.fiestamas.presentation.utils.isValidPhoneNumber
import com.universal.fiestamas.presentation.utils.showToast

@Composable
fun AddGuestDialog(
    viewModel: InvitationsViewModel,
    isVisible: Boolean,
    context: Context,
    idClientEvent: String,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        BaseDialog(
            title = "Agregar Invitado",
            addCloseIcon = true,
            isCancelable = true,
            onDismiss = onDismiss,
            content = {
                AddOrEditGuestContent { name, email, phone ->
                    viewModel.addNewGuest(idClientEvent, name, email, phone)
                    showToast(context, "Agregando invitado...")
                    onDismiss()
                }
            }
        )
    }
}

@Composable
fun EditGuestDialog(
    viewModel: InvitationsViewModel,
    isVisible: Boolean,
    context: Context,
    guestToEdit: Guest?,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        val cName = guestToEdit?.name.orEmpty()
        val cEmail = guestToEdit?.email.orEmpty()
        val cPhone = guestToEdit?.phone.orEmpty()
        BaseDialog(
            title = "Editar Invitado",
            addCloseIcon = true,
            isCancelable = true,
            onDismiss = onDismiss,
            content = {
                AddOrEditGuestContent(cName, cEmail, cPhone) { newName, newEmail, newPhone ->
                    val newGuest = guestToEdit?.apply {
                        name = newName
                        email = newEmail
                        phone = newPhone
                    }
                    newGuest?.let { viewModel.editGuest(it) }
                    showToast(context, "Guardando cambios...")
                    onDismiss()
                }
            }
        )
    }
}

@Composable
fun AddOrEditGuestContent(
    name: String = "",
    email: String = "",
    phone: String = "",
    onOkClicked: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    var guestName by rememberSaveable { mutableStateOf(name) }
    var guestEmail by rememberSaveable { mutableStateOf(email) }
    var guestPhone by rememberSaveable { mutableStateOf(phone) }

    var showValidationName by remember { mutableStateOf(false) }
    var showValidationEmail by remember { mutableStateOf(false) }
    var showValidationPhone by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = allRoundedCornerShape10)
                .padding(12.dp)
        ) {
            RoundedEdittext(
                placeholder = "Nombre del Invitado",
                value = guestName,
                onValueChange = {
                    guestName = it
                    showValidationName = it.isBlank()
                }
            )

            ValidationText(show = showValidationName, text = "Agregue un nombre")
            VerticalSpacer(height = 10.dp)

            RoundedEdittext(
                placeholder = "Email del Invitado",
                value = guestEmail,
                onValueChange = {
                    guestEmail = it
                    showValidationEmail = it.isBlank() || !isValidEmail(guestEmail.trim())
                }
            )

            ValidationText(show = showValidationEmail, text = "Agregue un email válido")
            VerticalSpacer(height = 10.dp)

            RoundedEdittext(
                placeholder = "Teléfono del Invitado",
                value = guestPhone,
                onValueChange = {
                    guestPhone = it
                    showValidationPhone = it.isBlank() || !isValidPhoneNumber(guestPhone)
                }
            )

            ValidationText(show = showValidationPhone, text = "Agregue un teléfono válido")
            VerticalSpacer(height = 10.dp)

            ButtonPinkRoundedCornersV2(
                verticalPadding = 10.dp,
                content = {
                    TextSemiBold(
                        text = "Enviar",
                        size = 18.sp,
                        color = Color.White,
                        shadowColor = Color.Gray
                    )
                },
                onClick = {
                    if (guestName.isBlank()) {
                        showToast(context, "Agregue un nombre")
                        return@ButtonPinkRoundedCornersV2
                    }
                    if (guestEmail.isBlank() || !isValidEmail(guestEmail.trim())) {
                        showToast(context, "Agregue un email válido")
                        return@ButtonPinkRoundedCornersV2
                    }
                    if (guestPhone.isBlank() || !isValidPhoneNumber(guestPhone)) {
                        showToast(context, "Agregue un teléfono válido")
                        return@ButtonPinkRoundedCornersV2
                    }
                    onOkClicked(guestName, guestEmail.trim(), guestPhone)
                }
            )
        }
    }
}
