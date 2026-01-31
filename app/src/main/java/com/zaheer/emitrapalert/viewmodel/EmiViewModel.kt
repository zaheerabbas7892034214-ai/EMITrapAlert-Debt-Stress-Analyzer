package com.zaheer.emitrapalert.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zaheer.emitrapalert.billing.BillingManager
import com.zaheer.emitrapalert.domain.ProjectionEngine
import com.zaheer.emitrapalert.domain.StressCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmiViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _uiState = MutableStateFlow(EmiUiState())
    val uiState: StateFlow<EmiUiState> = _uiState.asStateFlow()
    
    private val billingManager = BillingManager(application)
    
    init {
        // Load pro status from SharedPreferences
        viewModelScope.launch {
            billingManager.proStatusFlow.collect { isPro ->
                _uiState.update { it.copy(isPro = isPro) }
            }
        }
    }
    
    fun updateIncome(income: String) {
        _uiState.update { it.copy(monthlyIncome = income) }
    }
    
    fun updateFixedExpenses(expenses: String) {
        _uiState.update { it.copy(fixedExpenses = expenses) }
    }
    
    fun updateTotalEmis(emis: String) {
        _uiState.update { it.copy(totalEmis = emis) }
    }
    
    fun updateDesiredSavings(savings: String) {
        _uiState.update { it.copy(desiredSavings = savings) }
    }
    
    fun calculateAnalysis() {
        val state = _uiState.value
        
        val income = state.monthlyIncome.toDoubleOrNull() ?: 0.0
        val expenses = state.fixedExpenses.toDoubleOrNull() ?: 0.0
        val emis = state.totalEmis.toDoubleOrNull() ?: 0.0
        val savings = state.desiredSavings.toDoubleOrNull() ?: 0.0
        
        if (income > 0) {
            val analysis = StressCalculator.analyzeFinances(income, expenses, emis, savings)
            _uiState.update { it.copy(analysis = analysis) }
        }
    }
    
    fun unlockPro() {
        viewModelScope.launch {
            _uiState.update { it.copy(isUnlocking = true, unlockError = null) }
            try {
                billingManager.launchBillingFlow()
                // Status will be updated via billingManager.proStatusFlow
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isUnlocking = false, 
                        unlockError = "Failed to start purchase: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun restorePurchases() {
        viewModelScope.launch {
            _uiState.update { it.copy(isUnlocking = true, unlockError = null) }
            try {
                billingManager.restorePurchases()
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isUnlocking = false, 
                        unlockError = "Failed to restore: ${e.message}"
                    )
                }
            } finally {
                _uiState.update { it.copy(isUnlocking = false) }
            }
        }
    }
    
    fun generateWhatIfScenarios() {
        if (!_uiState.value.isPro) return
        
        val state = _uiState.value
        val income = state.monthlyIncome.toDoubleOrNull() ?: 0.0
        val expenses = state.fixedExpenses.toDoubleOrNull() ?: 0.0
        val emis = state.totalEmis.toDoubleOrNull() ?: 0.0
        val savings = state.desiredSavings.toDoubleOrNull() ?: 0.0
        
        if (income > 0) {
            val incomeScenario = ProjectionEngine.simulateIncomeIncrease(
                income, expenses, emis, savings, 20.0
            )
            val emiScenario = ProjectionEngine.simulateEmiReduction(
                income, expenses, emis, savings, emis * 0.2
            )
            val expenseScenario = ProjectionEngine.simulateExpenseReduction(
                income, expenses, emis, savings, expenses * 0.15
            )
            
            _uiState.update {
                it.copy(
                    incomeScenario = incomeScenario,
                    emiScenario = emiScenario,
                    expenseScenario = expenseScenario
                )
            }
        }
    }
    
    fun exportToPdf() {
        if (!_uiState.value.isPro) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(exportingPdf = true, pdfExportSuccess = null) }
            try {
                // PDF export logic would go here
                // For now, we'll just simulate success
                _uiState.update { 
                    it.copy(
                        exportingPdf = false, 
                        pdfExportSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        exportingPdf = false, 
                        pdfExportSuccess = false
                    )
                }
            }
        }
    }
    
    fun resetPdfExportStatus() {
        _uiState.update { it.copy(pdfExportSuccess = null) }
    }
    
    override fun onCleared() {
        super.onCleared()
        billingManager.endConnection()
    }
}
