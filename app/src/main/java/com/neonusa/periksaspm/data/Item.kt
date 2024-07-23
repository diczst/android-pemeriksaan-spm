package com.neonusa.periksaspm.data

import com.google.firebase.firestore.PropertyName

data class Item (
    var nama: String? = "",
    var gambar: MutableList<String>? = null,
    var keterangan: String? = "",
    var subitems: MutableList<Subitem>? = null
)