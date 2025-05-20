package com.universal.fiestamas.presentation.ui.dialogs

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.FirstQuestionsClient
import com.universal.fiestamas.domain.models.FirstQuestionsProvider
import com.universal.fiestamas.domain.models.Guest
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.Tag
import com.universal.fiestamas.domain.models.request.AddNewGuestRequest
import com.universal.fiestamas.domain.models.request.CreateEventResponse
import com.universal.fiestamas.domain.models.request.CreateEventResponseV2
import com.universal.fiestamas.domain.models.request.TagsOnGuestsRequest
import com.universal.fiestamas.domain.models.request.EditGuestRequest
import com.universal.fiestamas.domain.models.request.ListOfGuestsRequest
import com.universal.fiestamas.domain.models.response.StatusResponseV2
import com.universal.fiestamas.domain.usecases.EventUseCase
import com.universal.fiestamas.domain.usecases.GuestUseCase
import com.universal.fiestamas.domain.usecases.IEventRepository
import com.universal.fiestamas.domain.usecases.IGuestRepository
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.CardTag
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.InvitationsViewModel
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.DetailsServiceViewModel
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.showToast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ManageTagsDialog(
    isVisible: Boolean,
    context: Context,
    tagsList: List<Tag>,
    idClientEvent: String,
    viewModel: InvitationsViewModel,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        var tagName by rememberSaveable { mutableStateOf("") }
        var showValidationTag by remember { mutableStateOf(false) }

        BaseDialog(
            title = "Adminstración de Tags",
            addCloseIcon = false,
            onDismiss = onDismiss,
            content = {
                TextRegular(
                    text = "Usa etiquetas para organizar a tus invitados. Son perfectas para asignar números de mesa o crear listas A y B.",
                    color = Color.Gray
                )

                VerticalSpacer(height = 10.dp)

                RoundedEdittext(
                    placeholder = "Nombre de la etiqueta",
                    value = tagName,
                    onValueChange = {
                        tagName = it
                        showValidationTag = it.isBlank()
                    }
                )

                ValidationText(show = showValidationTag, text = "Agregue un nombre a la etiqueta")

                VerticalSpacer(height = 10.dp)

                ButtonPinkRoundedCornersV2(
                    verticalPadding = 10.dp,
                    content = {
                        TextSemiBold(
                            text = "Agregar etiqueta",
                            size = 18.sp,
                            color = Color.White,
                            shadowColor = Color.Gray
                        )
                    },
                    onClick = {
                        if (tagName.isBlank()) {
                            showToast(context, "Agregue un nombre a la etiqueta")
                            return@ButtonPinkRoundedCornersV2
                        }
                        if (tagsList.contains(Tag(tagName))) {
                            showToast(context, "Ya existe una etiqueta con este nombre")
                            return@ButtonPinkRoundedCornersV2
                        }
                        viewModel.createTagForEvent(idClientEvent, Tag(tagName))
                        showToast(context, "Creando etiqueta...")
                        tagName = ""
                    }
                )

                VerticalSpacer(height = 24.dp)

                Row {
                    FlowRow(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        tagsList.forEach {
                            CardTag(
                                modifier = Modifier.height(36.dp),
                                textSize = 18.sp,
                                text = it.name,
                                onDeleteTag = {
                                    viewModel.deleteTagForEvent(idClientEvent, it)
                                    showToast(context, "Eliminando etiqueta...")
                                }
                            )
                            HorizontalSpacer(width = 8.dp)
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun ManageTagsDialogPreview() {
    ManageTagsDialog(
        isVisible = true,
        context = LocalContext.current,
        idClientEvent = "",
        tagsList = listOf(
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

class IGuestRepositoryTest: IGuestRepository {
    override fun deleteGuest(guestId: String) { }
    override fun addNewGuest(body: AddNewGuestRequest) { }
    override fun getGuestsList(idClientEvent: String): Flow<List<Guest>> = flow { }
    override fun getGuest(guestId: String): Flow<Guest?> = flow { }
    override fun editGuest(guestId: String, body: EditGuestRequest) { }
    override fun sendInvitationToGuests(idClientEvent: String, body: ListOfGuestsRequest) { }
    override fun getAllTagsByEvent(idClientEvent: String): Flow<List<Tag>> = flow { }
    override fun createTagForEvent(idClientEvent: String, tag: Tag) { }
    override fun deleteTagForEvent(idClientEvent: String, tag: Tag) { }
    override fun addTagToGuest(body: TagsOnGuestsRequest) { }
    override fun deleteTagToManyGuests(body: TagsOnGuestsRequest) { }
    override fun updateGuestStatus(body: ListOfGuestsRequest, status: String) { }
}

class IEventRepositoryTest: IEventRepository {
    override fun getEventTypesList(): Flow<List<Event>> = flow { }
    override fun getEventTypeById(id: String): Flow<Event?> = flow { }
    override fun createEventByClient(
        clientId: String,
        eventId: String,
        questions: FirstQuestionsClient
    ): Flow<BaseResult<CreateEventResponseV2, ErrorResponse>> = flow { }
    override fun createEventByProvider(
        providerId: String,
        eventId: String,
        questions: FirstQuestionsProvider
    ): Flow<CreateEventResponseV2?> = flow { }
    override fun getClientEventsByClientId(clientId: String): Flow<List<MyPartyEvent>> = flow { }
    override fun getClientEventById(id: String): Flow<MyPartyEvent?> = flow { }
    override fun getServicesEventByClientEventId(id: String): Flow<List<MyPartyService>> = flow { }
    override fun getServicesEventByClientEventIdInThread(clientEventId: String): Flow<List<MyPartyService>> = flow { }
    override fun getServicesEventsByClient(clientId: String): Flow<List<MyPartyService>> = flow { }

    override fun getServicesEventByProviderId(id: String): Flow<List<MyPartyService>> = flow { }
    override fun getServicesEventByService(serviceId: String): Flow<List<MyPartyService>> = flow { }
    override fun getMyPartyService(serviceEventId: String): Flow<MyPartyService?> = flow { }
    override fun addServiceToEvent(eventId: String, serviceId: String): Flow<String> = flow { }
    override fun logService(idService: String, logType: DetailsServiceViewModel.LogServiceType): Flow<StatusResponseV2?> = flow { }
}
