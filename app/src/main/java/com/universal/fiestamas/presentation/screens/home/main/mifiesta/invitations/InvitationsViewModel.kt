package com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universal.fiestamas.domain.models.Guest
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.domain.models.Tag
import com.universal.fiestamas.domain.models.request.AddNewGuestRequest
import com.universal.fiestamas.domain.models.request.TagsOnGuestsRequest
import com.universal.fiestamas.domain.models.request.EditGuestRequest
import com.universal.fiestamas.domain.models.request.ListOfGuestsRequest
import com.universal.fiestamas.domain.usecases.EventUseCase
import com.universal.fiestamas.domain.usecases.GuestUseCase
import com.universal.fiestamas.presentation.ui.bottom_sheets.BottomGuestStatus
import com.universal.fiestamas.presentation.utils.extensions.getGuestStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationsViewModel @Inject constructor(
    private val guestUseCase: GuestUseCase,
    private val eventUseCase: EventUseCase
) : ViewModel() {

    private var alreadyGotList = false
    private var isSearching = false

    private val _emptyResults = MutableStateFlow(EmptyResults.Search)
    val emptyResults: StateFlow<EmptyResults>
        get() = _emptyResults

    private val _resetSearch = MutableStateFlow(false)
    val resetSearch: StateFlow<Boolean>
        get() = _resetSearch

    private val backupGuestsList = MutableStateFlow<List<Guest>?>(null)
    private val _guestsList = MutableStateFlow<List<Guest>?>(null)
    val guestsList: StateFlow<List<Guest>?>
        get() = _guestsList

    private val backupGuestsListForTables = MutableStateFlow<List<Guest>?>(null)
    private val _guestsListForTables = MutableStateFlow<List<Guest>?>(null)
    val guestsListForTables: StateFlow<List<Guest>?>
        get() = _guestsListForTables

    private val _tagsList = MutableStateFlow<List<Tag>>(emptyList())
    val tagsList: StateFlow<List<Tag>>
        get() = _tagsList

    private val _guest = MutableStateFlow<Guest?>(null)
    val guest: StateFlow<Guest?>
        get() = _guest

    private val _clientEvent = MutableStateFlow<MyPartyEvent?>(null)
    val clientEvent: StateFlow<MyPartyEvent?>
        get() = _clientEvent

    val pendingGuestsSelected = mutableSetOf<Guest>()

    fun getGuestsList(idClientEvent: String) {
        if (!alreadyGotList) {
            viewModelScope.launch(Dispatchers.IO) {
                guestUseCase.getGuestsList(idClientEvent).collectLatest {
                    _guestsList.value = it.ifEmpty { null }
                    backupGuestsList.value = _guestsList.value
                    alreadyGotList = true
                }
            }
        }
    }

    fun getGuestsListForTables(idClientEvent: String) {
        //if (!alreadyGotList) {
            viewModelScope.launch(Dispatchers.IO) {
                guestUseCase.getGuestsList(idClientEvent).collectLatest {
                    _guestsList.value = it
                    backupGuestsList.value = _guestsList.value

                    val filteredGuestList = it.filter { guest ->
                        guest.num_table == null || guest.num_table == 0
                    }
                    _guestsListForTables.value = filteredGuestList
                    backupGuestsListForTables.value = _guestsListForTables.value
                    //alreadyGotList = true
                }
            }
        //}
    }

    fun addNewGuest(idClientEvent: String, name: String, email: String, phone: String) {
        viewModelScope.launch(Dispatchers.IO) {
            guestUseCase.addNewGuest(AddNewGuestRequest(idClientEvent, name, email, phone))
        }
    }

    fun editGuest(guest: Guest) {
        viewModelScope.launch(Dispatchers.IO) {
            val guestId = guest.id
            val body = EditGuestRequest(
                name = guest.name.orEmpty(),
                num_table = guest.num_table,
                email = guest.email.orEmpty(),
                phone = guest.phone.orEmpty(),
                status = guest.status.orEmpty()
            )
            guestUseCase.editGuest(guestId, body)
        }
    }

    fun deleteGuest(guestId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            guestId?.let { guestUseCase.deleteGuest(it) }
        }
    }

    fun sendInvitationToOneGuest(idClientEvent: String, guestId: String) {
        sendInvitationToManyGuests(idClientEvent, listOf(guestId))
    }

    fun sendInvitationToManyGuests(idClientEvent: String, guestsIds: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val body = ListOfGuestsRequest(guestsIds)
            guestUseCase.sendInvitationToGuests(idClientEvent, body)
        }
    }

    fun filterGuestListByStatus(bottomGuestStatus: BottomGuestStatus) {
        if (bottomGuestStatus.status == GuestStatus.All) {
            _guestsList.value = backupGuestsList.value
        } else {
            if (isSearching) {
                isSearching = false
                _resetSearch.value = true
                _guestsList.value = backupGuestsList.value
            }
            backupGuestsList.value?.filter {
                it.status.getGuestStatus() == bottomGuestStatus.status.name.getGuestStatus()
            }.let {
                _guestsList.value = it
                if (it.isNullOrEmpty()) _emptyResults.value = EmptyResults.FilterBy
            }
        }
    }

    fun onSearchTerm(term: String) {
        if (term.isBlank()) {
            isSearching = false
            _guestsList.value = backupGuestsList.value
        } else {
            backupGuestsList.value?.filter {
                it.name?.contains(term, ignoreCase = true) == true
            }.let {
                isSearching = true
                _guestsList.value = it
                if (it.isNullOrEmpty()) _emptyResults.value = EmptyResults.Search
            }
        }
    }

    fun getAllTagsByEvent(idClientEvent: String) {
        viewModelScope.launch(Dispatchers.IO) {
            guestUseCase.getAllTagsByEvent(idClientEvent).collectLatest {
                _tagsList.value = it
            }
        }
    }

    fun createTagForEvent(idClientEvent: String, tag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            guestUseCase.createTagForEvent(idClientEvent, tag)
        }
    }

    fun deleteTagForEvent(idClientEvent: String, tag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            guestUseCase.deleteTagForEvent(idClientEvent, tag)
        }
    }

    fun addTagToGuest(tag: Tag, guestId: String, idClientEvent: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val body = TagsOnGuestsRequest(
                tagName = tag.name,
                guestIds = listOf(guestId),
                id_client_event = idClientEvent
            )
            guestUseCase.addTagToGuest(body)
        }
    }

    fun deleteTagFromGuest(tag: Tag, guestId: String, idClientEvent: String) {
        deleteTagFromManyGuests(tag, listOf(guestId), idClientEvent)
    }

    private fun deleteTagFromManyGuests(tag: Tag, guestIds: List<String>, idClientEvent: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val body = TagsOnGuestsRequest(
                tagName = tag.name,
                guestIds = guestIds,
                id_client_event = idClientEvent
            )
            guestUseCase.deleteTagToManyGuests(body)
        }
    }

    fun getGuestById(guestId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            guestUseCase.getGuest(guestId).collectLatest {
                _guest.value = it
            }
        }
    }

    fun updateGuestStatus(status: GuestStatus, guestsId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val normalizedStatus = status.name.uppercase().trim()
            val body = ListOfGuestsRequest(listOf(guestsId))
            guestUseCase.updateGuestStatus(body, normalizedStatus)
        }
    }

    fun getClientEventById(idClientEvent: String) {
        viewModelScope.launch(Dispatchers.IO) {
            eventUseCase.getClientEventById(idClientEvent).collectLatest {
                _clientEvent.value = it
            }
        }
    }
}
