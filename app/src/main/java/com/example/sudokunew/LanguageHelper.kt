@file:Suppress("DEPRECATION")

package com.example.sudokunew

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.*
import androidx.core.content.edit

class LanguageChangeHelper {

    fun changeLanguage(context: Context, languageCode: String) {
        // Save to preferences (you'll need to implement this)
        saveLanguagePreference(context, languageCode)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // API 33+ way
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(languageCode)
        } else {
            // API 17+ way (using AppCompat)
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))

            // For very old devices or immediate effect, we need to update the configuration manually
            updateResourcesLegacy(context, languageCode)
        }
    }

    fun getLanguageCode(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales[0]
                ?.toLanguageTag()
                ?.split("-")?.first()
                ?: getSavedLanguagePreference(context) // Fallback to saved preference
                ?: "en"
        } else {
            AppCompatDelegate.getApplicationLocales()[0]
                ?.toLanguageTag()
                ?.split("-")?.first()
                ?: getSavedLanguagePreference(context) // Fallback to saved preference
                ?: "en"
        }
    }

    private fun updateResourcesLegacy(context: Context, languageCode: String) {
        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)

        configuration.setLocale(locale)

        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)

        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    // You need to implement these preference methods
    private fun saveLanguagePreference(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        prefs.edit { putString("app_language", languageCode) }
    }

    private fun getSavedLanguagePreference(context: Context): String? {
        val prefs = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return prefs.getString("app_language", null)
    }
}