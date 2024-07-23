package com.neonusa.periksaspm.ui.periksa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.neonusa.periksaspm.R
import com.neonusa.periksaspm.data.Pemeriksaan
import com.neonusa.periksaspm.databinding.ActivityPeriksaBinding
import com.neonusa.periksaspm.ui.periksadetail.PeriksaDetailActivity
import com.neonusa.periksaspm.ui.riwayatpemeriksaan.RiwayatPemeriksaanAdapter
import com.neonusa.periksaspm.utils.Helper
import java.util.Locale

class PeriksaActivity : AppCompatActivity() {
    companion object {
        const val JENIS_SPM = "JENIS_SPM"
        const val NAMA_PERJALANAN_STASIUN = "NAMA_PERJALANAN_STASIUN"
    }

    private val listJenisPelayanan = arrayListOf<String>()
    private lateinit var jenisSpm: String
    private lateinit var namaPerjalananStasiun: String

    private val pemeriksaanList = mutableListOf<Pemeriksaan>()

    val db = Firebase.firestore
    val docRef = db
        .collection("pemeriksaan-new")

    private lateinit var binding: ActivityPeriksaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeriksaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jenisSpm = intent.getStringExtra(JENIS_SPM)!!
        namaPerjalananStasiun = intent.getStringExtra(NAMA_PERJALANAN_STASIUN)!!

        binding.tvNamaStasiunPerjalananPeriksa.text = namaPerjalananStasiun
        binding.tvHeader.text = jenisSpm
        binding.tvTanggal.text = Helper.getTanggalToday()

        binding.btnBack.setOnClickListener { finish() }
        
        getData()
    }

    override fun onResume() {
        super.onResume()

        getData()

        binding.btnSimpan.setOnClickListener {
            if(listJenisPelayanan.size == 6){
                val dialog = AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Kesimpulan")
                    .setMessage("Apakah kamu yakin ingin menyimpan data?")
                    .setPositiveButton("Ya") { dialog, which ->
                        var totalPersentase = 0f
                        pemeriksaanList.forEach {
                            totalPersentase += it.persentase!!.toFloat()
                        }
                        val formattedPersentase = String.format(Locale.US, "%.2f", totalPersentase/pemeriksaanList.size)
                        var nomor = 1
                        var daftar = ""

                        pemeriksaanList.forEach {
                            daftar += "$nomor. ${it.aspek} ${it.persentase}% Memenuhi SPM\n"
                            nomor++
                        }
                        val dialog = AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setTitle("Kesimpulan")
                            .setMessage("$daftar\n\nTotal $formattedPersentase% Memenuhi SPM")
                            .setPositiveButton("OK") { dialog, which ->
                                finish()
                            }
                            .create()
                        dialog.show()
                    }
                    .setNegativeButton("Tidak"){ dialog, which ->
                        dialog.dismiss()
                    }
                    .create()
                dialog.show()
            } else {
                // untuk tes dialog di activity
                Toast.makeText(this, "Harap mengisi semua data terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getData(){
        val query = docRef
            .whereEqualTo("nama", namaPerjalananStasiun)
            .whereEqualTo("tanggal", Helper.getTanggalTodayAngkaVer())
        showLoading()
        query.get()
            .addOnSuccessListener {documents ->
                listJenisPelayanan.clear()
                pemeriksaanList.clear()
                for (document in documents) {
                    val pemeriksaan = document.toObject<Pemeriksaan>()
                    listJenisPelayanan.add(pemeriksaan.aspek.toString())
                    pemeriksaanList.add(pemeriksaan)
                }

                if(!listJenisPelayanan.contains("Keselamatan")){
                    binding.cvKeselamatan.setOnClickListener {
                        toHalamanPengisian(PeriksaDetailActivity.SPM_KESELAMATAN, namaPerjalananStasiun, jenisSpm)
                    }
                } else {
                    binding.iconCentangKeselamatan.visibility = View.VISIBLE
                    binding.cvKeselamatan.setOnClickListener {
                        // do nothing
                    }
                }

                if(!listJenisPelayanan.contains("Keamanan")){
                    binding.cvKeamanan.setOnClickListener {
                        toHalamanPengisian(PeriksaDetailActivity.SPM_KEAMANAN, namaPerjalananStasiun, jenisSpm)
                    }
                } else {
                    binding.iconCentangKeamanan.visibility = View.VISIBLE
                    binding.cvKeamanan.setOnClickListener {
                        // do nothing
                    }
                }

                if(!listJenisPelayanan.contains("Kehandalan")){
                    binding.cvKehandalan.setOnClickListener {
                        toHalamanPengisian(PeriksaDetailActivity.SPM_KEHANDALAN, namaPerjalananStasiun, jenisSpm)
                    }
                } else {
                    binding.iconCentangKehandalan.visibility = View.VISIBLE
                    binding.cvKehandalan.setOnClickListener {
                        // do nothing
                    }
                }

                if(!listJenisPelayanan.contains("Kenyamanan")){
                    binding.cvKenyamanan.setOnClickListener {
                        toHalamanPengisian(PeriksaDetailActivity.SPM_KENYAMANAN, namaPerjalananStasiun, jenisSpm)
                    }
                } else {
                    binding.iconCentangKenyamanan.visibility = View.VISIBLE
                    binding.cvKenyamanan.setOnClickListener {
                        // do nothing
                    }
                }

                if(!listJenisPelayanan.contains("Kemudahan")){
                    binding.cvKemudahan.setOnClickListener {
                        toHalamanPengisian(PeriksaDetailActivity.SPM_KEMUDAHAN, namaPerjalananStasiun, jenisSpm)
                    }
                } else {
                    binding.iconCentangKemudahan.visibility = View.VISIBLE
                    binding.cvKemudahan.setOnClickListener {
                        // do nothing
                    }
                }

                if(!listJenisPelayanan.contains("Kesetaraan")){
                    binding.cvKesetaraan.setOnClickListener {
                        toHalamanPengisian(PeriksaDetailActivity.SPM_KESETARAAN, namaPerjalananStasiun, jenisSpm)
                    }
                } else {
                    binding.iconCentangKesetaraan.visibility = View.VISIBLE
                    binding.cvKesetaraan.setOnClickListener {
                        // do nothing
                    }
                }
                hideLoading()
            }.addOnFailureListener {
                hideLoading()
            }
    }

    fun toHalamanPengisian(aspek: String?, namaPerjalananStasiun: String?, jenisSpm: String?){
        val intent = Intent(this, PeriksaDetailActivity::class.java)
        intent.putExtra(PeriksaDetailActivity.NAMA_SPM, aspek)
        intent.putExtra(PeriksaDetailActivity.NAMA_STASIUN_PERJALANAN,namaPerjalananStasiun)
        intent.putExtra(PeriksaDetailActivity.JENIS_SPM,jenisSpm)
        startActivity(intent)
    }

    private fun showLoading(){
        binding.progressbarLoading.visibility = View.VISIBLE
        binding.darkOverlay.visibility = View.VISIBLE
        // menonaktifkan user interactions
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun hideLoading(){
        binding.progressbarLoading.visibility = View.GONE
        binding.darkOverlay.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}