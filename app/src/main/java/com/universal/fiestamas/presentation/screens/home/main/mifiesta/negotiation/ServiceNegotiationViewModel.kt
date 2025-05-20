package com.universal.fiestamas.presentation.screens.home.main.mifiesta.negotiation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.request.QuoteV2
import com.universal.fiestamas.domain.models.request.RequestQuotation
import com.universal.fiestamas.domain.models.response.GetQuoteResponse
import com.universal.fiestamas.domain.usecases.EventUseCase
import com.universal.fiestamas.domain.usecases.MessageUseCase
import com.universal.fiestamas.domain.usecases.ServiceUseCase
import com.universal.fiestamas.domain.usecases.SharedPrefsUseCase
import com.universal.fiestamas.presentation.ui.dialogs.OptionsQuote
import com.universal.fiestamas.presentation.utils.convertListNotificationDbToListNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceNegotiationViewModel @Inject constructor(
    private val serviceUseCase: ServiceUseCase,
    private val eventUseCase: EventUseCase,
    private val messageUseCase: MessageUseCase,
    private val sharedPrefsUseCase: SharedPrefsUseCase
) : ViewModel() {

    private var alreadyGotQuotes = false
    private var alreadyGotMyPartyService = false
    var alreadyRequestedEditQuoteFromProviderToClient = false
    var alreadyRequestedQuoteFromClientToProvider = false

    private val _unreadMessagesCount = MutableStateFlow(0)
    val unreadMessagesCount: StateFlow<Int>
        get() = _unreadMessagesCount

    private val _myPartyService = MutableStateFlow<MyPartyService?>(null)
    val myPartyService: StateFlow<MyPartyService?>
        get() = _myPartyService

    private val _currentQuote = MutableStateFlow<GetQuoteResponse?>(null)
    val currentQuote: StateFlow<GetQuoteResponse?>
        get() = _currentQuote

    fun getMyPartyService(serviceEventId: String) {
        if (!alreadyGotMyPartyService) {
            alreadyGotMyPartyService = true
            viewModelScope.launch(Dispatchers.IO) {
                eventUseCase.getMyPartyService(serviceEventId).collectLatest {
                    _myPartyService.value = it
                }
            }
        }
    }

    fun requestQuoteFromClientToProvider(
        request: RequestQuotation,
        onFinished: (String) -> Unit
    ) {
        if (!alreadyRequestedQuoteFromClientToProvider) {
            alreadyRequestedQuoteFromClientToProvider = true
            viewModelScope.launch(Dispatchers.IO) {
                val response = serviceUseCase.requestQuoteFromClientToProvider(request).first()
                if (response.status == 200) {
                    onFinished("Cotización solicitada con éxito")
                } else {
                    onFinished("No se ha podido realizar la solicitud")
                }
            }
        }
    }

    fun requestEditQuoteFromProviderToClient(
        request: RequestQuotation,
        onFinished: (String) -> Unit
    ) {
        if (!alreadyRequestedEditQuoteFromProviderToClient) {
            alreadyRequestedEditQuoteFromProviderToClient = true
            viewModelScope.launch(Dispatchers.IO) {
                val response = serviceUseCase.requestEditQuoteFromProviderToClient(request).first()
                if (response.status == 200) {
                    onFinished("")
                } else {
                    onFinished("No se ha podido realizar la solicitud")
                }
            }
        }
    }

    fun getQuote(serviceEventId: String) {
        if (!alreadyGotQuotes) {
            alreadyGotQuotes = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.getQuoteByServiceEvent(serviceEventId).collect { list: List<GetQuoteResponse> ->
                    list.firstOrNull()?.let { quoteResponse ->
                        // filter bid list to remove duplicates by bid and status
                        //val uniqueBids = quoteResponse.bids.distinctBy { Pair(it.bid, it.status) }.toMutableList()
                        // creates new instance of GetQuoteResponse with filtered bids
                        //val updatedQuoteResponse = quoteResponse.copy(bids = uniqueBids)
                        //_currentQuote.value = updatedQuoteResponse
                        _currentQuote.value = quoteResponse
                    }
                }
            }
        }
    }

    fun createNewQuoteV2(quote: QuoteV2, onFinished: (msg: String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = serviceUseCase.createNewQuoteV2(quote).first()) {
                is BaseResult.Error -> onFinished(response.rawResponse.message)
                is BaseResult.Success -> onFinished(null)
            }
        }
    }

    fun editQuoteV2(
        quoteId: String,
        quote: QuoteV2,
        onFinished: (msg: String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = serviceUseCase.editQuoteV2(quoteId, quote).first()) {
                is BaseResult.Error -> onFinished(response.rawResponse.message)
                is BaseResult.Success -> onFinished(null)
            }
        }
    }

    fun addNotesToQuote(
        quoteId: String,
        personalNotesClient: String?,
        personalNotesProvider: String?,
        importantNotes: String?,
        onFinished: (message: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            serviceUseCase.addNotesToExistingQuote(
                quotationId = quoteId,
                notesClient = personalNotesClient,
                notesProvider = personalNotesProvider,
                importantNotes = importantNotes
            ).collectLatest { res ->
                when (res) {
                    is BaseResult.Error -> onFinished(res.rawResponse.message)
                    is BaseResult.Success -> onFinished("Nota agregada con éxito!")
                }
            }
        }
    }

    fun acceptOrDeclineOfferV2(
        serviceEventId: String,
        quoteId: String,
        userId: String,
        total: Int,
        accepted: Boolean,
        title: String,
        content: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = if (accepted) "ACCEPTED" else "REJECTED"
            serviceUseCase.acceptOrDeclineOfferV2(serviceEventId, quoteId, userId, status, total, title, content)
        }
    }

    fun updateServiceStatus(serviceEventId: String, status: OptionsQuote, onFinished: (BaseResult<Boolean, ErrorResponse>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            serviceUseCase.updateServiceStatus(serviceEventId, status).collectLatest { response ->
                onFinished(response)
            }
        }
    }

    fun getUnreadCounterChatMessagesByServiceEvent(serviceEventId: String, senderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageUseCase.getUnreadCounterChatMessagesByServiceEvent(serviceEventId, senderId).collectLatest {
                _unreadMessagesCount.value = it
            }
        }
    }

    fun getServiceIdForNotification(): String {
        return sharedPrefsUseCase.getServiceIdNotification()
    }

    fun clientPreviouslyDeclinedEditingQuotation(
        serviceEvent: MyPartyService?,
        serviceEventId: String,
        onFinished: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            messageUseCase.getChatMessagesByServiceEvent(serviceEventId).collectLatest { listDb ->
                val chatMessages = convertListNotificationDbToListNotification(true,  serviceEvent, listDb)
                val previouslyApproved = chatMessages.any { it.message.contains("El cliente rechazó") }
                onFinished(previouslyApproved)
            }
        }
    }
}
