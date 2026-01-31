package com.zaheer.emitrapalert.domain

data class FinancialAnalysis(
    val dtiRatio: Double,
    val stressScore: Int,
    val riskLevel: RiskLevel,
    val suggestedSafeEmiLimit: Double,
    val suggestedMinSavings: Double,
    val suggestions: List<String>
)

enum class RiskLevel {
    SAFE,
    MODERATE,
    HIGH_RISK,
    CRITICAL
}

object StressCalculator {
    
    /**
     * Calculate Debt-to-Income Ratio
     * DTI = (Total EMIs / Monthly Income) * 100
     */
    fun calculateDTI(totalEmis: Double, monthlyIncome: Double): Double {
        if (monthlyIncome <= 0) return 100.0
        return (totalEmis / monthlyIncome) * 100.0
    }
    
    /**
     * Calculate Financial Stress Score (0-100)
     * Based on DTI, leftover savings, emergency buffer logic
     */
    fun calculateStressScore(
        monthlyIncome: Double,
        fixedExpenses: Double,
        totalEmis: Double,
        desiredSavings: Double
    ): Int {
        val dti = calculateDTI(totalEmis, monthlyIncome)
        val leftover = monthlyIncome - fixedExpenses - totalEmis
        val savingsDeficit = desiredSavings - leftover
        
        // Base stress from DTI
        var stress = when {
            dti < 30 -> 10
            dti < 40 -> 30
            dti < 50 -> 50
            dti < 60 -> 70
            else -> 90
        }
        
        // Add stress if savings target not met
        if (savingsDeficit > 0) {
            val savingsStress = ((savingsDeficit / monthlyIncome) * 100).toInt()
            stress += savingsStress
        }
        
        // Add stress if leftover is negative (overdraft)
        if (leftover < 0) {
            stress += 20
        }
        
        return stress.coerceIn(0, 100)
    }
    
    /**
     * Determine risk level based on DTI and stress score
     */
    fun determineRiskLevel(dti: Double, stressScore: Int): RiskLevel {
        return when {
            dti < 30 && stressScore < 30 -> RiskLevel.SAFE
            dti < 40 && stressScore < 50 -> RiskLevel.MODERATE
            dti < 50 && stressScore < 70 -> RiskLevel.HIGH_RISK
            else -> RiskLevel.CRITICAL
        }
    }
    
    /**
     * Calculate suggested safe EMI limit (30% of income is safe)
     */
    fun calculateSafeEmiLimit(monthlyIncome: Double): Double {
        return monthlyIncome * 0.30
    }
    
    /**
     * Calculate suggested minimum savings (20% of income)
     */
    fun calculateSuggestedMinSavings(monthlyIncome: Double): Double {
        return monthlyIncome * 0.20
    }
    
    /**
     * Generate financial improvement suggestions
     */
    fun generateSuggestions(
        monthlyIncome: Double,
        fixedExpenses: Double,
        totalEmis: Double,
        desiredSavings: Double,
        riskLevel: RiskLevel
    ): List<String> {
        val suggestions = mutableListOf<String>()
        val dti = calculateDTI(totalEmis, monthlyIncome)
        val leftover = monthlyIncome - fixedExpenses - totalEmis
        
        when (riskLevel) {
            RiskLevel.SAFE -> {
                suggestions.add("✅ You're in a safe financial zone!")
                suggestions.add("Consider building an emergency fund equal to 6 months of expenses.")
                if (leftover > desiredSavings) {
                    suggestions.add("You have surplus funds. Consider investing for long-term goals.")
                }
            }
            RiskLevel.MODERATE -> {
                suggestions.add("⚠️ Your EMI burden is moderate. Be cautious before taking new loans.")
                if (dti > 35) {
                    suggestions.add("Try to keep DTI below 35% by increasing income or reducing EMIs.")
                }
                suggestions.add("Build an emergency fund of at least 3-4 months expenses.")
            }
            RiskLevel.HIGH_RISK -> {
                suggestions.add("🚨 High Risk! Your EMI burden is too high.")
                suggestions.add("Avoid taking new loans. Focus on paying off high-interest debts first.")
                if (totalEmis > calculateSafeEmiLimit(monthlyIncome)) {
                    val excess = totalEmis - calculateSafeEmiLimit(monthlyIncome)
                    suggestions.add("Reduce EMIs by ₹%.0f to reach safe zone.".format(excess))
                }
                suggestions.add("Look for ways to increase income through side hustles or skill upgrades.")
            }
            RiskLevel.CRITICAL -> {
                suggestions.add("🔴 CRITICAL! You're in a debt trap. Immediate action needed!")
                suggestions.add("Stop all new borrowing immediately.")
                suggestions.add("Consider debt restructuring or consolidation.")
                suggestions.add("Seek help from a financial advisor.")
                if (leftover < 0) {
                    suggestions.add("Your expenses exceed income. Cut non-essential spending urgently.")
                }
            }
        }
        
        // Generic suggestions
        if (fixedExpenses > monthlyIncome * 0.5) {
            suggestions.add("Your fixed expenses are high (>50% of income). Review and reduce them.")
        }
        
        if (leftover < desiredSavings && leftover > 0) {
            suggestions.add("You're not meeting your savings goal. Reduce expenses or increase income.")
        }
        
        return suggestions
    }
    
    /**
     * Perform complete financial analysis
     */
    fun analyzeFinances(
        monthlyIncome: Double,
        fixedExpenses: Double,
        totalEmis: Double,
        desiredSavings: Double
    ): FinancialAnalysis {
        val dti = calculateDTI(totalEmis, monthlyIncome)
        val stressScore = calculateStressScore(monthlyIncome, fixedExpenses, totalEmis, desiredSavings)
        val riskLevel = determineRiskLevel(dti, stressScore)
        val safeEmiLimit = calculateSafeEmiLimit(monthlyIncome)
        val suggestedMinSavings = calculateSuggestedMinSavings(monthlyIncome)
        val suggestions = generateSuggestions(monthlyIncome, fixedExpenses, totalEmis, desiredSavings, riskLevel)
        
        return FinancialAnalysis(
            dtiRatio = dti,
            stressScore = stressScore,
            riskLevel = riskLevel,
            suggestedSafeEmiLimit = safeEmiLimit,
            suggestedMinSavings = suggestedMinSavings,
            suggestions = suggestions
        )
    }
}
