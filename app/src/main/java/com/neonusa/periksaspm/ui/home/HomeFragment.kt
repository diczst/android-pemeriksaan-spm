package com.neonusa.periksaspm.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.neonusa.periksaspm.ExperimentActivity
import com.neonusa.periksaspm.R
import com.neonusa.periksaspm.databinding.FragmentHomeBinding
import com.neonusa.periksaspm.ui.riwayatpemeriksaan.RiwayatPemeriksaanActivity
import com.neonusa.periksaspm.utils.Helper

class HomeFragment : Fragment() {
    val user = FirebaseAuth.getInstance().currentUser
    val db = Firebase.firestore

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvHi.text = "Hi " + user?.displayName

        binding.tvTanggalPerjalanan.text = Helper.getTanggalToday()
        binding.tvTanggalStasiun.text = Helper.getTanggalToday()

        binding.btnPeriksaSpmStasiun.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_spm_stasiun)
        }

        binding.btnPeriksaSpmPerjalanan.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_spm_perjalanan)
        }

        binding.btnRiwayat.setOnClickListener {
            startActivity(Intent(requireActivity(), RiwayatPemeriksaanActivity::class.java))
            // ke eksperiman
//            startActivity(Intent(requireActivity(), ExperimentActivity::class.java))
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}