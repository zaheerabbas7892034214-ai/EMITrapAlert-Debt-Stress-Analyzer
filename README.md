# EMI Trap Alert – Debt Stress Analyzer

A professional Android application that helps users understand if they are trapped in risky EMI debt by analyzing their financial health and providing personalized recommendations.

## 📱 About

EMI Trap Alert is a comprehensive debt management tool that calculates your financial stress score, debt-to-income ratio, and provides actionable insights to improve your financial health. With Pro features, users can access what-if scenarios, debt-free projections, and PDF export capabilities.

## 🚀 Features

### Core Features (Free)
- **Financial Input Form**: Enter monthly income, fixed expenses, total EMIs, and desired savings
- **Debt-to-Income Ratio (DTI)**: Calculate your EMI burden as a percentage of income
- **Financial Stress Score**: Get a 0-100 score based on DTI, savings, and emergency buffer
- **Risk Assessment**: Categorized as Safe, Moderate, High Risk, or Critical
- **Personalized Recommendations**: Receive tailored suggestions to improve your financial situation
- **Safe EMI Limit**: See the recommended maximum EMI amount
- **Minimum Savings Guidance**: Understand how much you should be saving

### Pro Features (One-Time Unlock)
- **What-If Simulator**: 
  - Income increase scenarios
  - EMI reduction scenarios  
  - Expense reduction scenarios
- **Debt-Free Projection**: Timeline showing when you'll be debt-free
- **PDF Export**: Generate and share comprehensive financial analysis reports
- **Google Play Billing Integration**: Secure one-time lifetime unlock

## 🏗️ Technical Stack

- **Language**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (ViewModel + StateFlow)
- **Navigation**: Navigation Compose
- **Build System**: Groovy Gradle (not Kotlin DSL)
- **Billing**: Google Play Billing Library 7.0.0
- **PDF Generation**: iText7 Core 7.2.5

## 📂 Project Structure

```
app/src/main/java/com/zaheer/emitrapalert/
├── MainActivity.kt                    # Single Activity entry point
├── domain/
│   ├── StressCalculator.kt           # Financial analysis logic
│   └── ProjectionEngine.kt           # What-if scenarios & projections
├── viewmodel/
│   ├── EmiUiState.kt                 # UI state data class
│   └── EmiViewModel.kt               # ViewModel with StateFlow
├── ui/
│   ├── navigation/
│   │   └── AppNav.kt                 # Navigation Compose setup
│   ├── screens/
│   │   ├── InputScreen.kt            # Financial input form
│   │   ├── ResultScreen.kt           # Analysis results display
│   │   └── SimulatorScreen.kt        # What-if simulator (Pro)
│   └── theme/
│       ├── Color.kt                  # Material 3 color scheme
│       ├── Theme.kt                  # App theme configuration
│       └── Type.kt                   # Typography definitions
├── billing/
│   └── BillingManager.kt             # Google Play Billing integration
└── utils/
    └── PdfExporter.kt                # PDF report generation
```

## 🔧 Setup Instructions

### Prerequisites
- Android Studio Hedgehog | 2023.1.1 or later
- JDK 8 or later
- Android SDK with API 34

### Building the Project

1. Clone the repository:
```bash
git clone https://github.com/zaheerabbas7892034214-ai/EMITrapAlert-Debt-Stress-Analyzer.git
cd EMITrapAlert-Debt-Stress-Analyzer
```

2. Open the project in Android Studio

3. Sync Gradle files

4. Run on an emulator or physical device (Min SDK 24)

### Google Play Billing Setup

To enable Pro feature purchases:

1. Create a Google Play Console account
2. Create an app listing
3. Add an in-app product with ID: `emitrap_pro_unlock`
4. Configure pricing (one-time purchase)
5. Test with Google Play's test account

## 🧮 Calculation Logic

### Debt-to-Income Ratio (DTI)
```kotlin
DTI = (Total EMIs / Monthly Income) × 100
```

### Financial Stress Score (0-100)
- Base stress calculated from DTI percentage
- Additional stress added if savings target not met
- Extra stress if expenses exceed income
- Score capped between 0-100

### Risk Levels
- **SAFE**: DTI < 30% and Stress < 30
- **MODERATE**: DTI < 40% and Stress < 50
- **HIGH_RISK**: DTI < 50% and Stress < 70
- **CRITICAL**: DTI ≥ 50% or Stress ≥ 70

## 📊 What-If Scenarios (Pro Feature)

The simulator allows users to explore:
- **Income Increase**: See impact of 20% income increase
- **EMI Reduction**: Analyze reducing EMIs by 20%
- **Expense Reduction**: Evaluate cutting expenses by 15%

Each scenario shows:
- New DTI ratio
- New stress score
- New risk level
- Improvement summary

## 🔐 Security Features

- Secure billing with Google Play Billing Library
- Purchase acknowledgment handling
- Persistent Pro status in SharedPreferences
- Purchase restoration capability

## 📄 License

Copyright © 2024 EMI Trap Alert. All rights reserved.

## 👨‍💻 Developer

Developed by Zaheer Abbas

## 🤝 Contributing

This is a proprietary project. Contributions are not currently accepted.

## 📞 Support

For support or queries, please contact through the app's Play Store listing.
