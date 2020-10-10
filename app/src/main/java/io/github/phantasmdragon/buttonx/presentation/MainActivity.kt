package io.github.phantasmdragon.buttonx.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import io.github.phantasmdragon.buttonx.R
import io.github.phantasmdragon.buttonx.data.SharedPreferences
import io.github.phantasmdragon.buttonx.presentation.view.CheckerboardDrawable
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val preferences by lazy(LazyThreadSafetyMode.NONE) {
        SharedPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setCheckerboardBackground()
        attachListeners()

        runStopwatch()
    }

    private fun setCheckerboardBackground() {
        main_root.background = CheckerboardDrawable(this)
    }

    private fun attachListeners() {
        main_button_messed_up.setOnClickListener {
            onMessedUpClick()
        }
        main_message.setOnLongClickListener {
            Snackbar.make(it, R.string.main_author, Snackbar.LENGTH_SHORT)
                .setAnchorView(main_button_messed_up)
                .show()

            true
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
