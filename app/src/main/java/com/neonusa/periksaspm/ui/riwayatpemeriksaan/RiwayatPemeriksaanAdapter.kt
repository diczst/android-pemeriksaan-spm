package com.neonusa.periksaspm.ui.riwayatpemeriksaan

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.neonusa.periksaspm.R
import com.neonusa.periksaspm.data.Pemeriksaan
import com.neonusa.periksaspm.databinding.ItemRiwayatPemeriksaanBinding

class RiwayatPemeriksaanAdapter(private val onClickListener: (Pemeriksaan, ListAdapterClickSource) -> Unit) : ListAdapter<Pemeriksaan, RiwayatPemeriksaanAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRiwayatPemeriksaanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pemeriksaan = getItem(position)
        holder.bind(pemeriksaan)

        holder.binding.ivDelete.setOnClickListener {
            onClickListener(pemeriksaan, ListAdapterClickSource.DELETE)
        }

        // when item clicked
        holder.binding.root.setOnClickListener {
            onClickListener(pemeriksaan, ListAdapterClickSource.ROOT)
//            val intent = Intent( it.context, DetailActivity::class.java)
//            intent.putExtra(DetailActivity.EXTRA_PUISI, puisi)
//            it.context.startActivity(intent)
        }

    }

    class MyViewHolder(val binding: ItemRiwayatPemeriksaanBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(pemeriksaan: Pemeriksaan) {
            binding.tvTanggal.text = pemeriksaan.tanggal
            binding.tvNamaStasiunPerjalanan.text = pemeriksaan.nama
            binding.tvNamaPemeriksa.text = "Pemeriksa : " + pemeriksaan.pemeriksa

            if(pemeriksaan.jenis.equals("SPM Stasiun")){
                binding.ivJenis.setImageResource(R.drawable.stasiun)
            } else {
                binding.ivJenis.setImageResource(R.drawable.perjalanan)
            }

//            itemView.setOnClickListener {
////                val intent = Intent( itemView.context, DetailActivity::class.java)
////                intent.putExtra(DetailActivity.EXTRA_PUISI, puisi)
////                itemView.context.startActivity(intent)
//            }
        }
    }


    fun removeItem(position: Int) {
        val currentList = currentList.toMutableList()
        currentList.removeAt(position)
        submitList(currentList)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Pemeriksaan> =
            object : DiffUtil.ItemCallback<Pemeriksaan>() {
                override fun areItemsTheSame(oldItem: Pemeriksaan, newItem: Pemeriksaan): Boolean {
//                    return oldItem.id == newItem.id
                    return false
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: Pemeriksaan, newItem: Pemeriksaan): Boolean {
                    return oldItem == newItem
                }
            }
    }

    enum class ListAdapterClickSource {
        ROOT,
        DELETE
    }
}