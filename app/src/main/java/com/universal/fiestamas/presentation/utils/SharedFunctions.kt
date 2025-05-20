package com.universal.fiestamas.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.tasks.Task
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.Notification
import com.universal.fiestamas.domain.models.NotificationStatus
import com.universal.fiestamas.domain.models.Role
import com.universal.fiestamas.domain.models.UriFile
import com.universal.fiestamas.domain.models.response.NotificationDb
import com.universal.fiestamas.presentation.ui.cards.MessageType
import com.universal.fiestamas.presentation.ui.cards.toMessageType
import com.universal.fiestamas.presentation.ui.cards.verifyTypeForMessage
import com.universal.fiestamas.presentation.utils.extensions.convertTimestampToDateAndHour
import com.universal.fiestamas.presentation.utils.extensions.isClient
import com.universal.fiestamas.presentation.utils.extensions.isProvider
import com.universal.fiestamas.presentation.utils.extensions.isRunningOnTablet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.URI
import java.util.UUID
import kotlin.coroutines.resumeWithException

@Composable
fun OpenUrl(url: String) {
    val context = LocalContext.current
    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(url)) }
    context.startActivity(intent)
}

fun openUrl(context: Context, url: String) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}

@SuppressLint("QueryPermissionsNeeded")
fun makePhoneCall(context: Context, phoneNumber: String?) {
    if (phoneNumber != null) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            showToast(context, "No app can handle this action")
        }
    } else {
        showToast(context, "El número no está disponible")
    }
}

@SuppressLint("QueryPermissionsNeeded")
fun sendEmail(context: Context, emailAddress: String?) {
    if (emailAddress != null) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
            putExtra(Intent.EXTRA_SUBJECT, "Subject")
            putExtra(Intent.EXTRA_TEXT, "Body")
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            showToast(context, "No app can handle this action")
        }
    } else {
        showToast(context, "El email no está disponible")
    }
}

@SuppressLint("QueryPermissionsNeeded")
fun openWhatsApp(context: Context, phoneNumber: String?) {
    if (phoneNumber != null) {
        val whatsappPackage = "com.whatsapp"
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber")
            `package` = whatsappPackage
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            showToast(context, "No app can handle this action")
        }
    } else {
        showToast(context, "El whatsapp no está disponible")
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Task<T>.awaitTask(): T {
    return suspendCancellableCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result!!){ }
            } else {
                continuation.resumeWithException(task.exception!!)
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
    return emailRegex.matches(email)
}

fun isValidPassword(password: String): Boolean {
    val pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}\$".toRegex()
    return pattern.matches(password)
}

fun isValidPhoneNumber(number: String): Boolean {
    val cleanNumber = number.replace("\\s".toRegex(), "")
    val hasOnlyDigits = cleanNumber.all { it.isDigit() }
    val hasTenDigits = cleanNumber.length == 10

    return hasOnlyDigits && hasTenDigits
}

fun isValidPhoneNumberNewFormat(number: String): Boolean {
    val cleanNumber = number
        .replace("\\s".toRegex(), "")
        .replace("(", "")
        .replace(")", "")
        .replace("-", "")
        .replace(" ", "")
        .trim()
    val hasOnlyDigits = cleanNumber.all { it.isDigit() }
    val hasTenDigits = cleanNumber.length == 10

    return hasOnlyDigits && hasTenDigits
}

fun String.cleanPhoneNumberNewFormat(): String{
    return this
        .replace("\\s".toRegex(), "")
        .replace("(", "")
        .replace(")", "")
        .replace("-", "")
        .replace(" ", "")
        .trim()
}

fun isValidRFC(rfc: String): Boolean {
    val pattern = "^([A-ZÑ\\x26]{3,4}([0-9]{2})(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1]))([A-Z\\d]{3})?\$".toRegex()
    return pattern.matches(rfc)
}


fun showToast(context: Context, message: String?, duration: Int = Toast.LENGTH_SHORT) {
    if (!message.isNullOrEmpty()) {
        val isTablet = isRunningOnTablet(context)
        val proportion = if (isTablet) 2f else 1f
        val customText = SpannableStringBuilder(message)
        customText.setSpan(RelativeSizeSpan(proportion), 0, message.length, 0)
        Toast.makeText(context, customText, duration).show()
    }
}

fun showToastOnUiThread(context: Context, message: String?, duration: Int = Toast.LENGTH_SHORT) {
    if (!message.isNullOrEmpty()) {
        val isTablet = isRunningOnTablet(context)
        val proportion = if (isTablet) 2f else 1f
        val customText = SpannableStringBuilder(message)
        customText.setSpan(RelativeSizeSpan(proportion), 0, message.length, 0)
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, customText, duration).show()
        }
    }
}

fun getFileNameFromUri(context: Context, uri: Uri, isForLocalVideo: Boolean = false): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst()) {
            return it.getString(nameIndex)
        }
    }

    return if (isForLocalVideo) {
        "${UUID.randomUUID()}.mp4"
    } else {
        UUID.randomUUID().toString()
    }
}

