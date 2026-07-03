package com.tailtown.pawcare.navigation

import android.app.Activity
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.razorpay.Checkout
import com.tailtown.pawcare.auth.AuthViewModel
import com.tailtown.pawcare.ui.health.HealthViewModel
import com.tailtown.pawcare.ui.home.HomeScreen
import com.tailtown.pawcare.ui.inbox.ChatScreen
import com.tailtown.pawcare.ui.inbox.InboxScreen
import com.tailtown.pawcare.ui.inbox.InboxViewModel
import com.tailtown.pawcare.ui.inbox.NotificationsScreen
import com.tailtown.pawcare.ui.home.PetProfileScreen
import com.tailtown.pawcare.ui.onboarding.AddPetScreen
import com.tailtown.pawcare.ui.onboarding.OtpScreen
import com.tailtown.pawcare.ui.onboarding.PermissionsScreen
import com.tailtown.pawcare.ui.onboarding.PhoneAuthScreen
import com.tailtown.pawcare.ui.onboarding.WelcomeScreen
import com.tailtown.pawcare.ui.account.AccountScreen
import com.tailtown.pawcare.ui.account.AccountViewModel
import com.tailtown.pawcare.ui.account.AddressesScreen
import com.tailtown.pawcare.ui.account.EditProfileScreen
import com.tailtown.pawcare.ui.account.HelpSupportScreen
import com.tailtown.pawcare.ui.account.OrderHistoryScreen
import com.tailtown.pawcare.ui.account.PaymentMethodsScreen
import com.tailtown.pawcare.ui.account.ReferFriendScreen
import com.tailtown.pawcare.ui.account.SettingsScreen
import com.tailtown.pawcare.ui.account.SubscriptionsScreen
import com.tailtown.pawcare.ui.health.MedicalTimelineScreen
import com.tailtown.pawcare.ui.health.PrescriptionScreen
import com.tailtown.pawcare.ui.health.VaccinationScheduleScreen
import com.tailtown.pawcare.ui.health.WeightTrackerScreen
import com.tailtown.pawcare.ui.shop.CartItem
import com.tailtown.pawcare.ui.shop.CartScreen
import com.tailtown.pawcare.ui.shop.CartViewModel
import com.tailtown.pawcare.ui.shop.CategoryScreen
import com.tailtown.pawcare.ui.shop.CategoryViewModel
import com.tailtown.pawcare.ui.shop.CheckoutScreen
import com.tailtown.pawcare.ui.shop.CheckoutUiState
import com.tailtown.pawcare.ui.shop.CheckoutViewModel
import com.tailtown.pawcare.ui.shop.MallHomeScreen
import com.tailtown.pawcare.ui.shop.OrderPlacedScreen
import com.tailtown.pawcare.ui.shop.PaymentFailedScreen
import com.tailtown.pawcare.ui.shop.PaymentPendingScreen
import com.tailtown.pawcare.ui.shop.PaymentVerifyingOverlay
import com.tailtown.pawcare.ui.shop.ProductDetailScreen
import com.tailtown.pawcare.ui.shop.ShopProductsViewModel
import com.tailtown.pawcare.ui.vet.BookedScreen
import com.tailtown.pawcare.ui.vet.MyBookingsScreen
import com.tailtown.pawcare.ui.vet.MyBookingsViewModel
import androidx.compose.runtime.LaunchedEffect
import com.tailtown.pawcare.ui.vet.VetDetailScreen
import com.tailtown.pawcare.ui.vet.VetDetailViewModel
import com.tailtown.pawcare.ui.vet.VetDirectoryScreen
import com.tailtown.pawcare.ui.vet.VetDirectoryViewModel
import com.tailtown.pawcare.ui.home.HomeViewModel
import com.tailtown.pawcare.ui.home.PetViewModel
import com.tailtown.pawcare.ui.shop.PromotionViewModel
import org.json.JSONObject

sealed class Screen(val route: String) {
    // Onboarding & Auth
    object Welcome : Screen("welcome")
    object PhoneAuth : Screen("phone_auth")
    object Otp : Screen("otp/{phone}") {
        fun createRoute(phone: String) = "otp/$phone"
    }
    object AddPet : Screen("add_pet")
    object Permissions : Screen("permissions")

