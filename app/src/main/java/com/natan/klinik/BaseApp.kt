package com.natan.klinik

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import com.pixplicity.easyprefs.library.Prefs

class BaseApp: Application() {

    override fun onCreate() {
        super.onCreate()
        setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Prefs.Builder()
            .setContext(this)
            .setMode(MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }
}