package com.zaheer.emitrapalert.billing

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BillingManager(private val context: Context) : PurchasesUpdatedListener {
    
    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()
    
    private val prefs: SharedPreferences = context.getSharedPreferences("emi_prefs", Context.MODE_PRIVATE)
    
    private val _proStatusFlow = MutableStateFlow(isProUnlocked())
    val proStatusFlow: StateFlow<Boolean> = _proStatusFlow.asStateFlow()
    
    private var currentActivity: Activity? = null
    
    companion object {
        private const val PRO_PRODUCT_ID = "emitrap_pro_unlock"
        private const val PREF_IS_PRO = "is_pro"
    }
    
    init {
        startConnection()
    }
    
    private fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Query purchases on startup
                    queryPurchases()
                }
            }
            
            override fun onBillingServiceDisconnected() {
                // Try to restart connection
                startConnection()
            }
        })
    }
    
    fun launchBillingFlow() {
        if (currentActivity == null) {
            currentActivity = context as? Activity
        }
        
        val activity = currentActivity ?: return
        
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRO_PRODUCT_ID)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        
        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                val productDetails = productDetailsList[0]
                
                val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
                
                val flowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()
                
                billingClient.launchBillingFlow(activity, flowParams)
            }
        }
    }
    
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }
    
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.products.contains(PRO_PRODUCT_ID) && 
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        unlockProFeatures()
                    }
                }
            } else {
                unlockProFeatures()
            }
        }
    }
    
    private fun unlockProFeatures() {
        prefs.edit().putBoolean(PREF_IS_PRO, true).apply()
        _proStatusFlow.value = true
    }
    
    private fun isProUnlocked(): Boolean {
        return prefs.getBoolean(PREF_IS_PRO, false)
    }
    
    fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        
        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in purchases) {
                    if (purchase.products.contains(PRO_PRODUCT_ID) && 
                        purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        unlockProFeatures()
                        return@queryPurchasesAsync
                    }
                }
            }
        }
    }
    
    fun restorePurchases() {
        queryPurchases()
    }
    
    fun endConnection() {
        billingClient.endConnection()
    }
}
