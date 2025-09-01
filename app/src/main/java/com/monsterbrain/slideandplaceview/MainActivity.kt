package com.monsterbrain.slideandplaceview

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Keep the original View-based implementation working
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.mountain)
        val dragGameView = findViewById<DragAndPlaceGameView>(R.id.dragGameView)
        dragGameView.setBitmap(bitmap)
        
        // Add button to switch to Compose version
        val switchToComposeButton = findViewById<Button>(R.id.switchToComposeButton)
        switchToComposeButton?.setOnClickListener {
            startActivity(Intent(this, ComposeMainActivity::class.java))
        }
    }
}