package com.israadev.fluxlauncher

import android.app.Application
import android.util.Log
import com.israadev.fluxlauncher.core.crash.CrashManager
import com.israadev.fluxlauncher.ui.activities.ErrorActivity

class FluxApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        org.lwjgl.glfw.CallbackBridge.sContext = this
        com.movtery.zalithlauncher.bridge.ZLNativeInvoker.appContext = this
        com.israadev.fluxlauncher.core.settings.SettingsManager.init(this)
        com.israadev.fluxlauncher.core.lang.FluxLanguageManager.init(this)
        com.israadev.fluxlauncher.core.account.AccountManager.init(this)

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                Log.e("FluxApplication", "Uncaught exception on thread ${thread.name}", throwable)
                // Catat ke sistem crash manager
                CrashManager.recordLauncherCrash(this, throwable)
                // Tampilkan ErrorActivity mandiri
                ErrorActivity.showLauncherCrash(this, throwable)
            } catch (e: Exception) {
                Log.e("FluxApplication", "Failed to dispatch crash activity", e)
                defaultHandler?.uncaughtException(thread, throwable)
            }

            // Hentikan proses yang mengalami crash agar tidak memicu ANR / loop
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(10)
        }
    }
}
