package com.nishiket.oauth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nishiket.oauth.databinding.ActivityAuthBinding


class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction()
            .replace(binding.frgContainer.id, SignInFragment())
            .commit()
    }
}