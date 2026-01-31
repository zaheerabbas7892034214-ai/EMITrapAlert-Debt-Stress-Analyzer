package com.zaheer.emitrapalert.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zaheer.emitrapalert.ui.screens.InputScreen
import com.zaheer.emitrapalert.ui.screens.ResultScreen
import com.zaheer.emitrapalert.ui.screens.SimulatorScreen
import com.zaheer.emitrapalert.viewmodel.EmiViewModel

sealed class Screen(val route: String) {
    object Input : Screen("input")
    object Result : Screen("result")
    object Simulator : Screen("simulator")
}

@Composable
fun AppNav(viewModel: EmiViewModel = viewModel()) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    
    NavHost(navController = navController, startDestination = Screen.Input.route) {
        composable(Screen.Input.route) {
            InputScreen(
                uiState = uiState,
                onIncomeChange = viewModel::updateIncome,
                onExpensesChange = viewModel::updateFixedExpenses,
                onEmisChange = viewModel::updateTotalEmis,
                onSavingsChange = viewModel::updateDesiredSavings,
                onCalculate = {
                    viewModel.calculateAnalysis()
                    navController.navigate(Screen.Result.route)
                }
            )
        }
        
        composable(Screen.Result.route) {
            ResultScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onSimulator = { navController.navigate(Screen.Simulator.route) },
                onUnlockPro = viewModel::unlockPro,
                onRestorePurchases = viewModel::restorePurchases
            )
        }
        
        composable(Screen.Simulator.route) {
            SimulatorScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onGenerateScenarios = viewModel::generateWhatIfScenarios,
                onUnlockPro = viewModel::unlockPro
            )
        }
    }
}
