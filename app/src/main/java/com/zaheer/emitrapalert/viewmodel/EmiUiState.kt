package com.zaheer.emitrapalert.viewmodel

import com.zaheer.emitrapalert.domain.FinancialAnalysis
import com.zaheer.emitrapalert.domain.WhatIfScenario

data class EmiUiState(
    // Input fields
    val monthlyIncome: String = "",
    val fixedExpenses: String = "",
    val totalEmis: String = "",
    val desiredSavings: String = "",
    
    // Analysis result
    val analysis: FinancialAnalysis? = null,
    
    // Pro features
    val isPro: Boolean = false,
    val isUnlocking: Boolean = false,
    val unlockError: String? = null,
    
    // What-if scenarios (Pro feature)
    val incomeScenario: WhatIfScenario? = null,
    val emiScenario: WhatIfScenario? = null,
    val expenseScenario: WhatIfScenario? = null,
    
    // PDF export status
    val exportingPdf: Boolean = false,
    val pdfExportSuccess: Boolean? = null
)
