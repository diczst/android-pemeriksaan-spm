package com.neonusa.periksaspm.ui.riwayatpemeriksaan

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import com.google.android.material.tabs.TabLayoutMediator
import com.neonusa.periksaspm.R
import com.neonusa.periksaspm.ui.riwayatpemeriksaan.SectionsPagerAdapter
import com.neonusa.periksaspm.databinding.ActivityRiwayatPemeriksaanBinding

class RiwayatPemeriksaanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRiwayatPemeriksaanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatPemeriksaanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.viewPager.adapter = sectionsPagerAdapter
        TabLayoutMediator(
            binding.tabs, binding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.tab_text_1, R.string.tab_text_2,R.string.tab_text_3)
    }
}