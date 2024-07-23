package com.neonusa.periksaspm.ui.spmperjalanan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.neonusa.periksaspm.R
import com.neonusa.periksaspm.data.Pemeriksaan
import com.neonusa.periksaspm.databinding.FragmentSpmperjalananBinding
import com.neonusa.periksaspm.ui.periksa.PeriksaActivity
import com.neonusa.periksaspm.ui.periksadetail.PeriksaDetailActivity
import com.neonusa.periksaspm.utils.Helper
import java.util.Locale

class SPMPerjalananFragment : Fragment() {
    private var _binding: FragmentSpmperjalananBinding? = null
    var firebaseAuth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    val docRef = db
        .collection("pemeriksaan-new")

    private val listJenisPelayanan = arrayListOf<String>()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(SPMPerjalananViewModel::class.java)

        _binding = FragmentSpmperjalananBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvTanggal.text = Helper.getTanggalToday()

        val items = arrayOf("Nomor Sarana",
            "Trainset 1",
            "Trainset 2",
            "Trainset 3",
            "Trainset 4",
            "Trainset 5",
            "Trainset 6",
            "Trainset 7",
            "Trainset 8")
        val adapter = object : ArrayAdapter<String>(requireActivity(), R.layout.custom_spinner_item, items){
            override fun isEnabled(position: Int): Boolean {
                // Hint pada posisi pertama, nonaktifkan klik untuk item ini
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) {
                    // Atur warna teks untuk hint agar terlihat berbeda
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(Color.BLACK)
                }
                return view
            }
        }

        binding.spinnerPerjalanan.adapter = adapter

        db.collection("users").document(firebaseAuth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { document ->
                binding.btnNextSpmPerjalanan.setOnClickListener {
                    if(document.exists()){
                        if(!binding.spinnerPerjalanan.selectedItem.toString().equals("Nomor Sarana")){
                            getData()
                        } else {
                            Toast.makeText(requireActivity(), "Harap memilih nomor sarana terlebih dahulu", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireActivity(), "Harap mengisi NIP sebelum melakukan pemeriksaan", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_spm_perjalanan_to_navigation_home)
        }


        return root
    }

    fun getData(){
        val query = docRef
            .whereEqualTo("nama", binding.spinnerPerjalanan.selectedItem.toString())
            .whereEqualTo("tanggal", Helper.getTanggalTodayAngkaVer())
        query.get()
            .addOnSuccessListener {documents ->
                listJenisPelayanan.clear()
                for (document in documents) {
                    val pemeriksaan = document.toObject<Pemeriksaan>()
                    listJenisPelayanan.add(pemeriksaan.aspek.toString())
                }
                if(listJenisPelayanan.size == 6){
                    Toast.makeText(requireActivity(), "Kamu sudah mengisi data pemeriksaan SPM ${binding.spinnerPerjalanan.selectedItem}", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(requireActivity(), PeriksaActivity::class.java)
                    intent.putExtra(PeriksaActivity.JENIS_SPM,"SPM Perjalanan")
                    intent.putExtra(PeriksaActivity.NAMA_PERJALANAN_STASIUN, binding.spinnerPerjalanan.selectedItem.toString())
                    requireActivity().startActivity(intent)
                }

            }.addOnFailureListener {
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}