package com.nammakathey.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.nammakathey.app.R
import com.nammakathey.app.databinding.ActivityRegisterBinding
import com.nammakathey.app.ui.base.BaseActivity
import com.nammakathey.app.ui.main.MainActivity
import com.nammakathey.app.utils.SessionManager

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            val confirmPass = binding.etConfirmPassword.text.toString().trim()

            if (name.isEmpty()) {
                binding.etName.error = getString(R.string.error_field_required)
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.etEmail.error = getString(R.string.error_field_required)
                return@setOnClickListener
            }
            if (pass.isEmpty()) {
                binding.etPassword.error = getString(R.string.error_field_required)
                return@setOnClickListener
            }
            if (pass != confirmPass) {
                binding.etConfirmPassword.error = getString(R.string.error_password_mismatch)
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        
                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { profileTask ->
                                // Save user to Firestore regardless of profile update success
                                val userData = hashMapOf(
                                    "uid" to (user?.uid ?: ""),
                                    "fullName" to name,
                                    "email" to email,
                                    "createdAt" to System.currentTimeMillis()
                                )
                                
                                user?.uid?.let { uid ->
                                    db.collection("users").document(uid)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            binding.progressBar.visibility = View.GONE
                                            sessionManager.saveLoginSession(name, email)
                                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(this, MainActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        }
                                        .addOnFailureListener { e ->
                                            binding.progressBar.visibility = View.GONE
                                            Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                    } else {
                        binding.progressBar.visibility = View.GONE
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthWeakPasswordException -> "The password is too weak."
                            is FirebaseAuthInvalidCredentialsException -> "The email address is badly formatted."
                            is FirebaseAuthUserCollisionException -> "The email address is already in use by another account."
                            else -> task.exception?.message ?: "Registration failed"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }
}
