package com.xmbest.floatmaster

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.xmbest.floatmaster.utils.StartupPerformanceTracker

@HiltAndroidApp
class FloatMasterApp : Application() {
    
    override fun onCreate() {
        StartupPerformanceTracker.markAppStart()
        super.onCreate()
        StartupPerformanceTracker.mark("application_created")
    }
}