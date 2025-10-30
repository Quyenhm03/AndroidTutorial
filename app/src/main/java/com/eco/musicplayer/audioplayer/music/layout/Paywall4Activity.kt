package com.eco.musicplayer.audioplayer.music.layout

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.eco.musicplayer.audioplayer.music.R
import com.eco.musicplayer.audioplayer.music.databinding.ActivityPwOnboardingBinding
import com.eco.musicplayer.audioplayer.music.ggbilling.BillingManager
import com.eco.musicplayer.audioplayer.music.remoteconfig.model.PaywallConfig
import com.google.gson.Gson

class Paywall4Activity : BaseActivity() {

    private lateinit var binding: ActivityPwOnboardingBinding
    private lateinit var billingManager: BillingManager
    private var productDetailsSub: ProductDetails? = null
    private var productDetailsLifetime: ProductDetails? = null
    private var isEligible = true
    private var isProductsLoaded = false

    private var paywallConfig: PaywallConfig? = null
    private var subProductId: String = "free_123"
    private var lifetimeProductId: String = "test3"
    private var subOfferId: String? = "3days"
    private var lifetimeOfferId: String? = null

    companion object {
        private const val TAG = "BillingDebug"
        private const val PLAN_WEEKLY = "WEEKLY"
        private const val PLAN_LIFETIME = "LIFETIME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPwOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()

        intent.getStringExtra("paywall_config")?.let { configJson ->
            paywallConfig = Gson().fromJson(configJson, PaywallConfig::class.java)
            parsePaywallConfig(paywallConfig)
        }

        val extras = intent.extras
        val state = extras?.getInt("state", 0) ?: 0
        isEligible = state == 0
        Log.d(TAG, "Activity state: $state, isEligible: $isEligible")

        billingManager = BillingManager(
            context = this,
            onBillingReady = {
                Log.d(TAG, "Billing ready, fetching products...")
                fetchProductDetailsAndSetupUI()
            },
            onPurchaseSuccess = { purchase ->
                Log.i(TAG, "Purchase success: ${purchase.products[0]}, token: ${purchase.purchaseToken}")
                finish()
            },
            onPurchaseFailed = { error ->
                Log.e(TAG, "Purchase failed: $error")
            }
        )

        binding.btnClose.setOnClickListener { finish() }

        stateIsLoading()
    }

    private fun parsePaywallConfig(config: PaywallConfig?) {
        config?.products?.let { products ->
            val subProduct = products.getOrNull(0)
            subProduct?.let {
                subProductId = it.productId ?: subProductId
                subOfferId = it.offerId
            }

            val lifetimeProduct = products.getOrNull(1)
            lifetimeProduct?.let {
                lifetimeProductId = it.productId ?: lifetimeProductId
                lifetimeOfferId = it.offerId
            }

            Log.d(TAG, "Parsed config - Sub: $subProductId (offer: $subOfferId), Lifetime: $lifetimeProductId (offer: $lifetimeOfferId)")
        }
    }

