package com.universal.fiestamas.data.extensions

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.universal.fiestamas.data.module.Constants
import com.universal.fiestamas.data.module.Constants.SERVICE_TYPES
import com.universal.fiestamas.data.module.Constants.SUB_SERVICE_TYPES
import com.universal.fiestamas.domain.models.FirebaseModel
import com.universal.fiestamas.domain.models.ServiceType
import com.universal.fiestamas.domain.models.request.Filters
import com.universal.fiestamas.domain.models.request.FilterRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

fun <T: FirebaseModel> DocumentReference.documentListenerFlow(
    dataType: Class<T>
): Flow<T?> = callbackFlow {
    val listener = object : EventListener<DocumentSnapshot> {
        override fun onEvent(snapshot: DocumentSnapshot?, exception: FirebaseFirestoreException?) {
            if (exception != null) {
                trySend(null) // send null in case of exception
                cancel(CancellationException("FirebaseFirestoreException occurred", exception))
                return
            }

            if (snapshot != null && snapshot.exists()) {
                try {
                    val data = snapshot.toObject(dataType)
                    if (data != null) {
                        data.id = snapshot.id
                        trySend(data)
                    }
                } catch (e: Exception) {
                    println("Error serializing firebase obj: ${e.message}")
                }
            } else {
                trySend(null)
            }
        }
    }

    val registration = addSnapshotListener(listener)
    awaitClose { registration.remove() }
}

fun <T : FirebaseModel> CollectionReference.collectionListenerFlow(
    dataType: Class<T>,
    query: com.google.firebase.firestore.Query? = null
): Flow<List<T>> = callbackFlow {
    val listener = object : EventListener<QuerySnapshot> {
        override fun onEvent(querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
            if (exception != null) {
                cancel()
                return
            }

            val dataList = mutableListOf<T>()
            for (documentSnapshot in querySnapshot!!.documents) {
                if (documentSnapshot.exists()) {
                    try {
                        val data = documentSnapshot.toObject(dataType)
                        if (data != null) {
                            data.id = documentSnapshot.id
                            dataList.add(data)
                        }
                    }  catch (e: Exception) {
                        println("Error serializing firebase obj: ${e.message}")
                    }
                }
            }
            trySend(dataList)
        }
    }

    val registration = query?.addSnapshotListener(listener) ?: addSnapshotListener(listener)
    awaitClose { registration.remove() }
}

fun getFilterByQuery(path: String, value: Any, opStr: String = "=="): FilterRequest {
    return FilterRequest(
        listOf(
            Filters(
                fieldPath = path,
                opStr = opStr,
                value = value
            )
        )
    )
}


// Extension function for ServicesTypesByServiceCategory
fun FirebaseFirestore.getServicesTypesByServiceCategoryIdFlow(id: String): Flow<List<ServiceType>> {
    val serviceTypesCollection = this.collection(SERVICE_TYPES)
    val query = serviceTypesCollection.whereEqualTo(Constants.ID_SERVICE_CATEGORY, id)

    return callbackFlow {
        val listener = object : EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                if (exception != null) {
                    cancel()
                    return
                }

                val serviceTypesList = mutableListOf<ServiceType>()
                val tasks = mutableListOf<Deferred<Unit>>()

                querySnapshot?.documents?.forEach { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        try {
                            val serviceType = documentSnapshot.toObject(ServiceType::class.java)
                            serviceType?.id = documentSnapshot.id

                            serviceType?.let { st ->
                                val job = CoroutineScope(Dispatchers.IO).async {
                                    checkHasSubServices(st)
                                }
                                tasks.add(job)
                                serviceTypesList.add(st)
                            }
                        } catch (e: Exception) {
                            println("Error serializing firebase obj: ${e.message}")
                        }
                    }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    tasks.awaitAll()
                    trySend(serviceTypesList)
                }
            }
        }

        val registration = query.addSnapshotListener(listener)
        awaitClose { registration.remove() }
    }
}

// Función de verificación de subservicios
private suspend fun FirebaseFirestore.checkHasSubServices(serviceType: ServiceType) {
    val subServiceQuery = this.collection(SUB_SERVICE_TYPES)
        .whereEqualTo(Constants.ID_SERVICE_TYPE, serviceType.id)
        .limit(1)

    val result = subServiceQuery.get().await()
    serviceType.hasSubServices = !result.isEmpty
}
