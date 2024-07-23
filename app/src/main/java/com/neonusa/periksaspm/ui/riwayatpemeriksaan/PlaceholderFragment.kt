package com.neonusa.periksaspm.ui.riwayatpemeriksaan

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Image
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.neonusa.periksaspm.data.Pemeriksaan
import com.neonusa.periksaspm.databinding.FragmentRiwayatPemeriksaanBinding
import com.neonusa.periksaspm.utils.Helper.convertDate
import com.neonusa.periksaspm.utils.Helper.parseDate
import com.neonusa.periksaspm.utils.PDFUtility.addEmptyLine
import com.neonusa.periksaspm.utils.PDFUtility.addHeader
import com.neonusa.periksaspm.utils.PDFUtility.createDataTableKesimpulan
import com.neonusa.periksaspm.utils.PDFUtility.createDataTableSPM
import com.neonusa.periksaspm.utils.PDFUtility.createTableTidakMemenuhi
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter

// todo
// 1. waktu data di load, maka load semua  data image dari firebase
// 2. simpan ke dalam sebuah list gambar untuk dipake imagepdf

class PlaceholderFragment : Fragment() {
    private var _binding: FragmentRiwayatPemeriksaanBinding? = null
    private var tabName: String? = null
    private lateinit var pemeriksaanAdapter: RiwayatPemeriksaanAdapter

    var firebaseAuth = FirebaseAuth.getInstance()

    // for pdf
    var isPortrait = false

    private val pemeriksaanList = mutableListOf<Pemeriksaan>()
    val db = Firebase.firestore

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRiwayatPemeriksaanBinding.inflate(inflater, container, false)
        val root = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabName = arguments?.getString(ARG_TAB)

        showLoading(false,false)

        val docRef = db
            .collection("pemeriksaan-new")

