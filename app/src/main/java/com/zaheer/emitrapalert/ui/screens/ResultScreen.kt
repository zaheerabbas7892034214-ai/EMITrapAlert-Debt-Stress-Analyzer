package com.zaheer.emitrapalert.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zaheer.emitrapalert.domain.RiskLevel
import com.zaheer.emitrapalert.ui.theme.*
import com.zaheer.emitrapalert.viewmodel.EmiUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    uiState: EmiUiState,
    onBack: () -> Unit,
    onSimulator: () -> Unit,
    onUnlockPro: () -> Unit,
    onRestorePurchases: () -> Unit
) {
    val analysis = uiState.analysis ?: return
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analysis Results") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Risk Level Card
            val riskColor = when (analysis.riskLevel) {
                RiskLevel.SAFE -> SafeGreen
                RiskLevel.MODERATE -> ModerateOrange
                RiskLevel.HIGH_RISK -> HighRiskRed
                RiskLevel.CRITICAL -> CriticalDarkRed
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = riskColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = analysis.riskLevel.name.replace("_", " "),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            // Key Metrics
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("📊 Key Metrics", style = MaterialTheme.typography.titleMedium)
                    
                    MetricRow("Debt-to-Income Ratio", "${String.format("%.2f", analysis.dtiRatio)}%")
                    MetricRow("Financial Stress Score", "${analysis.stressScore}/100")
                    MetricRow("Safe EMI Limit", "₹${String.format("%.2f", analysis.suggestedSafeEmiLimit)}")
                    MetricRow("Suggested Min Savings", "₹${String.format("%.2f", analysis.suggestedMinSavings)}")
                }
            }
            
            // Recommendations
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("💡 Recommendations", style = MaterialTheme.typography.titleMedium)
                    
                    analysis.suggestions.forEach { suggestion ->
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
            
            // Pro Features Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isPro) ProGold.copy(alpha = 0.2f) 
                                     else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "🔒 Pro Features",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (uiState.isPro) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = ProGold,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    "PRO",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                    
                    Text("• What-if Simulator", style = MaterialTheme.typography.bodyMedium)
                    Text("• Debt-free Timeline Projection", style = MaterialTheme.typography.bodyMedium)
                    Text("• PDF Export Report", style = MaterialTheme.typography.bodyMedium)
                    
                    if (uiState.isPro) {
                        Button(
                            onClick = onSimulator,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Open Simulator")
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onUnlockPro,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ProGold,
                                    contentColor = Color.Black
                                ),
                                enabled = !uiState.isUnlocking
                            ) {
                                Text(if (uiState.isUnlocking) "Processing..." else "Unlock Pro (One-Time)")
                            }
                            
                            TextButton(
                                onClick = onRestorePurchases,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isUnlocking
                            ) {
                                Text("Restore Purchases")
                            }
                        }
                    }
                    
                    uiState.unlockError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
