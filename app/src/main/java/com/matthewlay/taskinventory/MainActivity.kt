package com.matthewlay.taskinventory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Baris ini yang paling PENTING.
        // Dia memerintahkan aplikasi untuk membuka desain XML 'activity_main'
        // di mana NavHostFragment (Peta Navigasi) berada.
        setContentView(R.layout.activity_main)
    }
}