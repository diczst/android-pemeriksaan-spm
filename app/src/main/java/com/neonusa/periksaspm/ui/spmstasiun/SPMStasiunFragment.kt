package com.neonusa.periksaspm.ui.spmstasiun

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
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
import com.neonusa.periksaspm.databinding.FragmentSpmstasiunBinding
import com.neonusa.periksaspm.ui.periksa.PeriksaActivity
import com.neonusa.periksaspm.utils.Helper

class SPMStasiunFragment : Fragment() {

    // note
    // this is part of fragment in bottom nav
    // but the bottom nav menu for this page is remove, but the page not and still can be accesses from home fragment

    private var _binding: FragmentSpmstasiunBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val db = Firebase.firestore
    var firebaseAuth = FirebaseAuth.getInstance()
    val docRef = db
        .collection("pemeriksaan-new")

    private val listJenisPelayanan = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(SPMStasiunViewModel::class.java)

        _binding = FragmentSpmstasiunBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvTanggal.text = Helper.getTanggalToday()

        val items = arrayOf("Nama Stasiun",
            "Stasiun DJKA",
            "Stasiun Jakabaring",
            "Stasiun Polresta",
            "Stasiun Ampera",
            "Stasiun Cinde",
            "Stasiun Dishub",
            "Stasiun Bumi Sriwijaya",
            "Stasiun Demang",
            "Stasiun Garuda Dempo",
            "Stasiun RSUD",
            "Stasiun Punti Kayu",
            "Stasiun Asrama Haji",
            "Stasiun Bandara")
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

        binding.spinnerStasiun.adapter = adapter

        db.collection("users").document(firebaseAuth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { document ->
                binding.btnNextSpmStasiun.setOnClickListener {
                    if(document.exists()){
                        if(!binding.spinnerStasiun.selectedItem.toString().equals("Nama Stasiun")){
                            getData()
                        } else {
                            Toast.makeText(requireActivity(), "Harap memilih stasiun terlebih dahulu", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireActivity(), "Harap mengisi NIP sebelum melakukan pemeriksaan", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_spm_stasiun_to_navigation_home)
        }

        return root
    }

    fun getData(){
        val query = docRef
            .whereEqualTo("nama", binding.spinnerStasiun.selectedItem.toString())
            .whereEqualTo("tanggal", Helper.getTanggalTodayAngkaVer())
        query.get()
            .addOnSuccessListener {documents ->
                listJenisPelayanan.clear()
                for (document in documents) {
                    val pemeriksaan = document.toObject<Pemeriksaan>()
                    listJenisPelayanan.add(pemeriksaan.aspek.toString())
                }
                if(listJenisPelayanan.size == 6){
                    Toast.makeText(requireActivity(), "Kamu sudah mengisi data pemeriksaan SPM ${binding.spinnerStasiun.selectedItem}", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(requireActivity(), PeriksaActivity::class.java)
                    intent.putExtra(PeriksaActivity.JENIS_SPM,"SPM Stasiun")
                    intent.putExtra(PeriksaActivity.NAMA_PERJALANAN_STASIUN, binding.spinnerStasiun.selectedItem.toString())
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