        pemeriksaanAdapter = RiwayatPemeriksaanAdapter{ riwayatPemeriksaan, clickSource ->
            if (clickSource == RiwayatPemeriksaanAdapter.ListAdapterClickSource.ROOT) {
                try {
                    buatPdf(riwayatPemeriksaan.nama!!, riwayatPemeriksaan.tanggal!!, riwayatPemeriksaan.pemeriksa!!, riwayatPemeriksaan.nip!!)
                } catch (e: Exception){
                    Toast.makeText(requireActivity(), "${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else if(clickSource == RiwayatPemeriksaanAdapter.ListAdapterClickSource.DELETE){
                val dialog = AlertDialog.Builder(requireActivity())
                    .setCancelable(false)
                    .setTitle("Hapus Riwayat Pemeriksaan")
                    .setMessage("Apakah kamu yakin ingin menghapus riwayat pemeriksaan ini?")
                    .setPositiveButton("Ya") { dialog, which ->
                        dialog.dismiss()
                        deleteData(riwayatPemeriksaan.nama!!, riwayatPemeriksaan.tanggal!!, riwayatPemeriksaan)
                    }.setNegativeButton("Tidak"){dialog, which ->
                        dialog.dismiss()
                    }
                    .create()
                dialog.show()
            }
        }


        docRef.get()
            .addOnSuccessListener { documents ->
                pemeriksaanList.clear()
                pemeriksaanAdapter.submitList(null)

                val uniqueTanggalJenisSet = mutableSetOf<Triple<String, String, String>>()
                val uniquePemeriksaanList = mutableListOf<Pemeriksaan>()
                for (document in documents) {
                    val pemeriksaan = document.toObject<Pemeriksaan>()

                    // jika 3 property data di beberapa dokumen sama maka ambil satu dokumen saja untuk ditampilkan
                    val uniquePair = Triple(pemeriksaan.tanggal!!, pemeriksaan.jenis!!, pemeriksaan.nama!!)

                    // Check if the date is unique and add it to the set
                    if (uniqueTanggalJenisSet.add(uniquePair)) {
                        uniquePemeriksaanList.add(pemeriksaan)
                    }

                    // nambahkan data yang ga unik untuk ke table dan pemeriksaan
                    pemeriksaanList.add(pemeriksaan)
//                    Toast.makeText(requireActivity(), "${document.id} => $user", Toast.LENGTH_SHORT).show()
                }

                // sudah berisi unique
                val pemeriksaanListFiltered = if(tabName.equals(TAB_STASIUN)) {
                    uniquePemeriksaanList.filter { it.jenis.equals(TAB_STASIUN) }.toMutableList()
                } else if(tabName.equals(TAB_PERJALANAN)) {
                    uniquePemeriksaanList.filter { it.jenis.equals(TAB_PERJALANAN) }.toMutableList()
                } else {
                    uniquePemeriksaanList.toMutableList()
                }

                val pemeriksaanAspekLengkapList = mutableListOf<Pemeriksaan>()
                pemeriksaanListFiltered.forEach { pemeriksaan ->
                    val filteredPemeriksaan = pemeriksaanList.filter { (it.nama.equals(pemeriksaan.nama)) && (it.tanggal.equals(pemeriksaan.tanggal)) }
                    if(filteredPemeriksaan.size == 6){
                        pemeriksaanAspekLengkapList.add(pemeriksaan)
                    }
                }

                // urutkan berdasarkan tanggal
                //==================================================================
                // Comparator berdasarkan properti date
                val comparator = Comparator<Pemeriksaan> { a, b ->

                    val dateA = parseDate(a.tanggal.toString())
                    val dateB = parseDate(b.tanggal.toString())

                    // Urutkan berdasarkan tahun, bulan, dan hari
                    when {
                        dateA.first != dateB.first -> dateB.first.compareTo(dateA.first) // Bandingkan tahun
                        dateA.second != dateB.second -> dateB.second.compareTo(dateA.second) // Bandingkan bulan
                        else -> dateB.third.compareTo(dateA.third) // Bandingkan hari
                    }
                }

                // Mengurutkan berdasarkan tanggal (terbaru)
                val sortedPemeriksaanAspekLengkap = pemeriksaanAspekLengkapList.sortedWith(comparator)

                //==================================================================

                pemeriksaanAdapter.submitList(sortedPemeriksaanAspekLengkap)
                hideLoading()
            }
            .addOnFailureListener { exception ->
                hideLoading()
                Toast.makeText(requireActivity(), "${exception.message}", Toast.LENGTH_SHORT).show()
            }

        binding?.rvRiwayatPemeriksaan?.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = pemeriksaanAdapter
        }
    }

    private fun buatPdf(nama: String, tanggal: String, pemeriksa: String, nip: String){
        // requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) : -1 jika belum ada izin, 0 jika sudah ada
        // packageManager.PermissionGranted = 0
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            // android 11 keatas tidak perlu minta izin
            generateDoc(nama, tanggal, pemeriksa, nip)
//            Toast.makeText(requireActivity(), "hello from 11 keatas", Toast.LENGTH_SHORT).show()
        } else {
            // minta izin dulu
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                // jika sudah ada izin storage
                generateDoc(nama, tanggal, pemeriksa, nip)
            }
        }

    }

    private fun generateDoc(nama: String, tanggal: String, pemeriksa: String, nip: String){
        showLoading(true, true)
        val filePath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/" + "pemeriksaan-$nama-$tanggal" + ".pdf"

        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString())
        if (!folder.exists()) {
            folder.mkdirs() // Buat folder jika belum ada
        }

        val document = Document()
        document.setMargins(56f, 56f, 56f, 56f)
        document.setPageSize(if (isPortrait) PageSize.A4 else PageSize.A4.rotate())

        val writer = PdfWriter.getInstance(document, FileOutputStream(filePath))
        writer.setFullCompression()
//        writer.pageEvent = PageNumeration()

        document.open()
        // header
//        addHeader(requireContext(),document,"Pemeriksaan Bulanan SPM STASIUN", convertDate("16-06-2024"))
//        addEmptyLine(document, 1)

