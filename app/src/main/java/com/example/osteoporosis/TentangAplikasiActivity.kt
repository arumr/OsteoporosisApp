package com.example.osteoporosis

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.osteoporosis.databinding.ActivityModelBinding
import com.example.osteoporosis.databinding.ActivityTentangAplikasiBinding

class TentangAplikasiActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTentangAplikasiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTentangAplikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener{
            onBackPressed()
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }
}