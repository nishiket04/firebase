package com.nishiket.oauth

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.nishiket.oauth.databinding.FragmentSignUpBinding
import java.util.concurrent.TimeUnit

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
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
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            val input = binding.edtEmailOrNumber.text.toString()
            val password = binding.edtPass.text.toString()

            when {
                isValidEmail(input) -> {
                    auth.createUserWithEmailAndPassword(input, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(requireActivity(), MainActivity::class.java))
                                requireActivity().finish()
                            } else {
                                Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }

                isValidPhone(input) -> {
                    sendVerificationCode("+91$input")
                }

                else -> {
                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireContext(), "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
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


    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^[0-9]{10}$"))
    }
}
