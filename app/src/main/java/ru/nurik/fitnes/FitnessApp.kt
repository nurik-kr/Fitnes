package ru.nurik.fitnes

import android.app.Application
import ru.nurik.fitnes.data.PreferenceHelper

class FitnessApp : Application() {

    override fun onCreate() {
        super.onCreate()
        PreferenceHelper.initPreference(applicationContext)
    }

}