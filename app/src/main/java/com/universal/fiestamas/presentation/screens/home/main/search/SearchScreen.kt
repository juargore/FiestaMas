package com.universal.fiestamas.presentation.screens.home.main.search

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.Screen
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.models.ServiceType
import com.universal.fiestamas.domain.models.response.FirebaseUserDb
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.ServicesViewModel
import com.universal.fiestamas.presentation.screens.location.AddressAutoCompleteScreen
import com.universal.fiestamas.presentation.screens.location.AddressAutoCompleteViewModel
import com.universal.fiestamas.presentation.screens.location.PermissionEvent
import com.universal.fiestamas.presentation.screens.location.ViewState
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape24
import com.universal.fiestamas.presentation.theme.topRoundedCornerShape15
import com.universal.fiestamas.presentation.ui.CardServiceOption
import com.universal.fiestamas.presentation.ui.CardServiceOptionForHorizontalList
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.bottom_sheets.BottomSheetSearchFilters
import com.universal.fiestamas.presentation.ui.bottom_sheets.StoredValuesForFilters
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.utils.Constants
import com.universal.fiestamas.presentation.utils.Constants.ONE_SECOND
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.hasLocationPermission
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.getRole
import com.universal.fiestamas.presentation.utils.loadBitmapFromUrl
import com.universal.fiestamas.presentation.utils.showToast
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    vm: SearchViewModel = hiltViewModel(),
    vma: AuthViewModel = hiltViewModel(),
    vms: ServicesViewModel = hiltViewModel(),
    onNavigateServiceSearched: (ScreenInfo) -> Unit,
    onBackClicked: () -> Unit,
    reloadScreen: () -> Unit
) {
    val firebaseUserDb by vma.firebaseUserDb.collectAsState()

    vma.getFirebaseUserDb(MainParentClass.userId)

    firebaseUserDb?.let {
        vms.getFavouriteServices(it)
        vm.getAllServices()
        vm.getAllServicesCategories()
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var searchView: SearchView by rememberSaveable { mutableStateOf(SearchView.GRID) }
    var showProgressDialog by remember { mutableStateOf(false) }
    var bottomSheetContent: BottomSheetView by remember { mutableStateOf(BottomSheetView.Filters) }

    var addressSelected: Address? by remember { mutableStateOf(null) }
    var selectedCategory: ServiceCategory? by remember { mutableStateOf(null) }
    var selectedType: ServiceType? by remember { mutableStateOf(null) }
    var selectedDistance: Pair<String, Int>? by remember { mutableStateOf(null) }
    var selectedUnity: String? by remember { mutableStateOf(null) }
    var minCapacity: String? by remember { mutableStateOf(null) }
    var maxCapacity: String? by remember { mutableStateOf(null) }

    val filteredServices by vm.filteredServicesList.collectAsState()
    val likedServices by vms.likedServices.collectAsState()
    val servicesCategories by vm.servicesByEvent.collectAsState()
    var serviceCategorySelected: ServiceCategory? by remember { mutableStateOf(null) }
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    DisposableEffect(Unit) {
        val activity = context as Activity
        activity.window.statusBarColor = Color.White.toArgb()
        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
        onDispose { }
    }

    ProgressDialog(showProgressDialog)

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = topRoundedCornerShape15,
        sheetContent = {
            when (bottomSheetContent) {
                BottomSheetView.Filters -> {
                    BottomSheetSearchFilters(
                        storedValues = StoredValuesForFilters(selectedCategory, selectedType, addressSelected, selectedDistance, selectedUnity, minCapacity, maxCapacity),
                        onFilterApplied = { category, type, distance, unity, min, max ->
                            coroutineScope.launch { modalSheetState.hide() }
                            vm.filterServicesByParameters(category, type, distance, addressSelected?.location, unity, min, max)
                        },
                        onReset = {
                            coroutineScope.launch { modalSheetState.hide() }
                            Handler(Looper.getMainLooper()).post { reloadScreen() }
                        },
                        onShowAutoComplete = { values ->
                            coroutineScope.launch {
                                selectedCategory = values.category
                                selectedType = values.type
                                selectedDistance = values.distance
                                selectedUnity = values.unity
                                minCapacity = values.minCapacity
                                maxCapacity = values.maxCapacity
                                bottomSheetContent = BottomSheetView.AutoComplete
                                modalSheetState.show()
                            }
                        }
                    )
                }
                BottomSheetView.AutoComplete -> {
                    AddressAutoCompleteScreen(
                        searchForCities = true,
                        showMapOption = true
                    ) { mAddress, _ ->
                        addressSelected = mAddress
                        coroutineScope.launch {
                            bottomSheetContent = BottomSheetView.Filters
                            modalSheetState.show()
                        }
                    }
                }
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (searchView == SearchView.GRID) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchTitle { onBackClicked() }

                    Box(
                        modifier = Modifier.padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SearchBarAndFilters {
                            coroutineScope.launch {
                                bottomSheetContent = BottomSheetView.Filters
                                modalSheetState.show()
                            }
                        }
                    }

                    if (filteredServices == null) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            TextRegular(
                                modifier = Modifier.align(Alignment.Center),
                                text = "Cargando servicios...",
                                size = 16.sp.autoSize()
                            )
                        }
                    } else {
                        if (filteredServices!!.isNotEmpty()) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(Constants.TWO_COLUMNS),
                                contentPadding = PaddingValues(vertical = 6.dp, horizontal = 2.dp),
                                modifier = Modifier.sidePadding(8.dp),
                            ) {
                                itemsIndexed(filteredServices!!) { i, item ->
                                    CardServiceOption(
                                        service = item,
                                        user = firebaseUserDb,
                                        isFavourite = likedServices.any { it.id == item.id },
                                        index = i,
                                        onItemClick = { service ->
                                            val screenInfo = ScreenInfo(
                                                role = firebaseUserDb?.role.getRole(),
                                                startedScreen = Screen.Shared,
                                                prevScreen = Screen.Shared,
                                                event = Event(id = context.getString(R.string.gral_shared)),
                                                questions = null,
                                                questionsProvider = null,
                                                serviceCategory = null,
                                                clientEventId = null,
                                                service = Service(service.id, context.getString(R.string.gral_shared))
                                            )
                                            onNavigateServiceSearched(screenInfo)
                                        },
                                        onHeartClick = { service ->
                                            if (firebaseUserDb == null) {
                                                showToast(context, "Inicie sesión primero para agregar a favoritos")
                                                return@CardServiceOption
                                            }
                                            firebaseUserDb?.id?.let { userId ->
                                                showProgressDialog = true
                                                vms.alreadyLikedService = false
                                                vms.likeService(userId, service.id) {
                                                    showProgressDialog = false
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        vms.alreadyLikedService = false
                                                    },ONE_SECOND)
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        } else {
                            EmptyResults()
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    SeeMapOrGridButton("Ver Mapa", R.drawable.ic_map) {
                        searchView = SearchView.MAP
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchTitle { onBackClicked() }
                    Box(modifier = Modifier.fillMaxSize()) {
                        MapView(
                            services = filteredServices,
                            firebaseUserDb = firebaseUserDb,
                            likedServices = likedServices,
                            onNavigateServiceSearched = { screenInfo ->
                                onNavigateServiceSearched(screenInfo)
                            },
                            onDismiss = {
                                searchView = SearchView.GRID
                            }
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .shadow(3.dp, allRoundedCornerShape24)
                                        .background(Color.White, allRoundedCornerShape24)
                                        .clip(allRoundedCornerShape24)
                                        .padding(6.dp)
                                ) {
                                    SearchBarAndFilters {
                                        coroutineScope.launch {
                                            bottomSheetContent = BottomSheetView.Filters
                                            modalSheetState.show()
                                        }
                                    }
                                }

                                VerticalSpacer(height = 7.dp)

                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(servicesCategories) { serviceCategory ->
                                        serviceCategory?.let {
                                            if (serviceCategory.id == "0000") {
                                                serviceCategory.name = "Todos"
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .shadow(3.dp, allRoundedCornerShape24)
                                                    .background(
                                                        color = if (serviceCategory == serviceCategorySelected)
                                                            PinkFiestamas else Color.White,
                                                        shape = allRoundedCornerShape24
                                                    )
                                                    .padding(vertical = 5.dp, horizontal = 10.dp)
                                                    .clickable {
                                                        serviceCategorySelected = serviceCategory
                                                        vm.filterServicesByServiceCategory(
                                                            serviceCategory
                                                        )
                                                    }
                                            ) {
                                                TextSemiBold(
                                                    text = serviceCategory.name,
                                                    color = if (serviceCategory == serviceCategorySelected) Color.White else Color.Black,
                                                    size = 11.sp,
                                                    fillMaxWidth = false
                                                )
                                            }
                                        }

                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}



@SuppressLint("MissingPermission", "SuspiciousIndentation")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapView(
    vm: AddressAutoCompleteViewModel = hiltViewModel(),
    firebaseUserDb: FirebaseUserDb?,
    likedServices: List<Service>,
    services: List<Service>?,
    onDismiss: () -> Unit,
    onNavigateServiceSearched: (ScreenInfo) -> Unit
) {
    val context = LocalContext.current

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val viewState by vm.viewState.collectAsState()

    LaunchedEffect(!context.hasLocationPermission()) {
        permissionState.launchMultiplePermissionRequest()
    }

    when {
        permissionState.allPermissionsGranted -> {
            LaunchedEffect(Unit) {
                vm.handle(PermissionEvent.Granted)
            }
        }
        permissionState.shouldShowRationale -> { }
        !permissionState.allPermissionsGranted && !permissionState.shouldShowRationale -> {
            LaunchedEffect(Unit) {
                vm.handle(PermissionEvent.Revoked)
            }
        }
    }

    with(viewState) {
        when (this) {
            ViewState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            ViewState.RevokedPermissions -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = "Image",
                        modifier = Modifier
                            .size(50.dp)
                    )

                    VerticalSpacer(height = 5.dp)
                    TextSemiBold(text = stringResource(R.string.autocomplete_location_permissions_title))

                    VerticalSpacer(height = 8.dp)
                    TextRegular(text = stringResource(R.string.autocomplete_location_permissions_subtitle), size = 14.sp)

                    VerticalSpacer(height = 16.dp)
                    Button(
                        onClick = {
                            startActivity(context, Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), null)
                        },
                        enabled = !context.hasLocationPermission()
                    ) {
                        if (context.hasLocationPermission()) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = Color.White
                            )
                        } else {
                            Text(stringResource(R.string.autocomplete_location_permissions_settings))
                        }
                    }
                }
            }

            is ViewState.Success -> {
                val currentLoc =
                    LatLng(
                    location?.latitude ?: 0.0,
                    location?.longitude ?: 0.0
                    )
                MapSearchScreen(
                    firebaseUserDb = firebaseUserDb,
                    likedServices = likedServices,
                    services = services,
                    currentPosition = LatLng(
                        currentLoc.latitude,
                        currentLoc.longitude
                    ),
                    onClose = onDismiss,
                    onNavigateServiceSearched = onNavigateServiceSearched
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MapSearchScreen(
    vms: ServicesViewModel = hiltViewModel(),
    firebaseUserDb: FirebaseUserDb?,
    likedServices: List<Service>,
    services: List<Service>?,
    currentPosition: LatLng,
    onClose: () -> Unit,
    onNavigateServiceSearched: (ScreenInfo) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cameraZoom = 18f
    val markerPosition = LatLng(currentPosition.latitude, currentPosition.longitude)
    var showProgressDialog by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, cameraZoom)
    }

    val pagerState = rememberPagerState()

    ProgressDialog(showProgressDialog)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            )
        ) {
            services?.forEach { service ->
                val latitude = service.lat?.toDouble() ?: 0.0
                val longitude = service.lng?.toDouble() ?: 0.0
                val url = service.image
                if (latitude != 0.0 && longitude != 0.0 && !url.isNullOrEmpty()) {
                    val bitmapDescriptor = remember(url) {
                        mutableStateOf<BitmapDescriptor?>(null)
                    }
                    LaunchedEffect(url) {
                        coroutineScope.launch {
                            val bitmap = loadBitmapFromUrl(context, url)
                            bitmapDescriptor.value = bitmap?.let { BitmapDescriptorFactory.fromBitmap(it) }
                        }
                    }
                    bitmapDescriptor.value?.let { descriptor ->
                        Marker(
                            state = MarkerState(position = LatLng(latitude, longitude)),
                            title = service.name,
                            icon = descriptor
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
        ) {
            Column {
                SeeMapOrGridButton(
                    text = "Ver Lista",
                    icon = R.drawable.ic_grid,
                    onClick = { onClose() }
                )

                val filteredServices = services?.filter { service ->
                    val latitude = service.lat?.toDouble() ?: 0.0
                    val longitude = service.lng?.toDouble() ?: 0.0
                    val url = service.image
                    latitude != 0.0 && longitude != 0.0 && !url.isNullOrEmpty()
                }
                HorizontalPager(
                    count = filteredServices?.size ?: 0,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                ) { page ->
                    CardServiceOptionForHorizontalList(
                        service = filteredServices?.get(page),
                        user = firebaseUserDb,
                        isFavourite = likedServices.any { it.id == filteredServices?.get(page)?.id },
                        onItemClick = { service ->
                            val screenInfo = ScreenInfo(
                                role = firebaseUserDb?.role.getRole(),
                                startedScreen = Screen.Shared,
                                prevScreen = Screen.Shared,
                                event = Event(id = context.getString(R.string.gral_shared)),
                                questions = null,
                                questionsProvider = null,
                                serviceCategory = null,
                                clientEventId = null,
                                service = Service(service.id, context.getString(R.string.gral_shared))
                            )
                            onNavigateServiceSearched(screenInfo)
                        },
                        onHeartClick = { service ->
                            if (firebaseUserDb == null) {
                                showToast(context, "Inicie sesión primero para agregar a favoritos")
                                return@CardServiceOptionForHorizontalList
                            }
                            firebaseUserDb.id.let { userId ->
                                showProgressDialog = true
                                vms.alreadyLikedService = false
                                vms.likeService(userId, service.id) {
                                    showProgressDialog = false
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        vms.alreadyLikedService = false
                                    },ONE_SECOND)
                                }
                            }
                        }
                    )
                }

                LaunchedEffect(pagerState.currentPage) {
                    val visibleService = filteredServices?.get(pagerState.currentPage)
                    val latitude = visibleService?.lat?.toDouble() ?: 0.0
                    val longitude = visibleService?.lng?.toDouble() ?: 0.0
                    if (latitude != 0.0 && longitude != 0.0) {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), cameraZoom), 1
                        )
                    }
                }

            }
        }
    }
}
