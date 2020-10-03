package io.github.phantasmdragon.buttonx.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.phantasmdragon.buttonx.R
import io.github.phantasmdragon.buttonx.data.SharedPreferences

class MainActivity : AppCompatActivity() {

    private val preferences by lazy {
        SharedPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}
