package com.eco.musicplayer.audioplayer.music.layout

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.eco.billing.BillingManager
import com.eco.billing.asProductDetailsOffer
import com.eco.billing.buy
import com.eco.billing.model.ProductDetailsOffer
import com.eco.billing.model.ProductInfo
import com.eco.billing.queryAlls
import com.eco.billing.state.BillingPurchasesState
import com.eco.inappbilling.BillingQueryState
import com.eco.musicplayer.audioplayer.music.R
import com.eco.musicplayer.audioplayer.music.databinding.ActivityPwOnboardingBinding
import com.eco.musicplayer.audioplayer.music.remoteconfig.model.PaywallConfig
import com.google.gson.Gson

class Paywall4ActivityNewBilling : BaseActivity() {
    private lateinit var binding: ActivityPwOnboardingBinding
    private lateinit var billingManager: BillingManager

    private var isEligible = true
    private var paywallConfig: PaywallConfig? = null

    private var subProductId: String = "free_123"
    private var lifetimeProductId: String = "test3"
    private var subOfferId: String? = "3days"         // offer có trial
    private var lifetimeOfferId: String? = null

    companion object {
        private const val TAG = "Paywall4Activity"
        private const val PLAN_WEEKLY = "WEEKLY"
        private const val PLAN_LIFETIME = "LIFETIME"
        private const val BILLING_IDENTITY = "paywall4"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPwOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarIconsColor(false)
        hideSystemUI()

        // Parse config từ intent
        intent.getStringExtra("paywall_config")?.let { configJson ->
            paywallConfig = Gson().fromJson(configJson, PaywallConfig::class.java)
            parsePaywallConfig(paywallConfig)
        }

//        val state = intent.extras?.getInt("state", 0) ?: 0
//        isEligible = state == 0
        Log.d(TAG, ">>> Activity started - isEligible: $isEligible")

        billingManager = BillingManager(this)

        binding.btnClose.setOnClickListener { finish() }
        stateIsLoading()

        // Khởi động query sản phẩm
        loadProductsAndSetupUI()
    }

    private fun parsePaywallConfig(config: PaywallConfig?) {
        config?.products?.let { products ->
            products.getOrNull(0)?.let {
                subProductId = it.productId ?: subProductId
                subOfferId = it.offerId
            }
            products.getOrNull(1)?.let {
                lifetimeProductId = it.productId ?: lifetimeProductId
                lifetimeOfferId = it.offerId
            }
            Log.d(TAG, "Parsed config → Weekly: $subProductId (offer: $subOfferId), Lifetime: $lifetimeProductId")
        }
    }

    private fun loadProductsAndSetupUI() {
        val productInfos = listOf(
            ProductInfo(productType = BillingClient.ProductType.SUBS, productId = subProductId),
            ProductInfo(productType = BillingClient.ProductType.INAPP, productId = lifetimeProductId)
        )

        Log.i(TAG, "Start querying products: Weekly=$subProductId, Lifetime=$lifetimeProductId")

        billingManager.queryAlls(
            identity = BILLING_IDENTITY,
            cached = true,
            productInfos = productInfos
        ) { state ->
            when (state) {
                is BillingQueryState.ProductDetailsComplete -> {
                    val detailsList = state.products
                    if (detailsList.size == 2) {
                        val subDetails = detailsList.find { it.productId == subProductId }
                        val lifetimeDetails = detailsList.find { it.productId == lifetimeProductId }

                        isEligible = checkEligible(subDetails)

                        logProductDetails(subDetails, isSubscription = true)
                        logProductDetails(lifetimeDetails, isSubscription = false)

                        if (isEligible) {
                            stateNormal()
                        } else {
                            stateNotEligible()
                        }
                    } else {
                        Log.e(TAG, "Expected 2 products, got ${detailsList.size}")
                        showErrorState()
                    }
                }
                is BillingQueryState.PurchaseComplete -> {
                    Log.d(TAG, "Loaded ${state.purchases.size} active purchases (ignored in paywall)")
                }
                is BillingQueryState.Error -> {
                    Log.e(TAG, "Billing query failed", state.exception)
                    showErrorState()
                }
            }
        }
    }

    private fun checkEligible(details: ProductDetails?) : Boolean {
        if (details != null) {
            details.subscriptionOfferDetails?.forEach { offer ->
                if (offer.offerId == subOfferId) {
                    return true
                }
            }
        }

        return false
    }

