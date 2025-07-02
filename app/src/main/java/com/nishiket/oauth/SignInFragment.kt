package com.nishiket.oauth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.nishiket.oauth.databinding.FragmentSignInBinding
import java.util.concurrent.TimeUnit

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = App.firebaseAuth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtSignUp.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frg_container, SignUpFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.btnSignIn.setOnClickListener {
            val email0rPass = binding.edtEmailOrNumber.text.toString()
            val pass = binding.edtPass.text.toString()
            when {
                isValidEmail(email0rPass) -> {
                    auth.signInWithEmailAndPassword(email0rPass, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("TAG", "signInWithEmail:success")
                                val intent = Intent(requireActivity(), MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithEmail:failure", task.exception)
                                Toast.makeText(
                                    context,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                }

                isValidPhone(email0rPass) -> {
                    sendVerificationCode("+91$email0rPass")
                }

                else -> {
                    Toast.makeText(
                        context,
                        "Please enter a valid email or phone number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(requireContext(), "Verification failed: ${e.message}", Toast.LENGTH_LONG)
                .show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            storedVerificationId = verificationId
            resendToken = token
            Toast.makeText(requireContext(), "OTP sent", Toast.LENGTH_SHORT).show()

            val otpFragment = OtpFragment()
            val bundle = Bundle().apply {
                putString("verificationId", verificationId)
            }
            otpFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frg_container, otpFragment)
                .addToBackStack(null)
                .commit()
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    Toast.makeText(requireContext(), "Logged in: ${user?.uid}", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^[0-9]{10}$"))
    }
}