package com.tailtown.pawcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.tailtown.pawcare.auth.AuthViewModel
import com.tailtown.pawcare.navigation.PawcareNavGraph
import com.tailtown.pawcare.navigation.Screen
import com.tailtown.pawcare.payment.RazorpayResultBridge
import com.tailtown.pawcare.ui.theme.PawcareTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentResultWithDataListener {

    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var razorpayResultBridge: RazorpayResultBridge

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        if (razorpayPaymentId == null) return
        razorpayResultBridge.emitSuccess(
            razorpayPaymentId = razorpayPaymentId,
            razorpayOrderId = paymentData?.orderId,
            razorpaySignature = paymentData?.signature,
        )
    }

    override fun onPaymentError(code: Int, response: String?, paymentData: PaymentData?) {
        razorpayResultBridge.emitFailure(code, response)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PawcareTheme {
                val navController = rememberNavController()

                val startDestination = if (authViewModel.isLoggedIn) {
                    Screen.Home.route
                } else {
                    Screen.Welcome.route
                }

                // When tokens are cleared mid-session (access token expired + refresh failed,
                // or explicit sign-out), navigate back to the welcome screen immediately.
                LaunchedEffect(Unit) {
                    authViewModel.sessionActiveFlow.collect { token ->
                        if (token == null) {
                            navController.navigate(Screen.Welcome.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }

                PawcareNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    startDestination = startDestination,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
