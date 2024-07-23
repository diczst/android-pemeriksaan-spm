package com.neonusa.periksaspm.ui.periksadetail

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.neonusa.periksaspm.R
import com.neonusa.periksaspm.databinding.ActivityPeriksaDetailBinding
import com.neonusa.periksaspm.utils.Helper
import com.neonusa.periksaspm.utils.SPMDataFormGenerator
import java.util.Locale

class PeriksaDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPeriksaDetailBinding

    var firebaseAuth = FirebaseAuth.getInstance()

    lateinit var spmItems: List<Pair<String, List<String>>>
    var nip: String = "-"

    private lateinit var namaStasiunPerjalanan: String
    private lateinit var jenisSPM: String
    private lateinit var namaSPM: String
//    private lateinit var jenis_spm_firestore: String
    private lateinit var tanggalToday: String
    private lateinit var formattedPersentase: String

    val db = Firebase.firestore
    val storageRef = FirebaseStorage.getInstance().reference

    // Membuat MutableList yang berisi mutablelist dengan tipe data apapun
    val isianUser = mutableListOf<MutableList<Int>>()

    // menyimpan nama-nama item
    val namaItemList = ArrayList<String>()

    // Membuat ArrayList untuk menyimpan URI/gambar dan list namanya
//    val uriList = ArrayList<Uri?>

    val uriList = ArrayList<ArrayList<Uri?>>()
    val namaGambarList = ArrayList<ArrayList<String?>>()
    val keteranganList = ArrayList<String>()

    companion object {
        const val NAMA_SPM = "NAMA_SPM"
        const val NAMA_STASIUN_PERJALANAN = "NAMA_STASIUN_PERJALANAN"
        const val JENIS_SPM = "JENIS_SPM"

        const val SPM_STASIUN = "SPM Stasiun"
        const val SPM_PERJALANAN = "SPM Perjalanan"

        const val SPM_KESELAMATAN = "Keselamatan"
        const val SPM_KEAMANAN = "Keamanan"
        const val SPM_KEHANDALAN = "Kehandalan"
        const val SPM_KENYAMANAN = "Kenyamanan"
        const val SPM_KEMUDAHAN = "Kemudahan"
        const val SPM_KESETARAAN = "Kesetaraan"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeriksaDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)

        showLoading()

        namaSPM = intent.getStringExtra(NAMA_SPM).toString()
        namaStasiunPerjalanan = intent.getStringExtra(NAMA_STASIUN_PERJALANAN).toString()
        jenisSPM = intent.getStringExtra(JENIS_SPM).toString()

        tanggalToday = Helper.getTanggalTodayAngkaVer()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tvHeader.text = namaSPM
        val inflater = LayoutInflater.from(this)

        if(jenisSPM.equals(SPM_STASIUN)){
            spmItems = if (namaSPM.equals(SPM_KESELAMATAN)){
                SPMDataFormGenerator.spmKeselamatanItems
            } else if(namaSPM.equals(SPM_KEAMANAN)) {
                SPMDataFormGenerator.spmKeamananItems
            } else if(namaSPM.equals(SPM_KEHANDALAN)) {
                SPMDataFormGenerator.spmKehandalanItems
            } else if(namaSPM.equals(SPM_KENYAMANAN)) {
                SPMDataFormGenerator.spmKenyamananItems
            } else if(namaSPM.equals(SPM_KEMUDAHAN)) {
                SPMDataFormGenerator.spmKemudahanItems
            } else if(namaSPM.equals(SPM_KESETARAAN)) {
                SPMDataFormGenerator.spmKesetaraanItems
            }
            else {
                // biarkan kosong karena wajib
                return
            }
        } else if (jenisSPM.equals(SPM_PERJALANAN)) {
            spmItems = if (namaSPM.equals(SPM_KESELAMATAN)){
                SPMDataFormGenerator.spmKeselamatanItemsPerjalanan
            } else if(namaSPM.equals(SPM_KEAMANAN)) {
                SPMDataFormGenerator.spmKeamananItemsPerjalanan
            } else if(namaSPM.equals(SPM_KEHANDALAN)) {
                SPMDataFormGenerator.spmKehandalanItemsPerjalanan
            } else if(namaSPM.equals(SPM_KENYAMANAN)) {
                SPMDataFormGenerator.spmKenyamananItemsPerjalanan
            } else if(namaSPM.equals(SPM_KEMUDAHAN)) {
                SPMDataFormGenerator.spmKemudahanItemsPerjalanan
            } else if(namaSPM.equals(SPM_KESETARAAN)) {
                SPMDataFormGenerator.spmKesetaraanItemsPerjalanan
            }
            else {
                // biarkan kosong karena wajib
                return
            }
        }

        for ((indexSpmItem, pair) in spmItems.withIndex()) {
            val (spmItem, subItems) = pair

            // tambahkan null terlebih dahulu sebanyak jumlah item (max upload gambar = 3 ditandai 3 null)
            uriList.add(arrayListOf(null, null, null))

            namaGambarList.add(arrayListOf("", "", ""))
            keteranganList.add("")

            // membuat baris matrix sebanyak sumlah item spm
            // elemen awalnya -1
            val barisBaru = mutableListOf(-1)
            isianUser.add(barisBaru.toMutableList())
            val itemSPM = inflater.inflate(R.layout.item_spm, null, false) as LinearLayout
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                    topMargin = 48
            }
            itemSPM.layoutParams = params
            itemSPM.findViewById<TextView>(R.id.tv_item_spm_nama).text = spmItem

            val btnTambahFoto = itemSPM.findViewById<TextView>(R.id.btn_tambah_foto)

            val tvNamaFile = itemSPM.findViewById<TextView>(R.id.tv_nama_file_item)
            namaItemList.add(spmItem)

            val edtKeterangan = itemSPM.findViewById<EditText>(R.id.edt_keterangan_item)

            binding.linearLayoutItemSpm.addView(itemSPM)

            for ((indexSubitem, subitem) in subItems.withIndex()) {
                // tidak perlu ditambahkan lagi untuk yang pertama karena sudah
                if(indexSubitem != 0){
                    isianUser[indexSpmItem].add(-1)
                }
                // tmbahkan juga listener untuk edittext
                if(indexSubitem + 1 == subItems.size){
                    val startForProfileImageResult =
                        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                            val resultCode = result.resultCode
                            if (resultCode == Activity.RESULT_OK) {
                                val uri = result.data?.data!!

                                // tambahkan uri ke list
                                // todo ini urilist jadi array dua dimensi
                                // pemahaman akan fitur ini tersedia di chatgpt chat yang berjudul "Create Kotlin ArrayList URI"

                                val row = uriList[indexSpmItem]
                                for (i in row.indices) {
                                    if (row[i] == null) {
                                        row[i] = uri
                                        Toast.makeText(this, "$i", Toast.LENGTH_SHORT).show()
                                        break
                                    }
                                }
//                                uriList[indexSpmItem] = uri
//                                namaGambarList[indexSpmItem] = namaFile

                                val rowNamaGambar = namaGambarList[indexSpmItem]
                                for (i in rowNamaGambar.indices) {
                                    if (rowNamaGambar[i].equals("")) {
                                        val namaFile = "$namaStasiunPerjalanan-$tanggalToday-$namaSPM-$indexSpmItem$i.jpg"
                                        rowNamaGambar[i] = namaFile
                                        Toast.makeText(this, "$namaFile | $i", Toast.LENGTH_SHORT).show()
                                        break
                                    }
                                }

                                val joinedNamaGambar = rowNamaGambar.joinToString(separator = " ")
                                tvNamaFile.text = joinedNamaGambar
//                                Toast.makeText(this@PeriksaDetailActivity, "$isianUser", Toast.LENGTH_SHORT).show()
                                hideLoading()
                                tvNamaFile.setTextColor(ContextCompat.getColor(this, R.color.color_image_added))
                            } else if (resultCode == Activity.RESULT_CANCELED){
                                hideLoading()
                            }
                        }

                    //

                    btnTambahFoto.setOnClickListener {
                        ImagePicker.with(this)
                            .compress(256)
                            .galleryMimeTypes(  // membatasi gambar yang keluar
                                mimeTypes = arrayOf(
                                    "image/png",
                                    "image/jpg",
                                    "image/jpeg"
                                )
                            )
                            .createIntent { intent ->
                                showLoading()
                                startForProfileImageResult.launch(intent)
                            }
                    }

                    edtKeterangan.addTextChangedListener(object: TextWatcher{
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        }
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            keteranganList[indexSpmItem] = p0.toString()
//                            Toast.makeText(this@PeriksaDetailActivity, "$isianUser", Toast.LENGTH_SHORT).show()
                        }
                        override fun afterTextChanged(p0: Editable?) {
                            // nothing to do
                        }

                    })
                }

                var subitemState = -1 // belum memilih centang atau silang
                val subitemSPM = inflater.inflate(R.layout.subitem_spm, null, false) as LinearLayout
                subitemSPM.findViewById<TextView>(R.id.tv_subitem_spm_nama).text = subitem

                val ivCentang = subitemSPM.findViewById<ImageView>(R.id.iv_centang_subitem)
                val ivSilang = subitemSPM.findViewById<ImageView>(R.id.iv_silang_subitem)

                ivCentang.setOnClickListener {
                    if(subitemState != 1){
                        ivCentang.setImageResource(R.drawable.vtrue)
                        ivSilang.setImageResource(R.drawable.xnull)
                        isianUser[indexSpmItem][indexSubitem] = 1
//                        Toast.makeText(this, "$isianUser", Toast.LENGTH_SHORT).show()
                        subitemState = 1
                    }
                }

                ivSilang.setOnClickListener {
                    if(subitemState != 0){
                        ivSilang.setImageResource(R.drawable.xtrue)
                        ivCentang.setImageResource(R.drawable.vnull)
                        isianUser[indexSpmItem][indexSubitem] = 0
//                        Toast.makeText(this, "$isianUser", Toast.LENGTH_SHORT).show()
                        subitemState = 0
                    }
                }

                val paramsSubItem = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 36
                }
                subitemSPM.layoutParams = paramsSubItem
                val linearLayoutSubitem = itemSPM.findViewById<LinearLayout>(R.id.linear_layout_subitem_spm_item)
                linearLayoutSubitem.addView(subitemSPM)
            }
        } // end of for loop parent

        binding.btmSimpan.setOnClickListener {
            try{
                simpanBaru()
            } catch (e: Exception){
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // get nip data
        db.collection("users").document(firebaseAuth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { document ->
                if(document.exists()){
                    if(document.contains("nip")){
                        nip = document.getString("nip").toString()
                    }
                }
                hideLoading()
            }

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
        // mengaktifkan user interactions
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun uploadImage(imageUri: Uri, imageName: String) {
        val riversRef: StorageReference = storageRef.child("images/$imageName")
        riversRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@PeriksaDetailActivity, "${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun simpanBaru(){
        val docRef = db
            .collection("pemeriksaan-new")

        showLoading()
        val hashMap = hashMapOf<String, Any>()
        hashMap["nama"] = namaStasiunPerjalanan
        hashMap["jenis"] = jenisSPM
        hashMap["aspek"] = namaSPM
        hashMap["tanggal"] = tanggalToday
        hashMap["pemeriksa"] = firebaseAuth.currentUser?.displayName.toString()
        hashMap["nip"] = nip


        // flatten mengubah array 2d menjadi 1d karena kita ingin ambil semua subitem maka kita jadikan 1d
        val totalPengisian = isianUser.flatten().size
        val totalCentang = isianUser.flatten().filter { it == 1 }.size
        val desimal = (totalCentang.toFloat() / totalPengisian.toFloat())

        val persentase = desimal * 100

        // Locale US biar koma jadi titik (indo : 73,33 | US 73.33) penting karena kalau koma, stringnya harus diganti titik dulu
        // agar bisa dikonversi ke tipe data number seperti float
        formattedPersentase = String.format(Locale.US,"%.2f", persentase)
        hashMap["persentase"] = formattedPersentase

        hashMap["items"] = spmItems.mapIndexed { index, (key, values) ->
            mapOf(
                "nama" to key,
                "gambar" to namaGambarList[index],
                "keterangan" to keteranganList[index],
                "subitems" to values.mapIndexed { subIndex, value ->
                    mapOf("deskripsi" to value, "status" to isianUser[index][subIndex])
                }
            )
        }

        if(!keteranganList.contains("") && !isianUser.flatten().contains(-1)){
            // tambahkan dokumen
            docRef.document("$tanggalToday-pemeriksaan-$namaStasiunPerjalanan-$namaSPM").set(hashMap)
                .addOnSuccessListener {
                    hideLoading()
                    var daftar = ""

                    var nomor = 1
                    isianUser.forEachIndexed { index, ints ->
                        if(ints.contains(0)){
                            daftar += "$nomor. ${namaItemList.get(index)}\n"
                            nomor++
                        }
                    }

                    val itemTidakMemenuhi = if(formattedPersentase.equals("100.00")){
                        ""
                    } else {
                        "\nItem yang tidak memenuhi SPM:\n\n$daftar"
                    }

                    val dialog = AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("Hasil Pemeriksaan")
                        .setMessage("$formattedPersentase% memenuhi SPM" + itemTidakMemenuhi)
                        .setPositiveButton("OK") { dialog, which ->
                            finish()
                        }
                        .create()
                    dialog.show()
                }.addOnFailureListener {
                    hideLoading()
                }
            // upload semua gambar
            for (i in 0 until spmItems.size){
                for (j in 0 until 3){
                    if(uriList[i][j] != null){
                        uploadImage(uriList[i][j]!!, namaGambarList[i][j].toString())
                    }
                }
            }
            Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        } else {
            hideLoading()
            Toast.makeText(this, "Harap mengisi form yang masih kosong", Toast.LENGTH_SHORT).show()
        }

    }

}