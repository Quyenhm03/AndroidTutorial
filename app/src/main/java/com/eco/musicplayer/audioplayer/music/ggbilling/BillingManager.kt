package com.eco.musicplayer.audioplayer.music.ggbilling

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
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
    private val onPurchaseSuccess: (Purchase) -> Unit,
    private val onPurchaseFailed: (String) -> Unit
) {
    private lateinit var billingClient: BillingClient
    private val scope = CoroutineScope(Dispatchers.Main)

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        handlePurchaseUpdate(billingResult, purchases)
    }

    init {
        initializeBillingClient()
    }

    private fun initializeBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    scope.launch {
                        queryExistingPurchases()
                    }
                } else {
                    onPurchaseFailed("Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                initializeBillingClient()
            }
        })
    }

    fun queryProductDetails(productId: String, productType: String, onResult: (ProductDetails?) -> Unit) {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(productType)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                onResult(productDetailsList[0])
            } else {
                onResult(null)
                onPurchaseFailed("Query product failed: ${billingResult.debugMessage}")
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        val offerToken = productDetails.subscriptionOfferDetails?.get(0)?.offerToken ?: ""
        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .apply { if (offerToken.isNotEmpty()) setOfferToken(offerToken) }
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            onPurchaseFailed("Launch flow failed: ${result.debugMessage}")
        }
    }

    private fun queryExistingPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.forEach { handlePurchase(it) }
            }
        }

        val subParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        billingClient.queryPurchasesAsync(subParams) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.forEach { handlePurchase(it) }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            verifyPurchaseOnServer(purchase.purchaseToken) { isValid ->
                if (isValid) {
                    scope.launch {
                        if (purchase.products[0].contains("subs")) {
                            acknowledgePurchase(purchase)
                        } else {
                            consumePurchase(purchase)
                        }
                        onPurchaseSuccess(purchase)
                    }
                } else {
                    onPurchaseFailed("Invalid purchase")
                }
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            onPurchaseFailed("Purchase pending")
        }
    }

    private fun handlePurchaseUpdate(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !purchases.isNullOrEmpty()) {
            purchases.forEach { handlePurchase(it) }
        } else {
            onPurchaseFailed("Purchase failed: ${billingResult.debugMessage}")
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(params) { billingResult ->
                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                    onPurchaseFailed("Acknowledge failed: ${billingResult.debugMessage}")
                }
            }
        }
    }

    private fun consumePurchase(purchase: Purchase) {
        val params = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.consumeAsync(params) { billingResult, _ ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                onPurchaseFailed("Consume failed: ${billingResult.debugMessage}")
            }
        }
    }

    private fun verifyPurchaseOnServer(purchaseToken: String, callback: (Boolean) -> Unit) {
        // TODO: Gửi purchaseToken đến server để xác minh
        // Ví dụ: Gọi Google Play Developer API hoặc server của bạn
        callback(true) // Giả lập xác minh thành công
    }

    fun disconnect() {
        billingClient.endConnection()
    }
}