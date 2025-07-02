package com.nishiket.oauth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.nishiket.oauth.databinding.FragmentOtpBinding

class OtpFragment : Fragment() {
    private lateinit var binding: FragmentOtpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        storedVerificationId = arguments?.getString("verificationId") ?: ""

        binding.btnVerify.setOnClickListener {
            val otp = binding.edtOtp.text.toString()
            if (otp.isNotEmpty()) {
                val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(requireContext(), "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    Toast.makeText(requireContext(), "Logged in: ${user?.uid}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
