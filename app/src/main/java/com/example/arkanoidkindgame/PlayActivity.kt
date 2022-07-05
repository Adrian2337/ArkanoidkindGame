package com.example.arkanoidkindgame

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson

class PlayActivity : AppCompatActivity() {
    var newGame = true;
    var bricks: ArrayList<Brick> = ArrayList()
    val prefName = "MyPreferences"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        newGame = intent.getBooleanExtra("newGame", true)


        if (newGame) {
            deleteData()
        }

        setContentView(R.layout.play_layout)
    }


    fun deleteData() {
        val s = this.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        val prefEditor = s.edit()
        val gson = Gson()
        val json = gson.toJson(null)
        prefEditor.putString("Bricks", json)
        prefEditor.commit()
    }

}