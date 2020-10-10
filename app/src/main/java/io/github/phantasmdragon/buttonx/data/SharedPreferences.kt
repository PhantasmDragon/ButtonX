package io.github.phantasmdragon.buttonx.data

import android.content.Context
import android.os.SystemClock
import androidx.core.content.edit

class SharedPreferences(private val context: Context) {

    private val preferences by lazy {
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    }

    var lastClickTime: Long
        get() = preferences.getLong(KEY_LAST_CLICK_TIME, SystemClock.elapsedRealtime())
        set(value) {
            preferences.edit {
                putLong(KEY_LAST_CLICK_TIME, value)
            }
        }

    private companion object {
        const val FILE_NAME = "SYSTEM"
        const val KEY_LAST_CLICK_TIME = "KEY_LAST_CLICK_TIME"
    }

}