    private fun logProductDetails(details: ProductDetails?, isSubscription: Boolean) {
        if (details == null) {
            Log.e(TAG, "${if (isSubscription) "Weekly" else "Lifetime"} ProductDetails = NULL")
            return
        }

        if (isSubscription) {
            Log.i(TAG, "=== WEEKLY SUBSCRIPTION (${details.productId}) ===")

            val allOffers = details.subscriptionOfferDetails ?: emptyList()

            allOffers.forEach { offer ->
                val offerId = offer.offerId ?: "null"
                val isSelected = offerId == (subOfferId ?: "")
                val marker = if (isSelected) "SELECTED" else "       "

                Log.i(TAG, "$marker Offer ID: $offerId | Token: ${offer.offerToken}")

                offer.pricingPhases.pricingPhaseList.forEachIndexed { idx, phase ->
                    Log.i(TAG, "  $marker Phase $idx: ${phase.formattedPrice} " +
                            "| Period: ${phase.billingPeriod} " +
                            "| Micros: ${phase.priceAmountMicros}")
                }
            }

            // SELECTED OFFER SUMMARY
            if (subOfferId != null && allOffers.any { it.offerId == subOfferId }) {
                val selectedOffer = details.asProductDetailsOffer(subOfferId!!)
                Log.i(TAG, "SELECTED OFFER SUMMARY")
                Log.i(TAG, "  Type: ${selectedOffer.typeOffer}")
                Log.i(TAG, "  Trial days: ${selectedOffer.dayFreeTrial}")
                Log.i(TAG, "  Normal price: ${selectedOffer.formattedPrice}")
                Log.i(TAG, "  Offer price: ${selectedOffer.formattedPriceOffer}")
                Log.i(TAG, "  OfferToken: ${selectedOffer.offerToken}")
            } else {
                Log.w(TAG, "subOfferId='$subOfferId' NOT FOUND in available offers!")
                Log.w(TAG, "  Available offer IDs: ${allOffers.map { it.offerId }}")
            }

        } else {
            Log.i(TAG, "=== LIFETIME IN-APP (${details.productId}) ===")
            val price = details.oneTimePurchaseOfferDetails?.formattedPrice ?: "N/A"
            Log.i(TAG, "  Price: $price")
        }
    }

    private fun stateIsLoading() {
        Log.d(TAG, "UI State → Loading")
        binding.apply {
            progress.visibility = View.VISIBLE
            btnTry.isEnabled = false
            btnTry.text = ""
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5F5F5F"))
            txtFee.visibility = View.INVISIBLE
            swTrial.isEnabled = false
            llFreeTrial.visibility = View.VISIBLE
        }
    }

    private fun stateNormal() {
        Log.d(TAG, "UI State → Normal (Eligible for trial)")
        binding.apply {
            progress.visibility = View.GONE
            btnTry.isEnabled = true
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0F1E47"))
            txtFee.visibility = View.VISIBLE
            swTrial.isEnabled = true
            swTrial.isChecked = true

            updateUIForPlan(getSelectedPlan())

            swTrial.setOnCheckedChangeListener { _, isChecked ->
                Log.d(TAG, "Trial switch → $isChecked")
                updateUIForPlan(getSelectedPlan())
            }

            toggleGroup.setOnCheckedChangeListener { _, _ ->
                updateUIForPlan(getSelectedPlan())
            }

            btnTry.setOnClickListener {
                launchPurchase(getSelectedPlan(), swTrial.isChecked)
            }
        }
    }

    private fun stateNotEligible() {
        Log.d(TAG, "UI State → Not Eligible (No trial)")
        binding.apply {
            progress.visibility = View.GONE
            btnTry.isEnabled = true
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0F1E47"))
            txtFee.visibility = View.VISIBLE
            llFreeTrial.visibility = View.GONE
            swTrial.isEnabled = false

            updateUIForPlan(getSelectedPlan())

            toggleGroup.setOnCheckedChangeListener { _, _ ->
                updateUIForPlan(getSelectedPlan())
            }

            btnTry.setOnClickListener {
                launchPurchase(getSelectedPlan(), false)
            }
        }
    }

    private fun showErrorState() {
        Log.e(TAG, "UI State → Error")
        binding.apply {
            progress.visibility = View.GONE
            btnTry.isEnabled = false
            btnTry.text = getString(R.string.btn_continue)
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5F5F5F"))
            txtFee.visibility = View.VISIBLE
            txtFee.text = getString(R.string.error_loading_price)
            llFreeTrial.visibility = View.GONE
        }
    }

    private fun getSelectedPlan(): String {
        return when (binding.toggleGroup.checkedRadioButtonId) {
            R.id.btnWeekly -> PLAN_WEEKLY
            R.id.btnLifetime -> PLAN_LIFETIME
            else -> PLAN_WEEKLY
        }
    }

