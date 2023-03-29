package ru.kozlovss.workingcontacts.application

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import ru.kozlovss.workingcontacts.R

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(getString(R.string.MAPS_API_KEY))
    }
}