    private fun fetchProductDetailsAndSetupUI() {
        Log.i(TAG, "Fetching product details for sub: $subProductId and lifetime: $lifetimeProductId")

        billingManager.queryProductDetails(subProductId, BillingClient.ProductType.SUBS) { details ->
            productDetailsSub = details

            billingManager.queryProductDetails(lifetimeProductId, BillingClient.ProductType.INAPP) { lifetimeDetails ->
                productDetailsLifetime = lifetimeDetails

                if (details != null && lifetimeDetails != null) {
                    Log.i(TAG, "Products fetched successfully:")
                    Log.i(TAG, "  Sub offers: ${details.subscriptionOfferDetails?.size}")
                    Log.i(TAG, "  Lifetime price: ${lifetimeDetails.oneTimePurchaseOfferDetails?.formattedPrice}")

                    isProductsLoaded = true

                    runOnUiThread {
                        if (isEligible) {
                            stateNormal()
                        } else {
                            stateNotEligible()
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to fetch product details - sub: ${details != null}, lifetime: ${lifetimeDetails != null}")
                    runOnUiThread {
                        showErrorState()
                    }
                }
            }
        }
    }

    private fun stateIsLoading() {
        Log.d(TAG, "State: Loading")
        binding.apply {
            progress.visibility = View.VISIBLE
            btnTry.isEnabled = false
            btnTry.text = ""
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5F5F5F"))
            txtFee.visibility = View.INVISIBLE
            swTrial.isEnabled = false
            swTrial.isChecked = true
            llFreeTrial.visibility = View.VISIBLE
        }
    }

    private fun stateNormal() {
        Log.d(TAG, "State: Normal - Eligible for trial")
        if (!isProductsLoaded) {
            Log.w(TAG, "Products not loaded yet, cannot switch to Normal state")
            return
        }

        binding.apply {
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0F1E47"))
            progress.visibility = View.GONE
            txtFee.visibility = View.VISIBLE
            btnTry.isEnabled = true
            swTrial.isEnabled = true
            swTrial.isChecked = true

            val initialPlan = getSelectedPlan()
            Log.d(TAG, "Initial plan in stateNormal: $initialPlan")
            updateUIForPlan(initialPlan)

            swTrial.setOnCheckedChangeListener { _, isChecked ->
                Log.d(TAG, "Switch changed: trial = $isChecked")
                updateUIForPlan(getSelectedPlan())
            }

            toggleGroup.setOnCheckedChangeListener { _, _ ->
                val plan = getSelectedPlan()
                Log.d(TAG, "Plan changed: $plan")
                updateUIForPlan(plan)
            }

            btnTry.setOnClickListener {
                val plan = getSelectedPlan()
                launchPurchase(plan, swTrial.isChecked)
            }
        }
    }

    private fun stateNotEligible() {
        Log.d(TAG, "State: Not Eligible - No trial")
        if (!isProductsLoaded) {
            Log.w(TAG, "Products not loaded yet, cannot switch to NotEligible state")
            return
        }

        binding.apply {
            btnTry.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0F1E47"))
            progress.visibility = View.GONE
            btnTry.isEnabled = true
            txtFee.visibility = View.VISIBLE

            val initialPlan = getSelectedPlan()
            Log.d(TAG, "Initial plan in stateNotEligible: $initialPlan")
            updateUIForPlan(initialPlan)

            toggleGroup.setOnCheckedChangeListener { _, _ ->
                val plan = getSelectedPlan()
                Log.d(TAG, "Not eligible: plan changed to $plan")
                updateUIForPlan(plan)
            }

            btnTry.setOnClickListener {
                launchPurchase(getSelectedPlan(), false)
            }
        }
    }

    private fun showErrorState() {
        Log.e(TAG, "State: Error - Failed to load products")
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
        Log.d(TAG, "UpdateUIForPlan: plan=$plan, isEligible=$isEligible")

        binding.apply {
            val isTrial = swTrial.isChecked && plan != PLAN_LIFETIME
            llFreeTrial.visibility = if (plan == PLAN_LIFETIME || !isEligible ) View.GONE else View.VISIBLE
            swTrial.isEnabled = plan != PLAN_LIFETIME

            when (plan) {
                PLAN_WEEKLY -> {
                    productDetailsSub?.let { details ->
                        val offerId = subOfferId
                        val price = getFormattedPrice(details, offerId, 1)

                        if (isTrial) {
                            var pricePhase0 = getFormattedPrice(details, offerId, 0)
                            if (pricePhase0 == "Miễn phí") {
                                pricePhase0 = "3 days free trial"
                            } else {
                                pricePhase0 = "pay $pricePhase0 within 3 days"
                            }

                            txtFee.text = getString(R.string.fee_trial, price, pricePhase0)
                        } else {
                            txtFee.text = getString(R.string.fee_not_trial, price)
                        }
                    } ?: run { txtFee.text = getString(R.string.error_loading_price) }
                    btnTry.text = if (isTrial && isEligible) getString(R.string.btn_try) else getString(R.string.btn_continue)
                }
                PLAN_LIFETIME -> {
                    productDetailsLifetime?.let { details ->
                        val price = details.oneTimePurchaseOfferDetails?.formattedPrice ?: "N/A"
                        txtFee.text = getString(R.string.fee_onetime_purchase, price)
                    } ?: run { txtFee.text = getString(R.string.error_loading_price) }
                    btnTry.text = getString(R.string.btn_continue)
                }
            }
            updateTrialText(isTrial && plan != PLAN_LIFETIME)
            Log.i(TAG, "UI updated for $plan, trial=$isTrial, fee=${txtFee.text}")
        }
    }

    private fun updateTrialText(isTrialOn: Boolean) {
        binding.txtFreeTrial.text = if (isTrialOn) {
            getString(R.string.free_trial_enabled)
        } else {
            getString(R.string.enable_free_trial)
        }
    }

    private fun getFormattedPrice(productDetails: ProductDetails, offerId: String?, phase: Int): String {
        val offers = productDetails.subscriptionOfferDetails ?: return "N/A"
        val selectedOffer = if (offerId != null) {
            offers.find { it.offerId == offerId } ?: offers.find { it.offerId == null }
        } else {
            offers.find { it.offerId == null }
        } ?: offers[0]

        Log.i(TAG, "Selected offer for Weekly:")
        Log.i(TAG, "  Offer ID: ${selectedOffer.offerId ?: "null"}")
        Log.i(TAG, "  Offer Tags: ${selectedOffer.offerTags}")
        Log.i(TAG, "  Base Plan ID: ${selectedOffer.basePlanId}")
        selectedOffer.pricingPhases.pricingPhaseList.forEachIndexed { index, phase ->
            Log.i(TAG, "    Pricing Phase $index:")
            Log.i(TAG, "      Price: ${phase.formattedPrice}")
            Log.i(TAG, "      Billing Period: ${phase.billingPeriod}")
            Log.i(TAG, "      Recurrence Mode: ${phase.recurrenceMode}")
            Log.i(TAG, "      Billing Cycle Count: ${phase.billingCycleCount}")
        }

        val pricingPhase = selectedOffer.pricingPhases.pricingPhaseList.get(phase)
        return pricingPhase.formattedPrice
    }

    private fun launchPurchase(plan: String, isTrial: Boolean) {
        when (plan) {
            PLAN_WEEKLY -> {
                productDetailsSub?.let { details ->
                    val offerId = if (isTrial) subOfferId else lifetimeOfferId
                    Log.i(TAG, "Launching purchase for $plan with offer: ${offerId ?: "null"}")
                    billingManager.launchPurchaseFlow(this, details, offerId ?: "")
                } ?: Log.e(TAG, "No sub product details")
            }
            PLAN_LIFETIME -> {
                productDetailsLifetime?.let { details ->
                    Log.i(TAG, "Launching purchase for lifetime: $lifetimeProductId")
                    billingManager.launchPurchaseFlow(this, details)
                } ?: Log.e(TAG, "No lifetime product details")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.disconnect()
        Log.d(TAG, "BillingManager disconnected")
    }
}