package com.universal.fiestamas.data.module

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.universal.fiestamas.data.apis.AuthApi
import com.universal.fiestamas.data.apis.EventApi
import com.universal.fiestamas.data.apis.MessageApi
import com.universal.fiestamas.data.apis.ServiceApi
import com.universal.fiestamas.data.repositories.AuthRepositoryImpl
import com.universal.fiestamas.data.repositories.EventRepositoryImpl
import com.universal.fiestamas.data.repositories.GoogleServicesRepositoryImpl
import com.universal.fiestamas.data.repositories.MessageRepositoryImpl
import com.universal.fiestamas.data.repositories.ServiceRepositoryImpl
import com.universal.fiestamas.data.repositories.SharedPrefsRepositoryImpl
import com.universal.fiestamas.domain.usecases.IAuthRepository
import com.universal.fiestamas.domain.usecases.IEventRepository
import com.universal.fiestamas.domain.usecases.IGoogleServicesRepository
import com.universal.fiestamas.domain.usecases.IMessageRepository
import com.universal.fiestamas.domain.usecases.IServiceRepository
import com.universal.fiestamas.domain.usecases.ISharedPrefsRepository
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.universal.fiestamas.data.apis.GuestApi
import com.universal.fiestamas.data.repositories.GuestRepositoryImpl
import com.universal.fiestamas.data.utils.NetworkManager
import com.universal.fiestamas.domain.usecases.IGuestRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoriesModule {

    @Singleton
    @Provides
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Singleton
    @Provides
    fun provideServiceApi(retrofit: Retrofit): ServiceApi {
        return retrofit.create(ServiceApi::class.java)
    }

    @Singleton
    @Provides
    fun provideMessageApi(retrofit: Retrofit): MessageApi {
        return retrofit.create(MessageApi::class.java)
    }

    @Singleton
    @Provides
    fun provideEventApi(retrofit: Retrofit): EventApi {
        return retrofit.create(EventApi::class.java)
    }

    @Singleton
    @Provides
    fun provideGuestApi(retrofit: Retrofit): GuestApi {
        return retrofit.create(GuestApi::class.java)
    }

    @Singleton
    @Provides
    fun provideFirebase(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firestore.firestoreSettings = settings
        return firestore
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun providePlacesClient(context: Context): PlacesClient {
        return Places.createClient(context)
    }


    @Singleton
    @Provides
    fun provideLocationServices(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Singleton
    @Provides
    fun provideNetworkManager(context: Context): NetworkManager {
        return NetworkManager(context)
    }

    @Singleton
    @Provides
    fun provideGuestRepository(
        firestore: FirebaseFirestore,
        guestApi: GuestApi
    ) : IGuestRepository {
        return GuestRepositoryImpl(firestore, guestApi)
    }

    @Singleton
    @Provides
    fun provideEventRepository(
        authFirebase: FirebaseAuth,
        firestore: FirebaseFirestore,
        eventApi: EventApi,
        networkManager: NetworkManager
    ): IEventRepository {
        return EventRepositoryImpl(authFirebase, firestore, eventApi, networkManager)
    }

    @Singleton
    @Provides
    fun provideServicesRepository(
        authFirebase: FirebaseAuth,
        firestore: FirebaseFirestore,
        serviceApi: ServiceApi
    ): IServiceRepository {
        return ServiceRepositoryImpl(authFirebase, firestore, serviceApi)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        authFirebase: FirebaseAuth,
        firestore: FirebaseFirestore,
        autApi: AuthApi,
        networkManager: NetworkManager
    ): IAuthRepository {
        return AuthRepositoryImpl(authFirebase, firestore, autApi, networkManager)
    }

    @Singleton
    @Provides
    fun provideGoogleRepository(
        placesClient: PlacesClient,
        context: Context,
        locationServices: FusedLocationProviderClient
    ): IGoogleServicesRepository {
        return GoogleServicesRepositoryImpl(placesClient, context, locationServices)
    }

    @Singleton
    @Provides
    fun provideMessageRepository(
        //authFirebase: FirebaseAuth,
        firestore: FirebaseFirestore,
        messageApi: MessageApi
    ): IMessageRepository {
        return MessageRepositoryImpl(firestore, messageApi)
    }

    @Singleton
    @Provides
    fun provideSharedPrefsRepository(
        context: Context
    ): ISharedPrefsRepository {
        return SharedPrefsRepositoryImpl(context)
    }
}
