package com.nishiket.oauth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nishiket.oauth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.txtName.text = App.firebaseAuth.currentUser?.email
        binding.btnLogout.setOnClickListener {
            App.firebaseAuth.signOut()
            val intent = Intent(this@MainActivity,AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}