    private fun updateUIForPlan(plan: String) {
        val subDetails = billingManager.detailsMutableMap[subProductId]
        val lifetimeDetails = billingManager.detailsMutableMap[lifetimeProductId]

        val isTrial = binding.swTrial.isChecked && plan != PLAN_LIFETIME && isEligible

        binding.apply {
            llFreeTrial.visibility = if (plan == PLAN_LIFETIME || !isEligible) View.GONE else View.VISIBLE
            swTrial.isEnabled = plan != PLAN_LIFETIME

            when (plan) {
                PLAN_WEEKLY -> {
                    if (subDetails == null) {
                        txtFee.text = getString(R.string.error_loading_price)
                        return
                    }

                    val offer = subDetails.asProductDetailsOffer(subOfferId ?: "")

                    if (isTrial && offer.typeOffer == ProductDetailsOffer.TypeOffer.FREE_TRIAL) {
                        txtFee.text = getString(R.string.fee_trial, offer.formattedPrice, "${offer.dayFreeTrial} days free trial")
                        btnTry.text = getString(R.string.btn_try)
                    } else if (isTrial && offer.formattedPriceOffer.isNotEmpty()) {
                        txtFee.text = getString(R.string.fee_trial, offer.formattedPrice, offer.formattedPriceOffer)
                        btnTry.text = getString(R.string.btn_try)
                    } else {
                        txtFee.text = getString(R.string.fee_not_trial, offer.formattedPrice)
                        btnTry.text = getString(R.string.btn_continue)
                    }
                }
                PLAN_LIFETIME -> {
                    val price = lifetimeDetails?.oneTimePurchaseOfferDetails?.formattedPrice ?: "N/A"
                    txtFee.text = getString(R.string.fee_onetime_purchase, price)
                    btnTry.text = getString(R.string.btn_continue)
                }
            }
            updateTrialText(isTrial && plan != PLAN_LIFETIME)
        }
    }

    private fun updateTrialText(showEnabled: Boolean) {
        binding.txtFreeTrial.text = if (showEnabled) {
            getString(R.string.free_trial_enabled)
        } else {
            getString(R.string.enable_free_trial)
        }
    }

    private fun launchPurchase(plan: String, withTrial: Boolean) {
        when (plan) {
            PLAN_WEEKLY -> {
                val details = billingManager.detailsMutableMap[subProductId] ?: run {
                    Log.e(TAG, "Weekly ProductDetails not found!")
                    return
                }

                val offerToken = if (withTrial && isEligible) {
                    // Lấy đúng offer có trial theo subOfferId
                    details.asProductDetailsOffer(subOfferId ?: "").offerToken
                } else {
                    // Dùng base plan (không trial)
                    details.subscriptionOfferDetails?.find { it.offerId == null || it.pricingPhases.pricingPhaseList.size == 1 }
                        ?.offerToken ?: details.subscriptionOfferDetails?.firstOrNull()?.offerToken
                }

                Log.i(TAG, "Launch Weekly purchase → withTrial=$withTrial, offerToken=$offerToken")

                billingManager.buy(
                    activity = this,
                    productDetails = details,
                    offerToken = offerToken
                ) { purchaseState ->
                    when (purchaseState) {
                        is BillingPurchasesState.PurchaseAcknowledged -> {
                            Log.i(TAG, "Purchase SUCCESS: ${purchaseState.productId}")
                            finish()
                        }
                        is BillingPurchasesState.UserCancelPurchase -> Log.w(TAG, "User cancelled")
                        is BillingPurchasesState.Error -> Log.e(TAG, "Purchase error", purchaseState.exception)
                        else -> {}
                    }
                }
            }

            PLAN_LIFETIME -> {
                val details = billingManager.detailsMutableMap[lifetimeProductId] ?: run {
                    Log.e(TAG, "Lifetime ProductDetails not found!")
                    return
                }

                Log.i(TAG, "Launch Lifetime purchase")

                billingManager.buy(
                    activity = this,
                    productDetails = details,
                    offerToken = null
                ) { purchaseState ->
                    when (purchaseState) {
                        is BillingPurchasesState.PurchaseAcknowledged -> {
                            Log.i(TAG, "Lifetime purchase SUCCESS")
                            finish()
                        }
                        is BillingPurchasesState.UserCancelPurchase -> Log.w(TAG, "User cancelled")
                        is BillingPurchasesState.Error -> Log.e(TAG, "Purchase error", purchaseState.exception)
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.detachListeners(BILLING_IDENTITY)
        Log.d(TAG, "BillingManager listeners detached")
    }

}