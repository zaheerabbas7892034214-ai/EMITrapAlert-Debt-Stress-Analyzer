package com.zaheer.emitrapalert.domain

data class DebtProjection(
    val monthsToDebtFree: Int,
    val totalInterestPaid: Double,
    val monthlyBreakdown: List<MonthlySnapshot>
)

data class MonthlySnapshot(
    val month: Int,
    val principalPaid: Double,
    val interestPaid: Double,
    val remainingBalance: Double
)

data class WhatIfScenario(
    val scenarioName: String,
    val newDti: Double,
    val newStressScore: Int,
    val newRiskLevel: RiskLevel,
    val improvement: String
)

object ProjectionEngine {
    
    /**
     * Project debt-free timeline
     * Simplified projection assuming fixed monthly EMI
     */
    fun projectDebtFreeTimeline(
        totalOutstandingDebt: Double,
        monthlyEmiAmount: Double,
        averageInterestRate: Double = 12.0
    ): DebtProjection {
        if (monthlyEmiAmount <= 0 || totalOutstandingDebt <= 0) {
            return DebtProjection(0, 0.0, emptyList())
        }
        
        val monthlyRate = averageInterestRate / 12 / 100
        var remainingBalance = totalOutstandingDebt
        val breakdown = mutableListOf<MonthlySnapshot>()
        var month = 1
        var totalInterest = 0.0
        
        while (remainingBalance > 0 && month <= 360) { // Max 30 years
            val interestForMonth = remainingBalance * monthlyRate
            val principalForMonth = (monthlyEmiAmount - interestForMonth).coerceAtLeast(0.0)
            
            if (principalForMonth <= 0) {
                // EMI is too small to cover interest, debt won't be paid off
                break
            }
            
            remainingBalance -= principalForMonth
            if (remainingBalance < 0) remainingBalance = 0.0
            
            totalInterest += interestForMonth
            
            breakdown.add(
                MonthlySnapshot(
                    month = month,
                    principalPaid = principalForMonth,
                    interestPaid = interestForMonth,
                    remainingBalance = remainingBalance
                )
            )
            
            month++
        }
        
        return DebtProjection(
            monthsToDebtFree = month - 1,
            totalInterestPaid = totalInterest,
            monthlyBreakdown = breakdown
        )
    }
    
    /**
     * What-if scenario: Increase income
     */
    fun simulateIncomeIncrease(
        currentIncome: Double,
        fixedExpenses: Double,
        totalEmis: Double,
        desiredSavings: Double,
        increasePercentage: Double
    ): WhatIfScenario {
        val newIncome = currentIncome * (1 + increasePercentage / 100)
        val analysis = StressCalculator.analyzeFinances(newIncome, fixedExpenses, totalEmis, desiredSavings)
        val oldDti = StressCalculator.calculateDTI(totalEmis, currentIncome)
        val improvement = "DTI reduced from %.1f%% to %.1f%%".format(oldDti, analysis.dtiRatio)
        
        return WhatIfScenario(
            scenarioName = "Income increased by %.0f%%".format(increasePercentage),
            newDti = analysis.dtiRatio,
            newStressScore = analysis.stressScore,
            newRiskLevel = analysis.riskLevel,
            improvement = improvement
        )
    }
    
    /**
     * What-if scenario: Reduce EMI
     */
    fun simulateEmiReduction(
        monthlyIncome: Double,
        fixedExpenses: Double,
        currentEmis: Double,
        desiredSavings: Double,
        reductionAmount: Double
    ): WhatIfScenario {
        val newEmis = (currentEmis - reductionAmount).coerceAtLeast(0.0)
        val analysis = StressCalculator.analyzeFinances(monthlyIncome, fixedExpenses, newEmis, desiredSavings)
        val oldDti = StressCalculator.calculateDTI(currentEmis, monthlyIncome)
        val improvement = "DTI reduced from %.1f%% to %.1f%%".format(oldDti, analysis.dtiRatio)
        
        return WhatIfScenario(
            scenarioName = "EMI reduced by ₹%.0f".format(reductionAmount),
            newDti = analysis.dtiRatio,
            newStressScore = analysis.stressScore,
            newRiskLevel = analysis.riskLevel,
            improvement = improvement
        )
    }
    
    /**
     * What-if scenario: Reduce expenses
     */
    fun simulateExpenseReduction(
        monthlyIncome: Double,
        currentExpenses: Double,
        totalEmis: Double,
        desiredSavings: Double,
        reductionAmount: Double
    ): WhatIfScenario {
        val newExpenses = (currentExpenses - reductionAmount).coerceAtLeast(0.0)
        val analysis = StressCalculator.analyzeFinances(monthlyIncome, newExpenses, totalEmis, desiredSavings)
        val oldStress = StressCalculator.calculateStressScore(monthlyIncome, currentExpenses, totalEmis, desiredSavings)
        val improvement = "Stress score reduced from %d to %d".format(oldStress, analysis.stressScore)
        
        return WhatIfScenario(
            scenarioName = "Expenses reduced by ₹%.0f".format(reductionAmount),
            newDti = analysis.dtiRatio,
            newStressScore = analysis.stressScore,
            newRiskLevel = analysis.riskLevel,
            improvement = improvement
        )
    }
}
