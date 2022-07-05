package com.example.arkanoidkindgame

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var highscore = Int.MAX_VALUE
    var HasHighscore = true
    val prefName = "MyPreferences"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        highScore()
    }

    override fun onResume() {
        super.onResume()
        highScore()
    }


    fun onPlayButtonPressed(view: View) {
        val playIntent = Intent(this, PlayActivity::class.java)
        startActivityForResult(playIntent, 1234)
    }

    fun onContinueButtonPressed(view: View) {
        val playIntent = Intent(this, PlayActivity::class.java)
        playIntent.putExtra("newGame", false)
        startActivityForResult(playIntent, 1234)
    }

    fun highScore() {
        textView.setText("")
        val gson = Gson()
        var json =
            this.getSharedPreferences(prefName, Context.MODE_PRIVATE)
                .getString("High Score", "")

        val itemType = object : TypeToken<Int>() {}.type
        try {
            highscore = gson.fromJson<Int>(json, itemType)
        } catch (e: Exception) {
            HasHighscore = false
        }
        if (HasHighscore) {
            textView.setText("       The quickest time is: " + highscore + "s")
        }
    }
}
