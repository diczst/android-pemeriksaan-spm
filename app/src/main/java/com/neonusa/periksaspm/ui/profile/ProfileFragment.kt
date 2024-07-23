package com.neonusa.periksaspm.ui.profile

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Image
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.neonusa.periksaspm.R
import com.neonusa.periksaspm.databinding.FragmentProfileBinding
import com.neonusa.periksaspm.ui.login.LoginActivity
import com.neonusa.periksaspm.ui.pdfview.PdfViewActivity
import com.neonusa.periksaspm.utils.Helper
import com.neonusa.periksaspm.utils.PDFUtility
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class ProfileFragment : Fragment() {
    var firebaseAuth = FirebaseAuth.getInstance()
    val db = Firebase.firestore


    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi FirebaseStorage
    val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(),gso)

        showLoading()

        binding.edtNama.setText(firebaseAuth.currentUser?.displayName)
        binding.edtEmail.setText(firebaseAuth.currentUser?.email)

        db.collection("users").document(firebaseAuth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { document ->
                if(document.exists()){
                    if(document.contains("nip")){
                        binding.edtNip.setText(document.getString("nip"))
                        binding.layoutTambahNip.visibility = View.GONE
                    }
                } else {
                    // belum punya nip
                    binding.layoutTambahNip.visibility = View.VISIBLE
//                    Toast.makeText(requireActivity(), "Pengguna belum memiliki NIP", Toast.LENGTH_SHORT).show()
                }
            }

        // load img dari firebase storage
        val storageRef = storageRef.child("images/${firebaseAuth.currentUser?.uid}.jpg") // Sesuaikan dengan lokasi penyimpanan gambar Anda
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri)
                .into(binding.img)
            hideLoading()
        }.addOnFailureListener {
            // Penanganan jika gagal mengambil URL gambar
            Log.e("ProfileFragment", "Error getting image URL (image not exist and or internet not available)", it)
            hideLoading()
        }



        binding.btnLogout.setOnClickListener {
            showLoading()
            Handler().postDelayed(Runnable {
                hideLoading()
                firebaseAuth.signOut() // logout
                googleSignInClient.signOut() // agar bisa memilih akun lagi setelah logout

                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }, 3000)
        }

        binding.img.setOnClickListener {
            pickImage()
        }

        binding.ivBantuan.setOnClickListener {
            startActivity(Intent(requireActivity(),PdfViewActivity::class.java))
        }

        binding.tvTambahNip.setOnClickListener {
            val dialog = Dialog(requireActivity())
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_tambah_nip)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val edtNip : TextView = dialog.findViewById(R.id.edt_nip_dialog)
            val btnBatalSimpan : Button = dialog.findViewById(R.id.btn_batal_dialog_simpan)
            val btnSimpan : Button = dialog.findViewById(R.id.btn_simpan_dialog_simpan)

            btnBatalSimpan.setOnClickListener{
                dialog.dismiss()
            }

            btnSimpan.setOnClickListener {
                if(edtNip.text!!.isEmpty()){
                    edtNip.error = "Harap mengisi NIP terlebih dahulu"
                    edtNip.requestFocus()
                } else {

                    //todo : bikin data user baru di cloud firestore + nipnya juga

                    val userId = firebaseAuth.currentUser?.uid
                    val userMap = hashMapOf(
                        "name" to firebaseAuth.currentUser?.displayName,
                        "email" to firebaseAuth.currentUser?.email,
                        "nip" to edtNip.text.toString()
                    )

                    if (userId != null) {
                        db.collection("users").document(userId).set(userMap)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    binding.edtNip.setText(edtNip.text.toString())
                                    binding.layoutTambahNip.visibility = View.GONE
                                    Toast.makeText(requireActivity(), "NIP berhasil disimpan", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                } else {
                                    Toast.makeText(requireActivity(), "Gagal menyimpan data, periksa koneksi anda", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()

                                }
                            }
                    }

                }
            }

            // note penyebab error inflate class unknown karena mengugnakan backgroundColor ?attr yang belum didefinisikan di theme

            dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.show()
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            if (resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data!!
                showLoading()
                Handler().postDelayed(Runnable {
                    hideLoading()
                    uploadImage(uri)
                    Picasso.get().load(uri).into(binding.img)
                }, 2000)
            }
        }


    private fun pickImage(){
        ImagePicker.with(this)
            .compress(1024)
            .createIntent { intent ->
            startForProfileImageResult.launch(intent)
        }
    }

    fun uploadImage(imageUri: Uri) {
        val riversRef: StorageReference = storageRef.child("images/${firebaseAuth.currentUser?.uid}.jpg")

        riversRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Gambar berhasil diunggah
                Log.d("Profile Fragment", "Image uploaded successfully")

                // Dapatkan URL gambar yang diunggah
                riversRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    // Disimpan ke Firebase Database atau di tempatkan di tampilan profil pengguna
                }.addOnFailureListener {
                    // Penanganan kegagalan saat mendapatkan URL gambar
                }
            }
            .addOnFailureListener { exception ->
                // Penanganan kegagalan saat mengunggah gambar
                Log.e("Profile Fragment", "Error uploading image", exception)
            }
    }

    private fun showLoading(){
        binding.progressbarLoading.visibility = View.VISIBLE
        binding.darkOverlay.visibility = View.VISIBLE
        // menonaktifkan user interactions
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun hideLoading(){
        binding.progressbarLoading.visibility = View.GONE
        binding.darkOverlay.visibility = View.GONE
        // mengaktifkan user interactions
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}