package com.neonusa.periksaspm.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.neonusa.periksaspm.MainActivity
import com.neonusa.periksaspm.R
import com.neonusa.periksaspm.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    companion object {
        private const val SIGN_IN_CODE = 100
        private const val DEFAULT_PASSWORD = "SPM123"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    var firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        binding.btnLoginGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, SIGN_IN_CODE)
        }

        binding.btnLogin.setOnClickListener {
            // note : user bisa login dengan username apapun
            loginUser(binding.edtEmail.text.toString(), DEFAULT_PASSWORD)
        }
    }

    override fun onStart() {
        super.onStart()
        // jika sudah login, langsung ke halaman utama
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_CODE){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken.toString())
            } catch (e: ApiException){
                e.printStackTrace()
            }
        }
    }

    fun firebaseAuthWithGoogle(idToken: String){
        val credentials = GoogleAuthProvider.getCredential(idToken,null)
        firebaseAuth.signInWithCredential(credentials)
            .addOnSuccessListener {
                binding.progressbarLoading.visibility = View.VISIBLE
                binding.darkOverlay.visibility = View.VISIBLE

                // freeze layar atau menonaktifkan user interactions
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                Handler().postDelayed(Runnable {
                    binding.progressbarLoading.visibility = View.GONE
                    binding.darkOverlay.visibility = View.GONE

                    // mengaktifkan kembali user interactions
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                    finish()
                }, 3000)

            }
    }

    private fun loginUser(email: String, password: String) {
        if(binding.edtNama.text!!.isEmpty()){
            binding.edtNama.error = "Nama tidak boleh kosong"
            binding.edtNama.requestFocus()
            return
        }

        if(binding.edtEmail.text!!.isEmpty()){
            binding.edtEmail.error = "Email tidak boleh kosong"
            binding.edtEmail.requestFocus()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Akun tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
    }



}