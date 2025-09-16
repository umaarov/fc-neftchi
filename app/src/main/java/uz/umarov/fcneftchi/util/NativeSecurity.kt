package uz.umarov.fcneftchi.util

import kotlin.system.exitProcess

object NativeSecurity {
    init {
        System.loadLibrary("sec")
    }

    private external fun isDebuggerAttached(): Boolean

    fun checkAndExit() {
        if (isDebuggerAttached()) {
            exitProcess(0)
        }
    }
}