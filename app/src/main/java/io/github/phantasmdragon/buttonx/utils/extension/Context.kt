package io.github.phantasmdragon.buttonx.utils.extension

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Context.getColorCompat(@ColorRes colorId: Int): Int {
    return ContextCompat.getColor(this, colorId)
}
