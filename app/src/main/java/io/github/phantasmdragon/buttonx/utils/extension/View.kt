package io.github.phantasmdragon.buttonx.utils.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = true) {
    layoutInflater.inflate(layoutRes, this, attachToRoot)
}

val View.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(context)
