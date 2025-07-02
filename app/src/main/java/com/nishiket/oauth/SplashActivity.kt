package com.nishiket.oauth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.nishiket.oauth.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private var currentUser: FirebaseUser? = null
    private lateinit var binding: ActivitySplashBinding
    override fun onStart() {
        super.onStart()
        currentUser = App.firebaseAuth.currentUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent: Intent
            if (currentUser != null) {
                intent = Intent(this, MainActivity::class.java)
            } else {
                intent = Intent(this, AuthActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 3000)
    }
}