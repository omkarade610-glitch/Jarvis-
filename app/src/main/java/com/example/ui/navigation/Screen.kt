package com.example.ui.navigation

sealed class Screen(val title: String) {
    data object Dashboard : Screen("JARVIS Home")
    data object Chat : Screen("AI Chat Assistant")
    data object VoiceAssistant : Screen("Voice Assistant")
    data object PhoneControl : Screen("Phone Control")
    data object WebSearch : Screen("Real-time Web Search")
    data object CodingStudio : Screen("Advanced Coding Studio")
    data object WebsiteBuilder : Screen("AI Website Builder")
    data object AppBuilder : Screen("AI Android App Builder")
    data object DrawingStudio : Screen("AI Drawing Studio")
    data object CharacterDesigner : Screen("AI Character Designer")
    data object AnimeStudio : Screen("Anime Production Studio")
    data object Memory : Screen("Personal Memory System")
    data object OwnerPanel : Screen("Owner Control Panel")
    data object Settings : Screen("System Settings")
}
