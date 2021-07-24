package com.monsterbrain.slideandplaceview

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.mountain)
        val dragGameView = findViewById<DragAndPlaceGameView>(R.id.dragGameView)
        dragGameView.setBitmap(bitmap)
    }
}