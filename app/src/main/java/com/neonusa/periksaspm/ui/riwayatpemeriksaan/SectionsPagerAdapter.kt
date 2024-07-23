package com.neonusa.periksaspm.ui.riwayatpemeriksaan

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.neonusa.periksaspm.R

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter internal constructor(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        val fragment = PlaceholderFragment()
        val bundle = Bundle()
        if (position == 0) {
            bundle.putString(PlaceholderFragment.ARG_TAB, PlaceholderFragment.TAB_SEMUA)
        } else if(position == 1) {
            bundle.putString(PlaceholderFragment.ARG_TAB, PlaceholderFragment.TAB_STASIUN)
        } else {
            bundle.putString(PlaceholderFragment.ARG_TAB, PlaceholderFragment.TAB_PERJALANAN)
        }
        fragment.arguments = bundle
        return fragment
    }

    override fun getItemCount(): Int {
        return 3
    }
}