//        / Membuat tabel dengan dua kolom
        val table = PdfPTable(2)
        table.widthPercentage = 50f
        val widths = floatArrayOf(1f, 3f)
        table.setWidths(widths)
        table.horizontalAlignment = Element.ALIGN_LEFT

        // Menambahkan baris pertama
        var cell = PdfPCell(Paragraph("Nama"))
        cell.border = PdfPCell.NO_BORDER
        table.addCell(cell)

        cell = PdfPCell(Paragraph(":  $nama"))
        cell.border = PdfPCell.NO_BORDER
        table.addCell(cell)

        // Menambahkan baris kedua
        cell = PdfPCell(Paragraph("Tanggal"))
        cell.border = PdfPCell.NO_BORDER
        table.addCell(cell)

        cell = PdfPCell(Paragraph(":  ${convertDate(tanggal)}"))
        cell.border = PdfPCell.NO_BORDER
        table.addCell(cell)

        // baris ketiga
        cell = PdfPCell(Paragraph("Pemeriksa"))
        cell.border = PdfPCell.NO_BORDER
        table.addCell(cell)

        cell = PdfPCell(Paragraph(":   $pemeriksa"))
        cell.border = PdfPCell.NO_BORDER
        table.addCell(cell)

        // baris keempat
        cell = PdfPCell(Paragraph("NIP"))
        cell.border = PdfPCell.NO_BORDER
        table.addCell(cell)

        cell = PdfPCell(Paragraph(":  $nip"))
        cell.border = PdfPCell.NO_BORDER
        table.addCell(cell)

        // Menambahkan tabel ke dokumen
        document.add(table)

        addEmptyLine(document, 1)

        // ini kumpulan data pengisian satu stasiun/perjalanan (semua aspek) yang disatukan
        val filteredPemeriksaan = pemeriksaanList.filter { (it.nama == nama) && (it.tanggal == tanggal) }.toMutableList()

        val imagesName = mutableListOf<String>()
        
        // 0 karena emang cuma satu 
        filteredPemeriksaan.forEach { 
            it.items!!.forEach {
                it.gambar!!.forEach {
                    if(!it.equals("")){
                        imagesName.add(it)
                    }
//                    imagesName.add(it)
                }
            }
        }

        // mengurutkan data secara custom
        val referenceOrder = listOf("Keselamatan", "Keamanan", "Kehandalan", "Kenyamanan", "Kemudahan", "Kesetaraan")

        val comparator = Comparator<Pemeriksaan> { a, b ->
            val indexA = referenceOrder.indexOf(a.aspek)
            val indexB = referenceOrder.indexOf(b.aspek)
            indexA.compareTo(indexB)
        }

        val sortedList = filteredPemeriksaan.sortedWith(comparator).toMutableList()

        val listAspek = mutableListOf<String>()
        val listSubFasil = mutableListOf<String>()
        val listKeterangan = mutableListOf<String>()

        sortedList.forEach { pemeriksaan ->
            pemeriksaan.items!!.forEach { subFasil ->
                subFasil.subitems!!.filter { it.status == 0 }.forEach {
                    listAspek.add(pemeriksaan.aspek.toString())
                    listSubFasil.add(subFasil.nama.toString())
                    listKeterangan.add(subFasil.keterangan.toString())
                }
            }
        }

//        for((index, data) in listAspek.withIndex()){
//            Log.d("TAG", "${listAspek[index]} | ${listSubFasil[index]} | ${listKeterangan[index]}")
//        }

