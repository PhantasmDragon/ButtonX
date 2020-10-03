package io.github.phantasmdragon.buttonx.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.github.phantasmdragon.buttonx.R
import kotlinx.android.synthetic.main.activity_main.*
import io.github.phantasmdragon.buttonx.data.SharedPreferences

class MainActivity : AppCompatActivity() {

    private val preferences by lazy {
        SharedPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        attachListeners()
    }

    private fun attachListeners() {
        main_button_messed_up.setOnClickListener {
            Toast.makeText(this, "Messed up", Toast.LENGTH_SHORT).show()
        }
        main_countdown.start()
    }

}
