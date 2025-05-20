package com.universal.fiestamas.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScreenInfo(
    var role: Role,
    val startedScreen: Screen,
    var prevScreen: Screen,
    val event: Event, // si
    val questions: FirstQuestionsClient?,
    val questionsProvider: FirstQuestionsProvider? = null, // no
    val serviceCategory: ServiceCategory?, // si
    var clientEventId: String?,
    val serviceType: ServiceType? = null,
    val subService: SubService? = null,
    var service: Service? = null // si
): Parcelable {
    constructor(): this(
        role = Role.Unauthenticated,
        startedScreen = Screen.None,
        prevScreen = Screen.None,
        event = Event(),
        questions = null,
        serviceCategory = null,
        clientEventId = null,
        serviceType = null,
        subService = null,
        service = null
    )
}

enum class Role {
    Client,
    Provider,
    Unauthenticated
}

enum class Screen {
    Home,
    Mifiesta,
    ServiceCategories,
    ServiceTypes,
    SubServices,
    EditServiceProvider,
    None,
    Shared
}
