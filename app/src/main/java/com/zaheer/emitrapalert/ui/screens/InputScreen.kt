package com.zaheer.emitrapalert.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zaheer.emitrapalert.viewmodel.EmiUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    uiState: EmiUiState,
    onIncomeChange: (String) -> Unit,
    onExpensesChange: (String) -> Unit,
    onEmisChange: (String) -> Unit,
    onSavingsChange: (String) -> Unit,
    onCalculate: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EMI Trap Alert") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
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
            Text(
                text = "Enter Your Financial Details",
                style = MaterialTheme.typography.titleLarge
            )
            
            Text(
                text = "Help us analyze if you're trapped in risky EMI debt",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = uiState.monthlyIncome,
                onValueChange = onIncomeChange,
                label = { Text("Monthly Net Income (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = uiState.fixedExpenses,
                onValueChange = onExpensesChange,
                label = { Text("Fixed Monthly Expenses (₹)") },
                supportingText = { Text("Excluding EMIs") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = uiState.totalEmis,
                onValueChange = onEmisChange,
                label = { Text("Total EMIs per Month (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = uiState.desiredSavings,
                onValueChange = onSavingsChange,
                label = { Text("Minimum Desired Savings (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onCalculate,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.monthlyIncome.isNotEmpty()
            ) {
                Text("Analyze My Financial Health")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "💡 What We Calculate:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text("• Debt-to-Income Ratio (DTI)", style = MaterialTheme.typography.bodySmall)
                    Text("• Financial Stress Score (0-100)", style = MaterialTheme.typography.bodySmall)
                    Text("• Risk Level Assessment", style = MaterialTheme.typography.bodySmall)
                    Text("• Personalized Recommendations", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
