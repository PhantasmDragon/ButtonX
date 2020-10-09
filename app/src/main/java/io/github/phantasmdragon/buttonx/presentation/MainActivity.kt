package io.github.phantasmdragon.buttonx.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.phantasmdragon.buttonx.R
import kotlinx.android.synthetic.main.activity_main.*
import io.github.phantasmdragon.buttonx.data.SharedPreferences

class MainActivity : AppCompatActivity() {

    private val preferences by lazy(LazyThreadSafetyMode.NONE) {
        SharedPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        attachListeners()
        runStopwatch()
    }

    private fun attachListeners() {
        main_button_messed_up.setOnClickListener {
            onMessedUpClick()
        }
    }

    private fun runStopwatch() {
        main_time_count.apply {
            startTime = preferences.lastClickTime
            start()
        }
    }

    private fun onMessedUpClick() {
        main_time_count.restart()

        preferences.lastClickTime = main_time_count.startTime
    }

}
