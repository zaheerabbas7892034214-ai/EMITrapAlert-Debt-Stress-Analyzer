package com.zaheer.emitrapalert.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zaheer.emitrapalert.domain.RiskLevel
import com.zaheer.emitrapalert.domain.WhatIfScenario
import com.zaheer.emitrapalert.ui.theme.ProGold
import com.zaheer.emitrapalert.viewmodel.EmiUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatorScreen(
    uiState: EmiUiState,
    onBack: () -> Unit,
    onGenerateScenarios: () -> Unit,
    onUnlockPro: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("What-If Simulator") },
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
        if (!uiState.isPro) {
            // Pro locked screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Locked",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "Pro Feature Locked",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "Unlock Pro to access:",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("✨ Income increase scenarios")
                    Text("✨ EMI reduction scenarios")
                    Text("✨ Expense reduction scenarios")
                    Text("✨ Debt-free projection timeline")
                    Text("✨ PDF export reports")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onUnlockPro,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProGold,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Unlock Pro (One-Time Payment)")
                }
            }
        } else {
            // Pro unlocked - show simulator
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    color = ProGold,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        "PRO UNLOCKED",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                Text(
                    "What-If Scenarios",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Text(
                    "Explore how different changes would affect your financial health",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Button(
                    onClick = onGenerateScenarios,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Generate Scenarios")
                }
                
                // Income Increase Scenario
                uiState.incomeScenario?.let { scenario ->
                    ScenarioCard(scenario = scenario, title = "📈 Income Increase Scenario")
                }
                
                // EMI Reduction Scenario
                uiState.emiScenario?.let { scenario ->
                    ScenarioCard(scenario = scenario, title = "💰 EMI Reduction Scenario")
                }
                
                // Expense Reduction Scenario
                uiState.expenseScenario?.let { scenario ->
                    ScenarioCard(scenario = scenario, title = "🎯 Expense Reduction Scenario")
                }
                
                if (uiState.incomeScenario == null && 
                    uiState.emiScenario == null && 
                    uiState.expenseScenario == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Click 'Generate Scenarios' to see how different changes would improve your financial situation",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScenarioCard(scenario: WhatIfScenario, title: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = scenario.scenarioName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Divider()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("New DTI:")
                Text(
                    "${String.format("%.2f", scenario.newDti)}%",
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("New Stress Score:")
                Text(
                    "${scenario.newStressScore}/100",
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("New Risk Level:")
                Text(
                    scenario.newRiskLevel.name.replace("_", " "),
                    fontWeight = FontWeight.Bold,
                    color = getRiskColor(scenario.newRiskLevel)
                )
            }
            
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = scenario.improvement,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun getRiskColor(riskLevel: RiskLevel): Color {
    return when (riskLevel) {
        RiskLevel.SAFE -> Color(0xFF4CAF50)
        RiskLevel.MODERATE -> Color(0xFFFF9800)
        RiskLevel.HIGH_RISK -> Color(0xFFF44336)
        RiskLevel.CRITICAL -> Color(0xFFD32F2F)
    }
}
