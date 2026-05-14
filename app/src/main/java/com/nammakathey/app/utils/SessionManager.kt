package com.nammakathey.app.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("NammaKatheyPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_NAME = "userName"
        private const val KEY_USER_EMAIL = "userEmail"
        private const val KEY_LANGUAGE = "appLanguage"
        private const val KEY_SCORE = "totalScore"
        private const val KEY_BADGES = "unlockedBadges"
    }

    fun saveLoginSession(name: String, email: String) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logout() {
        val currentLang = getLanguage()
        prefs.edit().clear().apply()
        setLanguage(currentLang) // Preserve language across logouts
    }

    fun getUserName(): String {
        return prefs.getString(KEY_USER_NAME, "User") ?: "User"
    }

    fun getUserEmail(): String {
        return prefs.getString(KEY_USER_EMAIL, "") ?: ""
    }

    fun setLanguage(langCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, langCode).apply()
    }

    fun getLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }
    
    fun addScore(points: Int) {
        val currentScore = getScore()
        prefs.edit().putInt(KEY_SCORE, currentScore + points).apply()
    }
    
    fun getScore(): Int {
        return prefs.getInt(KEY_SCORE, 0)
    }

    fun getUnlockedBadgesCount(): Int {
        val score = getScore()
        return when {
            score >= 100 -> 5
            score >= 80 -> 4
            score >= 60 -> 3
            score >= 40 -> 2
            score >= 20 -> 1
            else -> 0
        }
    }

    fun addBadge(heroId: String) {
        val currentBadges = getBadges().toMutableSet()
        currentBadges.add(heroId)
        prefs.edit().putStringSet(KEY_BADGES, currentBadges).apply()
    }

    fun getBadges(): Set<String> {
        return prefs.getStringSet(KEY_BADGES, emptySet()) ?: emptySet()
    }
}