//        val keamanan = listOf(
//                listOf("keamanan", "ruang ibu menyusui", "ayam bakar mana"),
//                listOf("keamanan", "ruang anu", "butuh galon"),
//                listOf("keamanan", "ruang ibu", "gacor")
//            )
//
//            val kesetaraan = listOf(
//                listOf("kesetaraan", "ruang itu", "aman"),
//                listOf("kesetaraan", "ruang anu", "kurang ac"),
//                listOf("kesetaraan", "ruang ono", "kurang ajib")
//            )
//
//            val aspekList = listOf(
//                keamanan,
//                kesetaraan
//            )

        // List untuk menyimpan data yang terstruktur
        val aspekData = mutableMapOf<String, MutableSet<List<String>>>()

        // Menggabungkan data dari ketiga list
        for (i in listAspek.indices) {
            val aspek = listAspek[i]
            val subFasil = listSubFasil[i]
            val keterangan = listKeterangan[i]
            if (aspekData.containsKey(aspek)) {
                aspekData[aspek]?.add(listOf(aspek, subFasil, keterangan))
            } else {
                // mutable set biar data unik
                aspekData[aspek] = mutableSetOf(listOf(aspek, subFasil, keterangan))
            }
        }

        // Konversi map ke list
        val aspekList = aspekData.values.map { it.toList() }

        downloadImagesFiltered(imagesName) { bitmapList ->
//            val firstElements = bitmapList.map { it.first }
//            Log.d("TAG", "$firstElements")
            document.add(createDataTableSPM(bitmapList,requireContext(),sortedList))
            document.newPage()
            document.add(createDataTableKesimpulan(sortedList))
            addEmptyLine(document,2)
            document.add(createTableTidakMemenuhi(aspekList))
            document.close()
            writer.close()
            hideLoading()
            Toast.makeText(requireActivity(), "Dokumen tersimpan di folder unduhan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadImagesFiltered(selectedImageNames: List<String>, onComplete: (MutableList<Pair<String,Bitmap>>) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images")
        var downloadCount = 0
//        val bitmapList = mutableListOf<Bitmap>()

        // pakai pair karena ingin simpan nama (string0 beserta filenya (bitmap)
        val bitmapPairList = mutableListOf<Pair<String, Bitmap>>()

        val totalImages = selectedImageNames.size

        for (imageName in selectedImageNames) {
            val imageRef = storageRef.child(imageName)
            val fileSementara = File.createTempFile(imageName, "jpg")
            imageRef.getFile(fileSementara).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(fileSementara.absolutePath)
//                bitmapList.add(bitmap)
                val pair = Pair(imageName,bitmap)
                bitmapPairList.add(pair)
                downloadCount++
                // Jika semua gambar yang dipilih telah diunduh, panggil fungsi onComplete
                if (downloadCount == totalImages) {
                    onComplete(bitmapPairList)
                }
            }.addOnFailureListener {
                it.printStackTrace()
            }
        }

        // Jika tidak ada gambar yang dipilih untuk diunduh, panggil fungsi onComplete
        if (selectedImageNames.isEmpty()) {
            onComplete(bitmapPairList)
        }
    }


    private fun showLoading(showText: Boolean, showDarkOverlay: Boolean){
        binding.progressbarLoading.visibility = View.VISIBLE

        binding.darkOverlay.visibility = if(showDarkOverlay){
            View.VISIBLE
        } else {
            View.GONE
        }

        binding.tvLoading.visibility = if (showText){
            View.VISIBLE
        } else {
            View.GONE
        }

        // menonaktifkan user interactions
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun hideLoading(){
        binding.progressbarLoading.visibility = View.GONE
        binding.darkOverlay.visibility = View.GONE
        binding.tvLoading.visibility = View.GONE
        // mengaktifkan user interactions
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun deleteData(nama: String, tanggal: String, pemeriksaan: Pemeriksaan){
        showLoading(false,true)
        val collectionRef = db.collection("pemeriksaan-new") // Ganti dengan nama koleksi Anda
        // Query untuk mendapatkan dokumen yang sesuai
        val query = collectionRef
            .whereEqualTo("nama", nama)
            .whereEqualTo("tanggal", tanggal)

        // Eksekusi query
        query.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
//                            println("Dokumen ${document.id} berhasil dihapus.")
                        }
                }

                hideLoading()
                Toast.makeText(requireActivity(), "Riwayat pemeriksaan berhasil dihapus", Toast.LENGTH_SHORT).show()
                val position = pemeriksaanAdapter.currentList.indexOf(pemeriksaan)
                if (position != -1) {
                    pemeriksaanAdapter.removeItem(position)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireActivity(), "Riwayat pemeriksaan gagal dihapus", Toast.LENGTH_SHORT).show()
                hideLoading()
            }
    }
    

    companion object {
        const val ARG_TAB = "tab_name"
        const val TAB_SEMUA = "Semua"
        const val TAB_STASIUN = "SPM Stasiun"
        const val TAB_PERJALANAN = "SPM Perjalanan"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}