    // Home
    object Home : Screen("home")
    object PetProfile : Screen("pet_profile")

    // Vet Appointments
    object VetDirectory : Screen("vet_directory")
    object VetDetail : Screen("vet_detail/{vetId}") {
        fun createRoute(vetId: String) = "vet_detail/$vetId"
    }
    object Booked : Screen("booked/{vetId}?bookingDate={bookingDate}&bookingTime={bookingTime}") {
        fun createRoute(vetId: String, date: String, time: String) =
            "booked/$vetId?bookingDate=${Uri.encode(date)}&bookingTime=${Uri.encode(time)}"
    }
    object MyBookings : Screen("my_bookings")

    // Pet Mall Shopping
    object MallHome : Screen("mall_home")
    object Category : Screen("category/{categoryId}") {
        fun createRoute(categoryId: String) = "category/$categoryId"
    }
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object OrderPlaced : Screen("order_placed/{orderId}?amount={amount}") {
        fun createRoute(orderId: String, amount: Int? = null) =
            "order_placed/${Uri.encode(orderId)}?amount=${amount ?: ""}"
    }

    // Records & Health
    object VaccinationSchedule : Screen("vaccination_schedule")
    object MedicalTimeline : Screen("medical_timeline")
    object Prescription : Screen("prescription/{prescriptionId}") {
        fun createRoute(id: String) = "prescription/$id"
    }
    object WeightTracker : Screen("weight_tracker")

    // Inbox & Account
    object Account : Screen("account")
    object EditProfile : Screen("edit_profile")
    object Addresses : Screen("addresses")
    object Subscriptions : Screen("subscriptions")
    object PaymentMethods : Screen("payment_methods")
    object OrderHistory : Screen("order_history")
    object ReferFriend : Screen("refer_friend")
    object HelpSupport : Screen("help_support")
    object Settings : Screen("settings")
    object Inbox : Screen("inbox")
    object Chat : Screen("chat/{conversationId}") {
        fun createRoute(id: String) = "chat/$id"
    }
    object Notifications : Screen("notifications")
}

