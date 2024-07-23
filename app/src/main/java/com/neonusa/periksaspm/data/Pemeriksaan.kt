package com.neonusa.periksaspm.data

data class Pemeriksaan(
    var nama: String? = "",
    var jenis:String? = "",
    var tanggal:String? = "",
    var aspek: String? = "",
    var persentase: String? = "",
    var pemeriksa: String? = "",
    var nip: String? = "", // baru
    var items: MutableList<Item>? = null
    )
