package com.universal.fiestamas.presentation.navigation

import androidx.core.os.bundleOf
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.GoogleUserData
import com.universal.fiestamas.domain.models.LoginAccount
import com.universal.fiestamas.presentation.screens.auth.CreateBusinessScreenV2
import com.universal.fiestamas.presentation.screens.auth.CreateContactScreenV2
import com.universal.fiestamas.presentation.screens.auth.CreatePasswordScreenV1
import com.universal.fiestamas.presentation.screens.auth.CreatePasswordScreenV2
import com.universal.fiestamas.presentation.screens.auth.CreateProviderScreen
import com.universal.fiestamas.presentation.screens.auth.CreateUserScreen
import com.universal.fiestamas.presentation.screens.auth.LoginScreenV1
import com.universal.fiestamas.presentation.screens.auth.LoginScreenV2
import com.universal.fiestamas.presentation.screens.auth.StartEmailScreen
import com.universal.fiestamas.presentation.screens.auth.SuccessProviderAccountCreatedScreenV2
import com.universal.fiestamas.presentation.utils.extensions.clearBackStack
import com.universal.fiestamas.presentation.utils.extensions.getArgument
import com.universal.fiestamas.presentation.utils.extensions.navigate
import com.universal.fiestamas.presentation.utils.extensions.popBackStackMultiple

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.StartEmail.route
    ) {

        composable(route = AuthScreen.StartEmail.route) { backStackEntry ->
            if (backStackEntry.arguments?.getBoolean("showStartEmailScreen") == true) {
                // full screen view -> New design v2
                StartEmailScreen(
                    fullScreen = true,
                    refreshAppIfAccountIsCreated = false,
                    onBackClicked = { navController.popBackStack() },
                    onEmailValidated = { isNewAccountFromGmail, googleUserData, exists, email, account, refresh ->
                        val bundle = bundleOf(
                            "isNewAccountFromGmail" to isNewAccountFromGmail,
                            "googleUserData" to googleUserData,
                            "email" to email,
                            "includeStartScreen" to true,
                            "account" to account,
                            "refreshAppIfAccountIsCreated" to refresh,
                            "isV2" to true
                        )
                        if (exists) {
                            navController.navigate(AuthScreen.Login.route, bundle)
                        } else {
                            navController.navigate(AuthScreen.CreatePassword.route, bundle)
                        }
                    }
                )
                return@composable
            }

            val googleUserData: GoogleUserData? = backStackEntry.getArgument()
            val isNewAccountFromGmail = backStackEntry.arguments?.getBoolean("isNewAccountFromGmail") ?: false
            val email = backStackEntry.arguments?.getString("email").orEmpty()
            val accountExistsInDb = backStackEntry.arguments?.getBoolean("exists") ?: false
            val account: LoginAccount? = backStackEntry.getArgument()
            val mustRefreshApp = backStackEntry.arguments?.getBoolean("mustRefreshApp") ?: false
            val refreshAppIfAccountIsCreated = backStackEntry.arguments?.getBoolean("refreshAppIfAccountIsCreated") ?: false

            if (accountExistsInDb) {
                LoginScreenV1(
                    initialUserEmail = email,
                    mustRefreshApp = mustRefreshApp,
                    account = account,
                    onSuccessLogin = { navController.popBackStack() },
                    onBackClicked = { navController.popBackStack() }
                )
            } else {
                CreatePasswordScreenV1(
                    isNewAccountFromGmail = isNewAccountFromGmail,
                    initialUserEmail = email,
                    refreshAppIfAccountIsCreated = refreshAppIfAccountIsCreated,
                    onRedirectToCreateProvider = { pass, refresh ->
                        val bundle = bundleOf(
                            "email" to email,
                            "googleUserData" to googleUserData,
                            "password" to pass,
                            "refreshAppIfAccountIsCreated" to refresh
                        )
                        navController.navigate(AuthScreen.CreateProvider.route, bundle)
                    },
                    onRedirectToCreateUser = { pass, refresh ->
                        val bundle = bundleOf(
                            "googleUserData" to googleUserData,
                            "email" to email,
                            "password" to pass,
                            "refreshAppIfAccountIsCreated" to refresh
                        )
                        navController.navigate(AuthScreen.CreateUser.route, bundle)
                    },
                    onBackClicked = { navController.popBackStack() }
                )
            }
        }

        composable(route = AuthScreen.Login.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email").orEmpty()
            val isV2 = backStackEntry.arguments?.getBoolean("isV2") ?: false
            val account: LoginAccount? = backStackEntry.getArgument()
            if (isV2) {
                LoginScreenV2(
                    initialUserEmail = email,
                    account = account,
                    onSuccessLogin = {
                        navController.popBackStackMultiple(2)
                    },
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            } else {
                LoginScreenV1(
                    initialUserEmail = email,
                    account = account,
                    onSuccessLogin = {
                        navController.popBackStackMultiple(2)
                    },
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(route = AuthScreen.CreatePassword.route) { backStackEntry ->
            val isNewAccountFromGmail = backStackEntry.arguments?.getBoolean("isNewAccountFromGmail") ?: false
            val googleUserData: GoogleUserData? = backStackEntry.getArgument()
            val email = backStackEntry.arguments?.getString("email").orEmpty()
            val includeStartScreen = backStackEntry.arguments?.getBoolean("includeStartScreen") ?: false
            val refreshAppIfAccountIsCreated = backStackEntry.arguments?.getBoolean("refreshAppIfAccountIsCreated") ?: false
            val isV2 = backStackEntry.arguments?.getBoolean("isV2") ?: false

            if (isV2) {
                CreatePasswordScreenV2(
                    isNewAccountFromGmail = isNewAccountFromGmail,
                    initialUserEmail = email,
                    onRedirectToCreateBusiness = { pass ->
                        val bundle = bundleOf(
                            "email" to email,
                            "password" to pass,
                            "googleUserData" to googleUserData,
                            "includeStartScreen" to includeStartScreen
                        )
                        navController.navigate(AuthScreen.CreateBusiness.route, bundle)
                    },
                    onBackClicked = { navController.popBackStack() }
                )
            } else {
                CreatePasswordScreenV1(
                    isNewAccountFromGmail = isNewAccountFromGmail,
                    initialUserEmail = email,
                    refreshAppIfAccountIsCreated = refreshAppIfAccountIsCreated,
                    onRedirectToCreateProvider = { pass, refresh ->
                        val bundle = bundleOf(
                            "email" to email,
                            "googleUserData" to googleUserData,
                            "password" to pass,
                            "includeStartScreen" to includeStartScreen,
                            "refreshAppIfAccountIsCreated" to refresh
                        )
                        navController.navigate(AuthScreen.CreateProvider.route, bundle)
                    },
                    onRedirectToCreateUser = { pass, refresh ->
                        val bundle = bundleOf(
                            "email" to email,
                            "googleUserData" to googleUserData,
                            "password" to pass,
                            "includeStartScreen" to includeStartScreen,
                            "refreshAppIfAccountIsCreated" to refresh
                        )
                        navController.navigate(AuthScreen.CreateUser.route, bundle)
                    },
                    onBackClicked = { navController.popBackStack() }
                )
            }
        }

        composable(route = AuthScreen.CreateProvider.route) { backStackEntry ->
            val googleUserData: GoogleUserData? = backStackEntry.getArgument()
            val email = backStackEntry.arguments?.getString("email").orEmpty()
            val password = backStackEntry.arguments?.getString("password").orEmpty()
            val includeStartScreen = backStackEntry.arguments?.getBoolean("includeStartScreen") ?: false
            val refreshAppIfAccountIsCreated = backStackEntry.arguments?.getBoolean("refreshAppIfAccountIsCreated") ?: false

            CreateProviderScreen(
                email = email,
                googleUserData = googleUserData,
                password = password,
                refreshAppIfAccountIsCreated = refreshAppIfAccountIsCreated,
                onSuccessLogin = {
                    if (includeStartScreen) {
                        navController.popBackStackMultiple(3)
                    } else {
                        navController.popBackStackMultiple(2)
                    }
                },
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable(route = AuthScreen.CreateUser.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email").orEmpty()
            val googleUserData: GoogleUserData? = backStackEntry.getArgument()
            val password = backStackEntry.arguments?.getString("password").orEmpty()
            val includeStartScreen = backStackEntry.arguments?.getBoolean("includeStartScreen") ?: false
            val refreshAppIfAccountIsCreated = backStackEntry.arguments?.getBoolean("refreshAppIfAccountIsCreated") ?: false

            CreateUserScreen(
                email = email,
                googleUserData = googleUserData,
                password = password,
                refreshAppIfAccountIsCreated = refreshAppIfAccountIsCreated,
                onSuccessLogin = {
                    if (includeStartScreen) {
                        navController.popBackStackMultiple(3)
                    } else {
                        navController.popBackStackMultiple(2)
                    }
                },
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable(route = AuthScreen.CreateBusiness.route) { backStackEntry ->
            val googleUserData: GoogleUserData? = backStackEntry.getArgument()
            val email = backStackEntry.arguments?.getString("email").orEmpty()
            val password = backStackEntry.arguments?.getString("password").orEmpty()

            CreateBusinessScreenV2(
                onRedirectToCreateContact = { businessName, businessAddress, businessPhotoUrl ->
                    val bundle = bundleOf(
                        "email" to email,
                        "password" to password,
                        "googleUserData" to googleUserData,
                        "businessName" to businessName,
                        "businessAddress" to businessAddress,
                        "businessPhotoUrl" to businessPhotoUrl,
                    )
                    navController.navigate(AuthScreen.CreateContact.route, bundle)
                },
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = AuthScreen.CreateContact.route) { backStackEntry ->
            val googleUserData: GoogleUserData? = backStackEntry.getArgument()
            val email = backStackEntry.arguments?.getString("email").orEmpty()
            val password = backStackEntry.arguments?.getString("password").orEmpty()
            val businessPhotoUrl = backStackEntry.arguments?.getString("businessPhotoUrl").orEmpty()
            val businessName = backStackEntry.arguments?.getString("businessName").orEmpty()
            val businessAddress: Address? = backStackEntry.getArgument()

            CreateContactScreenV2(
                googleUserData = googleUserData,
                email = email,
                password = password,
                businessName = businessName,
                businessAddress = businessAddress,
                businessPhotoUrl = businessPhotoUrl,
                onBackClicked = { navController.popBackStack() },
                onSuccessProviderAccountCreated = {
                    navController.clearBackStack()
                    navController.navigate(AuthScreen.SuccessProviderAccount.route)
                }
            )
        }

        composable(route = AuthScreen.SuccessProviderAccount.route) { //backStackEntry ->
            SuccessProviderAccountCreatedScreenV2(
                onAddServiceCategoryV2 = {
                    navController.navigate(Graph.SERVICES_SELECTION_V2)
                }
            )
        }

        servicesSelectionNavGraphV2(navController = navController)
    }
}

sealed class AuthScreen(val route: String) {
    object StartEmail : AuthScreen(route = "START_EMAIL")
    object Login : AuthScreen(route = "LOGIN")
    object CreatePassword : AuthScreen(route = "CREATE_PASSWORD")
    object CreateProvider : AuthScreen(route = "CREATE_PROVIDER")
    object CreateBusiness : AuthScreen(route = "CREATE_BUSINESS")
    object CreateContact : AuthScreen(route = "CREATE_CONTACT")
    object SuccessProviderAccount : AuthScreen(route = "SUCCESS_PROVIDER_ACCOUNT")
    object CreateUser : AuthScreen(route = "CREATE_USER")
}