@Composable
fun PawcareNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String = Screen.Welcome.route,
    modifier: Modifier = Modifier,
) {
    val cartViewModel: CartViewModel = hiltViewModel()
    val accountViewModel: AccountViewModel = hiltViewModel()
    val vetViewModel: VetDirectoryViewModel = hiltViewModel()
    val myBookingsViewModel: MyBookingsViewModel = hiltViewModel()
    val shopProductsViewModel: ShopProductsViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val petViewModel: PetViewModel = hiltViewModel()
    val promotionViewModel: PromotionViewModel = hiltViewModel()
    val inboxViewModel: InboxViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {

        // ── Onboarding ─────────────────────────────────────────────────────

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStarted = { navController.navigate(Screen.PhoneAuth.route) },
                onSignIn = { navController.navigate(Screen.PhoneAuth.route) },
            )
        }

        composable(Screen.PhoneAuth.route) {
            PhoneAuthScreen(
                authViewModel = authViewModel,
                onSignedIn = { isNewUser ->
                    if (isNewUser) {
                        navController.navigate(Screen.AddPet.route) {
                            popUpTo(Screen.PhoneAuth.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
            )
        }

        composable(
            route = Screen.Otp.route,
            arguments = listOf(navArgument("phone") { type = NavType.StringType }),
        ) { back ->
            OtpScreen(
                phone = back.arguments?.getString("phone").orEmpty(),
                authViewModel = authViewModel,
                onNewUser = {
                    navController.navigate(Screen.AddPet.route) {
                        popUpTo(Screen.PhoneAuth.route) { inclusive = true }
                    }
                },
                onReturningUser = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.AddPet.route) {
            AddPetScreen(
                onAddPet = { name, breed, age, species, weight ->
                    petViewModel.createPet(name, breed, age, species, weight) {
                        navController.navigate(Screen.Permissions.route) {
                            popUpTo(Screen.AddPet.route) { inclusive = true }
                        }
                    }
                },
                onSkip = { navController.navigate(Screen.Permissions.route) },
            )
        }

        composable(Screen.Permissions.route) {
            PermissionsScreen(
                onAllowLocation = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNotNow = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        // ── Home ───────────────────────────────────────────────────────────

        composable(Screen.Home.route) {
            val vets by homeViewModel.vets.collectAsStateWithLifecycle()
            val petName by petViewModel.petName.collectAsStateWithLifecycle()
            val foodProducts by homeViewModel.foodProducts.collectAsStateWithLifecycle()
            val toyProducts by homeViewModel.toyProducts.collectAsStateWithLifecycle()
            val groomers by homeViewModel.groomers.collectAsStateWithLifecycle()
            HomeScreen(
                vets = (vets as? com.tailtown.pawcare.common.UiState.Success)?.data ?: emptyList(),
                petName = petName,
                foodProducts = foodProducts,
                toyProducts = toyProducts,
                groomers = groomers,
                onVetClick = { vetId -> navController.navigate(Screen.VetDetail.createRoute(vetId)) },
                onShowAllVets = { navController.navigate(Screen.VetDirectory.route) },
                onProductClick = { productId -> navController.navigate(Screen.ProductDetail.createRoute(productId)) },
                onTabSelected = homeViewModel::onTabSelected,
                onInbox = { navController.navigate(Screen.Inbox.route) },
                onVisits = {
                    navController.navigate(Screen.MyBookings.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onShop = { navController.navigate(Screen.MallHome.route) },
                onPetProfile = { navController.navigate(Screen.Account.route) },
            )
        }

        composable(Screen.PetProfile.route) {
            val petName   by petViewModel.petName.collectAsStateWithLifecycle()
            val petBreed  by petViewModel.petBreed.collectAsStateWithLifecycle()
            val petGender by petViewModel.petGender.collectAsStateWithLifecycle()
            val petAge    by petViewModel.petAge.collectAsStateWithLifecycle()
            val petWeight by petViewModel.petWeight.collectAsStateWithLifecycle()
            PetProfileScreen(
                petName   = petName,
                petBreed  = petBreed,
                petGender = petGender,
                petAge    = petAge,
                petWeight = petWeight,
                onBookCheckup = { navController.navigate(Screen.VetDirectory.route) },
                onViewVaccines = { navController.navigate(Screen.VaccinationSchedule.route) },
                onViewTimeline = { navController.navigate(Screen.MedicalTimeline.route) },
                onViewWeight = { navController.navigate(Screen.WeightTracker.route) },
                onBack = { navController.popBackStack() },
            )
        }

        // ── Records & Health ───────────────────────────────────────────────

        composable(Screen.VaccinationSchedule.route) {
            val petName by petViewModel.petName.collectAsStateWithLifecycle()
            VaccinationScheduleScreen(
                petName = petName,
                onBack = { navController.popBackStack() },
                onBookNow = { navController.navigate(Screen.VetDirectory.route) },
            )
        }

        composable(Screen.MedicalTimeline.route) {
            val petName by petViewModel.petName.collectAsStateWithLifecycle()
            MedicalTimelineScreen(
                petName = petName,
                onBack = { navController.popBackStack() },
                onViewPrescription = { id ->
                    navController.navigate(Screen.Prescription.createRoute(id))
                },
            )
        }

        composable(
            route = Screen.Prescription.route,
            arguments = listOf(navArgument("prescriptionId") { type = NavType.StringType }),
        ) { back ->
            val healthViewModel: HealthViewModel = hiltViewModel()
            val prescription by healthViewModel.prescription.collectAsStateWithLifecycle()
            PrescriptionScreen(
                prescriptionId = back.arguments?.getString("prescriptionId").orEmpty(),
                prescription = prescription,
                onMarkDose = healthViewModel::markDose,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.WeightTracker.route) {
            val healthViewModel: HealthViewModel = hiltViewModel()
            val weightPoints by healthViewModel.weightPoints.collectAsStateWithLifecycle()
            val petName  by petViewModel.petName.collectAsStateWithLifecycle()
            val petBreed by petViewModel.petBreed.collectAsStateWithLifecycle()
            WeightTrackerScreen(
                weightPoints = weightPoints,
                petName  = petName,
                petBreed = petBreed,
                onBack = { navController.popBackStack() },
                onLogWeight = { healthViewModel.logWeight((weightPoints.lastOrNull()?.value ?: 28.4f) + 0.1f) },
            )
        }

        // ── Vet Appointments ───────────────────────────────────────────────

        composable(Screen.VetDirectory.route) {
            val vets by vetViewModel.vets.collectAsStateWithLifecycle()
            val addresses by accountViewModel.addresses.collectAsStateWithLifecycle()
            val city = addresses.firstOrNull { it.isDefault }?.city
                ?: addresses.firstOrNull()?.city
                ?: ""
            VetDirectoryScreen(
                vets = vets,
                city = city,
                onVetClick = { vetId -> navController.navigate(Screen.VetDetail.createRoute(vetId)) },
            )
        }

        composable(
            route = Screen.VetDetail.route,
            arguments = listOf(navArgument("vetId") { type = NavType.StringType }),
        ) { back ->
            val vetId = back.arguments?.getString("vetId").orEmpty()
            val vets by vetViewModel.vets.collectAsStateWithLifecycle()
            val vet = vets.find { it.id == vetId } ?: vets.firstOrNull()
            val vetDetailViewModel: VetDetailViewModel = hiltViewModel()
            val slotState by vetDetailViewModel.slotState.collectAsStateWithLifecycle()
            androidx.compose.runtime.LaunchedEffect(vetId) { vetDetailViewModel.initialize(vetId) }
            if (vet != null) {
                VetDetailScreen(
                    vet = vet,
                    slotState = slotState,
                    onDateSelected = vetDetailViewModel::selectDate,
                    onTimeSelected = vetDetailViewModel::selectTime,
                    onBack = { navController.popBackStack() },
                    onReserve = { date, time ->
                        vetDetailViewModel.reserve(vetId) {
                            myBookingsViewModel.refresh()
                            navController.navigate(Screen.Booked.createRoute(vetId, date, time))
                        }
                    },
                )
            }
        }

        composable(
            route = Screen.Booked.route,
            arguments = listOf(
                navArgument("vetId") { type = NavType.StringType },
                navArgument("bookingDate") { type = NavType.StringType; defaultValue = "" },
                navArgument("bookingTime") { type = NavType.StringType; defaultValue = "" },
            ),
        ) { back ->
            val vetId       = back.arguments?.getString("vetId").orEmpty()
            val bookingDate = back.arguments?.getString("bookingDate").orEmpty()
            val bookingTime = back.arguments?.getString("bookingTime").orEmpty()
            val vets by vetViewModel.vets.collectAsStateWithLifecycle()
            val vet = vets.find { it.id == vetId } ?: vets.firstOrNull()
            val petName by petViewModel.petName.collectAsStateWithLifecycle()
            if (vet != null) {
                BookedScreen(
                    vet = vet,
                    petName = petName,
                    bookingWhen = listOf(bookingDate, bookingTime).filter { it.isNotBlank() }.joinToString(" · "),
                    onAddToCalendar = { navController.navigate(Screen.MyBookings.route) },
                    onMyBookings = { navController.navigate(Screen.MyBookings.route) },
                )
            }
        }

        composable(Screen.MyBookings.route) {
            LaunchedEffect(Unit) { myBookingsViewModel.refresh() }
            val bookings by myBookingsViewModel.bookings.collectAsStateWithLifecycle()
            MyBookingsScreen(
                bookings = bookings,
                onCancelBooking = myBookingsViewModel::cancelBooking,
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onInbox = { navController.navigate(Screen.Inbox.route) },
                onShop = { navController.navigate(Screen.MallHome.route) },
                onPetProfile = { navController.navigate(Screen.Account.route) },
            )
        }

        // ── Pet Mall Shopping ──────────────────────────────────────────────

        composable(Screen.MallHome.route) {
            val products by shopProductsViewModel.products.collectAsStateWithLifecycle()
            val promotion by promotionViewModel.promotion.collectAsStateWithLifecycle()
            val petName by petViewModel.petName.collectAsStateWithLifecycle()
            val unreadCount by accountViewModel.unreadCount.collectAsStateWithLifecycle()
            MallHomeScreen(
                products = products,
                promotion = promotion,
                petName = petName,
                unreadCount = unreadCount,
                onCategoryClick = { id -> navController.navigate(Screen.Category.createRoute(id)) },
                onProductClick = { id -> navController.navigate(Screen.ProductDetail.createRoute(id)) },
                onSeeAllPicks = { navController.navigate(Screen.Category.createRoute("all")) },
                onCartClick = { navController.navigate(Screen.Cart.route) },
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onInbox = { navController.navigate(Screen.Inbox.route) },
                onVisits = { navController.navigate(Screen.MyBookings.route) },
                onPetProfile = { navController.navigate(Screen.Account.route) },
            )
        }

        composable(
            route = Screen.Category.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType }),
        ) { back ->
            val categoryId = back.arguments?.getString("categoryId").orEmpty()
            val categoryViewModel: CategoryViewModel = hiltViewModel()
            val uiState by categoryViewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(categoryId) { categoryViewModel.init(categoryId) }
            CategoryScreen(
                categoryLabel = uiState.categoryLabel.ifBlank {
                    categoryId.replaceFirstChar { it.uppercaseChar() }
                },
                filters = uiState.filters,
                selectedFilter = uiState.selectedFilter,
                products = uiState.products,
                isLoading = uiState.isLoading,
                onFilterSelect = categoryViewModel::selectFilter,
                onBack = { navController.popBackStack() },
                onProductClick = { id -> navController.navigate(Screen.ProductDetail.createRoute(id)) },
            )
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType }),
        ) { back ->
            val productId = back.arguments?.getString("productId").orEmpty()
            val products by shopProductsViewModel.products.collectAsStateWithLifecycle()
            val product = products.find { it.id == productId } ?: products.firstOrNull()
            if (product != null) {
                ProductDetailScreen(
                    product = product,
                    onBack = { navController.popBackStack() },
                    onAddToCart = {
                        cartViewModel.addItem(
                            CartItem(
                                product = product,
                                variantLabel = product.subtitle,
                                qty = 1,
                                unitPrice = product.price,
                            )
                        )
                        navController.navigate(Screen.Cart.route)
                    },
                )
            }
        }

        composable(Screen.Cart.route) {
            val cartState by cartViewModel.cartState.collectAsStateWithLifecycle()
            CartScreen(
                items = cartState.items,
                subtotal = cartState.subtotal,
                subscriptionSaving = cartState.subscriptionSaving,
                total = cartState.total,
                onIncrement = cartViewModel::increment,
                onDecrement = cartViewModel::decrement,
                onBack = { navController.popBackStack() },
                onCheckout = { navController.navigate(Screen.Checkout.route) },
            )
        }

        composable(Screen.Checkout.route) {
            val cartState by cartViewModel.cartState.collectAsStateWithLifecycle()
            val addresses by accountViewModel.addresses.collectAsStateWithLifecycle()
            val phone by accountViewModel.phone.collectAsStateWithLifecycle()
            val defaultAddress = addresses.firstOrNull { it.isDefault } ?: addresses.firstOrNull()
            val deliveryAddress = defaultAddress?.let { "${it.street}, ${it.city} ${it.pincode}" } ?: ""

            val checkoutViewModel: CheckoutViewModel = hiltViewModel()
            val checkoutState by checkoutViewModel.uiState.collectAsStateWithLifecycle()
            val activity = LocalContext.current as Activity

            // Opens Razorpay's own checkout sheet once an order + gateway order have been created.
            LaunchedEffect(checkoutState) {
                val awaiting = checkoutState as? CheckoutUiState.AwaitingPayment ?: return@LaunchedEffect
                val checkout = Checkout()
                checkout.setKeyID(awaiting.keyId)
                val options = JSONObject().apply {
                    put("name", "TailTown")
                    put("description", "Order payment")
                    put("currency", awaiting.currency)
                    put("order_id", awaiting.razorpayOrderId)
                    put("amount", awaiting.amountPaise)
                }
                checkout.open(activity, options)
            }

            // Only navigate to the real order-placed screen once the backend has confirmed payment.
            LaunchedEffect(checkoutState) {
                val success = checkoutState as? CheckoutUiState.Success ?: return@LaunchedEffect
                navController.navigate(Screen.OrderPlaced.createRoute(success.orderNumber, success.amount)) {
                    popUpTo(Screen.Cart.route) { inclusive = true }
                }
            }

            when (val state = checkoutState) {
                is CheckoutUiState.Verifying -> PaymentVerifyingOverlay()
                is CheckoutUiState.Pending -> PaymentPendingScreen(
                    onGoToOrders = {
                        navController.navigate(Screen.OrderHistory.route) {
                            popUpTo(Screen.Cart.route) { inclusive = true }
                        }
                    },
                )
                is CheckoutUiState.Failed -> PaymentFailedScreen(
                    reason = state.reason,
                    cancelled = state.cancelled,
                    onRetry = { defaultAddress?.let { checkoutViewModel.retry(it.id) } },
                    onContactSupport = { navController.navigate(Screen.HelpSupport.route) },
                    onBack = { navController.popBackStack() },
                )
                else -> CheckoutScreen(
                    total = cartState.total,
                    itemCount = cartState.items.size,
                    deliveryAddress = deliveryAddress,
                    phone = phone,
                    isPlacingOrder = state is CheckoutUiState.PlacingOrder || state is CheckoutUiState.AwaitingPayment,
                    onBack = { navController.popBackStack() },
                    onPlaceOrder = { defaultAddress?.let { checkoutViewModel.placeOrder(it.id) } },
                )
            }
        }

        composable(
            route = Screen.OrderPlaced.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType; defaultValue = "" },
            ),
        ) { back ->
            val petName by petViewModel.petName.collectAsStateWithLifecycle()
            OrderPlacedScreen(
                orderId = back.arguments?.getString("orderId").orEmpty(),
                petName = petName,
                amount = back.arguments?.getString("amount")?.toIntOrNull(),
                onViewOrder = { /* future: order detail */ },
                onContinueShopping = {
                    navController.navigate(Screen.MallHome.route) {
                        popUpTo(Screen.MallHome.route) { inclusive = true }
                    }
                },
            )
        }

        // ── Inbox & Account ────────────────────────────────────────────────

        composable(Screen.Account.route) {
            AccountScreen(
                viewModel = accountViewModel,
                onViewPets = { navController.navigate(Screen.PetProfile.route) },
                onViewProfile = { navController.navigate(Screen.EditProfile.route) },
                onAddresses = { navController.navigate(Screen.Addresses.route) },
                onSubscriptions = { navController.navigate(Screen.Subscriptions.route) },
                onPaymentMethods = { navController.navigate(Screen.PaymentMethods.route) },
                onOrderHistory = { navController.navigate(Screen.OrderHistory.route) },
                onReferFriend = { navController.navigate(Screen.ReferFriend.route) },
                onHelpSupport = { navController.navigate(Screen.HelpSupport.route) },
                onSettings = { navController.navigate(Screen.Settings.route) },
                onInbox = { navController.navigate(Screen.Inbox.route) },
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onVisits = { navController.navigate(Screen.MyBookings.route) },
                onShop = { navController.navigate(Screen.MallHome.route) },
            )
        }

        composable(Screen.EditProfile.route) {
            val name  by accountViewModel.name.collectAsStateWithLifecycle()
            val phone by accountViewModel.phone.collectAsStateWithLifecycle()
            val email by accountViewModel.email.collectAsStateWithLifecycle()
            EditProfileScreen(
                initialName  = name,
                initialPhone = phone,
                initialEmail = email,
                onSave = { n, p, e ->
                    accountViewModel.updateProfile(n, p, e)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Addresses.route) {
            val addresses by accountViewModel.addresses.collectAsStateWithLifecycle()
            AddressesScreen(
                addresses    = addresses,
                onSetDefault = accountViewModel::setDefaultAddress,
                onDelete     = accountViewModel::deleteAddress,
                onAddNew     = { /* future: AddAddressScreen */ },
                onBack       = { navController.popBackStack() },
            )
        }

        composable(Screen.Subscriptions.route) {
            val subscriptions by accountViewModel.subscriptions.collectAsStateWithLifecycle()
            SubscriptionsScreen(
                subscriptions = subscriptions,
                onToggle      = accountViewModel::toggleSubscription,
                onBack        = { navController.popBackStack() },
            )
        }

        composable(Screen.PaymentMethods.route) {
            val methods by accountViewModel.paymentMethods.collectAsStateWithLifecycle()
            PaymentMethodsScreen(
                methods      = methods,
                onSetDefault = accountViewModel::setDefaultPayment,
                onDelete     = accountViewModel::deletePayment,
                onAddNew     = { /* future: AddPaymentScreen */ },
                onBack       = { navController.popBackStack() },
            )
        }

        composable(Screen.OrderHistory.route) {
            val orders by accountViewModel.orders.collectAsStateWithLifecycle()
            OrderHistoryScreen(
                orders    = orders,
                onReorder = { navController.navigate(Screen.MallHome.route) },
                onBack    = { navController.popBackStack() },
            )
        }

        composable(Screen.ReferFriend.route) {
            val referralInfo by accountViewModel.referralInfo.collectAsStateWithLifecycle()
            ReferFriendScreen(
                referral = referralInfo ?: com.tailtown.pawcare.ui.account.sampleReferral,
                onShareCode = { /* platform share sheet — needs Android Intent */ },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.HelpSupport.route) {
            HelpSupportScreen(
                onChatSupport  = { navController.navigate(Screen.Inbox.route) },
                onCallSupport  = { /* phone Intent — needs platform call */ },
                onEmailSupport = { /* email Intent — needs platform email */ },
                onBack         = { navController.popBackStack() },
            )
        }

        composable(Screen.Settings.route) {
            val notifPrefs by accountViewModel.notifPrefs.collectAsStateWithLifecycle()
            SettingsScreen(
                prefs              = notifPrefs,
                onAppointmentNotif = accountViewModel::setAppointmentNotif,
                onMedicationNotif  = accountViewModel::setMedicationNotif,
                onOrderNotif       = accountViewModel::setOrderNotif,
                onPromoNotif       = accountViewModel::setPromoNotif,
                onBack             = { navController.popBackStack() },
            )
        }

        composable(Screen.Inbox.route) {
            val conversations by inboxViewModel.filteredConversations.collectAsStateWithLifecycle()
            val selectedFilter by inboxViewModel.selectedFilter.collectAsStateWithLifecycle()
            InboxScreen(
                conversations = conversations,
                selectedFilter = selectedFilter,
                onFilterChange = inboxViewModel::setFilter,
                onConversationClick = { id ->
                    inboxViewModel.markRead(id)
                    navController.navigate(Screen.Chat.createRoute(id))
                },
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onVisits = { navController.navigate(Screen.MyBookings.route) },
                onShop = { navController.navigate(Screen.MallHome.route) },
                onAccount = { navController.navigate(Screen.Account.route) },
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType }),
        ) { back ->
            val conversationId = back.arguments?.getString("conversationId").orEmpty()
            val conversations by inboxViewModel.conversations.collectAsStateWithLifecycle()
            val messages by inboxViewModel.messages.collectAsStateWithLifecycle()
            val contactName = conversations.find { it.id == conversationId }?.name ?: ""
            ChatScreen(
                conversationId = conversationId,
                contactName = contactName,
                messages = messages,
                onSendMessage = { text -> inboxViewModel.sendMessage(conversationId, text) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(
                onBack = { navController.popBackStack() },
                onAppointmentTap = { navController.navigate(Screen.MyBookings.route) },
                onMedicationTap = { navController.navigate(Screen.Prescription.createRoute("rx1")) },
                onDeliveryTap = { navController.navigate(Screen.MallHome.route) },
                onPromoTap = { navController.navigate(Screen.Category.createRoute("food")) },
            )
        }
    }
}
