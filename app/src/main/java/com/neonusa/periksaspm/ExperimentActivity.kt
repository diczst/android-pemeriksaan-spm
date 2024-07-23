package com.neonusa.periksaspm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.storage.FirebaseStorage
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import com.neonusa.periksaspm.databinding.ActivityExperimentBinding
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ExperimentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExperimentBinding
    // Global list to store Bitmaps

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExperimentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedImages = listOf("Stasiun Demang-13-06-2024-Keamanan-0.jpg", "Stasiun Demang-13-06-2024-Keamanan-1.jpg", "Stasiun Demang-13-06-2024-Keamanan-2.jpg")

        downloadImagesFiltered(selectedImages) { bitmapList ->
            for (bitmap in bitmapList) {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val imageData = stream.toByteArray()
                val image = Image.getInstance(imageData)
            }
        }
    }


    // versi difilter
    private fun downloadImagesFiltered(selectedImageNames: List<String>, onComplete: (MutableList<Bitmap>) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images")
        var downloadCount = 0
        val bitmapList = mutableListOf<Bitmap>()
        val totalImages = selectedImageNames.size

        for (imageName in selectedImageNames) {
            val imageRef = storageRef.child(imageName)
            val fileSementara = File.createTempFile("temp", "jpg")
            imageRef.getFile(fileSementara).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(fileSementara.absolutePath)
                bitmapList.add(bitmap)
                downloadCount++
                // Jika semua gambar yang dipilih telah diunduh, panggil fungsi onComplete
                if (downloadCount == totalImages) {
                    onComplete(bitmapList)
                }
            }.addOnFailureListener {
                it.printStackTrace()
            }
        }

        // Jika tidak ada gambar yang dipilih untuk diunduh, panggil fungsi onComplete
        if (selectedImageNames.isEmpty()) {
            onComplete(bitmapList)
        }
    }

    // versi gak difilter (semua gambar di database)
    private fun downloadImages(onComplete: (MutableList<Bitmap>) -> Unit){
        val storageRef = FirebaseStorage.getInstance().reference.child("images")
        storageRef.listAll().addOnSuccessListener { listResult ->
            val totalImages = listResult.items.size
            var downloadCount = 0
            val bitmapList = mutableListOf<Bitmap>()
            for (item in listResult.items) {
                val fileSementara = File.createTempFile("temp", "jpg")
                item.getFile(fileSementara).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(fileSementara.absolutePath)
                    bitmapList.add(bitmap)
                    downloadCount++

                    // Jika semua gambar telah diunduh, panggil fungsi onComplete
                    if (downloadCount == totalImages) {
                        onComplete(bitmapList)
                    }
                }.addOnFailureListener {
                    it.printStackTrace()
                }
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

}