fun String?.getRole(): Role {
    return if(this.isProvider()) {
        Role.Provider
    } else if(this.isClient()) {
        Role.Client
    } else {
        Role.Unauthenticated
    }
}

fun String.toUri(): UriFile {
    return try {
        val uri = URI(this)
        val name = this.substringAfterLast("/")
        val url = this
        UriFile(
            uri = Uri.parse(uri.toString()),
            fileName = name,
            url = url
        )
    } catch (e: Exception) {
        e.printStackTrace()
        UriFile(
            uri = Uri.EMPTY,
            fileName = "",
            url = ""
        )
    }
}

fun convertListNotificationDbToListNotification(
    isProvider: Boolean,
    serviceEvent: MyPartyService?,
    listDb: List<NotificationDb>?
): MutableList<Notification> {
    val mList = mutableListOf<Notification>()

    listDb?.forEach { notification ->
        val status = if (notification.read) NotificationStatus.Read else NotificationStatus.Unread
        val (date, hour) = convertTimestampToDateAndHour(notification.timestamp)
        var messageType = notification.type.toMessageType()

        if (messageType == MessageType.MESSAGE) {
            // double check as sometimes the image url returns MESSAGE type from server
            messageType = notification.content.verifyTypeForMessage()
        }

        mList.add(
            Notification(
                id = notification.id,
                status = status,
                icon = serviceEvent?.image,
                eventName = serviceEvent?.service_category_name, //Alimentos
                eventType = serviceEvent?.event_data?.name_event_type, //Revelacion
                festejadosName = serviceEvent?.event_data?.name, //Fernanda
                serviceName = serviceEvent?.name, //Empanadas La Chona
                message = notification.content,
                //date = "$date $hour",
                date = date,
                providerName = serviceEvent?.provider_contact_name.orEmpty(),
                clientName = notification.name_sender,
                idReceiver = notification.id_receiver,
                idSender = notification.id_sender,
                clientEventId = notification.id_client_event,
                receiverId = notification.id_receiver,
                senderId = notification.id_sender,
                senderPhoto = notification.photo_sender.orEmpty(),
                receiverPhoto = notification.photo_receiver.orEmpty(),
                serviceId = notification.id_service,
                serviceEventId = notification.id_service_event,
                serviceEvent = serviceEvent,
                isApproved = notification.is_approved,
                type = messageType
            )
        )
    }
    return mList
}

fun convertNotificationDbToNotification(
    isProvider: Boolean,
    serviceEvent: MyPartyService?,
    notification: NotificationDb?
): Notification {
    val status = if (notification?.read == true) NotificationStatus.Read else NotificationStatus.Unread
    val (date, hour) = convertTimestampToDateAndHour(notification?.timestamp)

    return Notification(
        id = notification?.id.orEmpty(),
        status = status,
        icon = serviceEvent?.image,
        eventName = serviceEvent?.service_category_name, //Alimentos
        eventType = serviceEvent?.event_data?.name_event_type, //Revelacion
        festejadosName = serviceEvent?.event_data?.name, //Fernanda
        serviceName = serviceEvent?.name, //Empanadas La Chona
        message = notification?.content.orEmpty(),
        //date = "$date $hour",
        date = date,
        providerName = serviceEvent?.provider_contact_name.orEmpty(),
        clientName = notification?.name_sender.orEmpty(),
        idReceiver = notification?.id_receiver.orEmpty(),
        idSender = notification?.id_sender.orEmpty(),
        clientEventId = notification?.id_client_event.orEmpty(),
        receiverId = notification?.id_receiver.orEmpty(),
        senderId = notification?.id_sender.orEmpty(),
        serviceId = notification?.id_service.orEmpty(),
        senderPhoto = notification?.photo_sender.orEmpty(),
        receiverPhoto = notification?.photo_receiver.orEmpty(),
        serviceEventId = notification?.id_service_event.orEmpty(),
        serviceEvent = serviceEvent,
        isApproved = notification?.is_approved,
        type = notification?.type.orEmpty().toMessageType()
    )
}

fun getOptionsDistanceForDropDown(includeHeader: Boolean = false): List<Pair<String, Int>> {
    val optionsDistanceMax = mutableListOf(
        Pair("40km", 40),
        Pair("80km", 80),
        Pair("120km", 120),
        Pair("160km", 160),
        Pair("200km", 200),
        Pair("240km", 240),
        Pair("280km", 280),
        Pair("320km", 320),
        Pair("360km", 360),
        Pair("400km", 400)
    )
    if (includeHeader) {
        optionsDistanceMax.add(0, Pair("Cualquiera", 0))
    }
    return optionsDistanceMax
}

fun getOptionsForUnity(includeHeader: Boolean = false): List<String> {
    val optionsUnity = mutableListOf("Persona", "Pieza", "Kg", "Evento")
    if (includeHeader) {
        optionsUnity.add(0, "Cualquiera")
    }
    return optionsUnity
}
