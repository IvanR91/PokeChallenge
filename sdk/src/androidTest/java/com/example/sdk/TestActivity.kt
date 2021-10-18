package com.example.sdk

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import java.net.URL

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val frameLayout = FrameLayout(this)
        setContentView(frameLayout)

        frameLayout.addView(
            SdkCore.createLayoutFrom(
                this,
                URL("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/6.png"),
                "This is a pokemon description"
            )
        )
    }
}