package com.eco.musicplayer.audioplayer.music.ggbilling

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BillingManager(
    private val context: Context,
    private val onBillingReady: () -> Unit = {},
    private val onPurchaseSuccess: (Purchase) -> Unit,
    private val onPurchaseFailed: (String) -> Unit
) {
    private lateinit var billingClient: BillingClient
    private val scope = CoroutineScope(Dispatchers.Main)
    private var isReady = false
    private val TAG = "BillingDebug"

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        Log.d(TAG, "Purchases updated: responseCode=${billingResult.responseCode}")
        handlePurchaseUpdate(billingResult, purchases)
    }

    init {
        Log.i(TAG, "Initializing BillingManager")
        initializeBillingClient()
    }

    private fun initializeBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .enablePrepaidPlans()
                    .build()
            )
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Log.i(TAG, "Billing setup finished: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isReady = true
                    scope.launch {
                        queryExistingPurchases()
                        onBillingReady()
                    }
                } else {
                    onPurchaseFailed("Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected, reconnecting...")
                isReady = false
                initializeBillingClient()
            }
        })
    }

    fun queryProductDetails(productId: String, productType: String, onResult: (ProductDetails?) -> Unit) {
        if (!isReady) {
            Log.e(TAG, "BillingClient not ready yet")
            onResult(null)
            return
        }

        Log.i(TAG, "Querying product: $productId (type: $productType)")
        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(productId)
            .setProductType(productType)
            .build()
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(product))
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            Log.i(TAG, "Query result: ${billingResult.responseCode} - Details count: ${productDetailsList.size}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                val details = productDetailsList[0]

                details.subscriptionOfferDetails?.forEachIndexed { index, offer ->
                    Log.i(TAG, "Offer $index (All Offers):")
                    Log.i(TAG, "  Offer ID: ${offer.offerId}")
                    Log.i(TAG, "  Offer Tags: ${offer.offerTags}")
                    Log.i(TAG, "  Base Plan ID: ${offer.basePlanId}")
                    Log.i(TAG, "  Offer Token: ${offer.offerToken}")
                    offer.pricingPhases.pricingPhaseList.forEachIndexed { phaseIndex, phase ->
                        Log.i(TAG, "    Pricing Phase $phaseIndex:")
                        Log.i(TAG, "      Price: ${phase.formattedPrice}")
                        Log.i(TAG, "      Billing Period: ${phase.billingPeriod}")
                        Log.i(TAG, "      Recurrence Mode: ${phase.recurrenceMode}")
                        Log.i(TAG, "      Billing Cycle Count: ${phase.billingCycleCount}")
                        Log.i(TAG, "      Price Amount Micros: ${phase.priceAmountMicros}")
                        Log.i(TAG, "      Price Currency Code: ${phase.priceCurrencyCode}")
                    }
                }
                Log.i(TAG, "Product: ${details.title}, Price: ${details.oneTimePurchaseOfferDetails?.formattedPrice ?: "Sub offers: ${details.subscriptionOfferDetails?.size}"}")
                onResult(details)
            } else {
                Log.e(TAG, "Query failed: ${billingResult.debugMessage}")
                onResult(null)
                onPurchaseFailed("Query product failed: ${billingResult.debugMessage}")
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails, offerId: String = "") {
        val offerToken = if (offerId.isNotEmpty()) {
            productDetails.subscriptionOfferDetails?.find {
                it.offerId == offerId || it.offerTags.contains(offerId) }?.offerToken ?: ""
        } else {
            ""
        }
        Log.i(TAG, "Selected offer token: $offerToken for offerId: $offerId")

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .apply { if (offerToken.isNotEmpty()) setOfferToken(offerToken) }
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        Log.i(TAG, "Launch flow result: ${result.responseCode}")
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            onPurchaseFailed("Launch flow failed: ${result.debugMessage}")
        }
    }

    private fun queryExistingPurchases() {
        Log.i(TAG, "Querying existing in-app purchases")
        val params = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()
        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            Log.i(TAG, "In-app query: ${billingResult.responseCode}, count: ${purchases.size}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.forEach { handlePurchase(it) }
            }
        }

        Log.i(TAG, "Querying existing subscriptions")
        val subParams = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        billingClient.queryPurchasesAsync(subParams) { billingResult, purchases ->
            Log.i(TAG, "Subs query: ${billingResult.responseCode}, count: ${purchases.size}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.forEach { handlePurchase(it) }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        Log.i(TAG, "Handling purchase: ${purchase.products[0]}, state: ${purchase.purchaseState}")
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            verifyPurchaseOnServer(purchase.purchaseToken) { isValid ->
                if (isValid) {
                    scope.launch {
                        if (purchase.products[0].contains("subs")) {
                            Log.i(TAG, "Acknowledging sub purchase")
                            acknowledgePurchase(purchase)
                        } else {
                            Log.i(TAG, "Consuming in-app purchase")
                            consumePurchase(purchase)
                        }
                        onPurchaseSuccess(purchase)
                    }
                } else {
                    Log.e(TAG, "Invalid purchase")
                    onPurchaseFailed("Invalid purchase")
                }
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            Log.w(TAG, "Purchase pending: ${purchase.purchaseToken}")
            onPurchaseFailed("Purchase pending")
        }
    }

    private fun handlePurchaseUpdate(billingResult: BillingResult, purchases: List<Purchase>?) {
        Log.i(TAG, "Purchase update: ${billingResult.responseCode}, purchases count: ${purchases?.size}")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !purchases.isNullOrEmpty()) {
            purchases.forEach { handlePurchase(it) }
        } else {
            Log.e(TAG, "Purchase update failed: ${billingResult.debugMessage}")
            onPurchaseFailed("Purchase failed: ${billingResult.debugMessage}")
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
            billingClient.acknowledgePurchase(params) { result ->
                Log.i(TAG, "Acknowledge result: ${result.responseCode}")
                if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                    onPurchaseFailed("Acknowledge failed: ${result.debugMessage}")
                }
            }
        } else {
            Log.d(TAG, "Purchase already acknowledged")
        }
    }

    private fun consumePurchase(purchase: Purchase) {
        val params = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient.consumeAsync(params) { result, _ ->
            Log.i(TAG, "Consume result: ${result.responseCode}")
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                onPurchaseFailed("Consume failed: ${result.debugMessage}")
            }
        }
    }

    private fun verifyPurchaseOnServer(purchaseToken: String, callback: (Boolean) -> Unit) {
        Log.i(TAG, "Verifying token on server: $purchaseToken")
        callback(true)
    }

    fun disconnect() {
        if (::billingClient.isInitialized) {
            billingClient.endConnection()
            Log.i(TAG, "BillingClient disconnected")
        }
    }
}