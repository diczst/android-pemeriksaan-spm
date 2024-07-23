package com.neonusa.periksaspm.utils

object SPMDataFormGenerator {

    // stasiun
    val spmKeselamatanItems = listOf(
        "Alat Pemadam Kebakaran (APAR) 3kg di area tidak bertiket" to
                listOf("Masa Kadaluarsa", "Jarum Indikator Tekanan (Hijau)"),

        "Alat Pemadam Kebakaran (APAR) 10kg di area bertiket" to
                listOf("Masa Kadaluarsa", "Jarum Indikator Tekanan (Hijau)"),

        "Stiker Titik Kumpul Evakuasi" to
                listOf("Mudah Terlihat", "Jelas Terbaca"),

        "Petunjuk Jalur Evakuasi" to
                listOf("Mudah Terlihat", "Jelas Terbaca"),

        "Prosedur Evakuasi" to
                listOf("Mudah Terlihat", "Jelas Terbaca"),

        "Nomor Telepon Darurat (Emergency Call)" to
                listOf("Mudah Terlihat", "Jelas Terbaca"),

        "Pos Kesehatan" to
                listOf("Tersedia Fasilitas Obat-obatan", "Tersedia Petugas Paramedis", "Tersedia Fasilitas Kerja"),

        "Kursi Roda" to
                listOf("Tersedia", "Berfungsi"),

        "Tandu" to
                listOf("Tersedia", "Berfungsi"),

        "Tabung Oksigen dengan Berat Minimal 0,5kg" to
                listOf("Tersedia", "Berfungsi"),

        "Lampu Penerangan" to
                listOf("Intensitas Cahaya >= 200 Lux"),

        "Celah (Gap) Antara Tepi Peron dengan Badan Kereta" to
                listOf("Maksimal 20cm"),

        "Selisih Ketinggian lantai peron stasiun dengan lantai kereta" to
                listOf("Maksimal 20cm"),

        "Lantai Peron" to
                listOf("Area Bebas dari Kegiatan Komersial","Tidak Licin", "Tidak Tergenang Air"),

        "Marka Petunjuk/Pembatas Antrean Naik/Turun Penumpang" to
                listOf("Tersedia"),

        "Guiding Block (Bagi Penumpang Tuna Netra)" to
                listOf("Tersedia"),

        "Safety line dari tepi peron atau PSD (Platform Screen Door)" to
                listOf("Tersedia", "Berfungsi"),

        "Kanopi Peron Stasiun" to
                listOf("Tersedia"),

        "Papan Titik Kumpul Evakuasi (Assembly Point)" to
                listOf("Mudah Terlihat", "Jelas Terbaca"),
    )

    val spmKeamananItems = listOf(
        "CCTV" to
                listOf("Tersedia", "Berfungsi"),

        "Petugas Keamanan Minimal 1 Orang" to
                listOf("Tersedia"),

        "Stiker Berisi Telpon Polsek/Polres" to
                listOf("Tersedia"),

        "Lampu Penerangan" to
                listOf("Intensitas Cahaya ≥ 200 lux"),
    )

    val spmKehandalanItems = listOf(
        "Layanan Penjualan Tiket" to
                listOf("Tersedia Loket Tiket dan/atau Vending Machine", "Tersedia Papan Informasi Tata Cara Pembelian dan Top-Up", "Pelayanan Tiket Manual Maksimum 180 Detik per Penumpang"),

        "Tersedia Peta Jadwal Operasi" to
                listOf("Area Bertiket", "Area Tidak Bertiket"),

        "Peta Jaringan Pelayanan Kereta Api" to
                listOf("Area Bertiket", "Area Tidak Bertiket"),

        "Display dan/atau Running Text" to
                listOf("Berfungsi"),

        "Pengeras Suara" to
                listOf("Berfungsi"),
    )

    val spmKenyamananItems = listOf(
        "Ruang Tunggu" to
                listOf("Untuk 1 (satu) Orang Minimum 0,6 m2", "Dilengkapi Tempat Duduk Yang Memadai", "Dilengkapi Tempat Duduk Prioritas"),
        "Ruang Boarding" to
                listOf("Untuk 1 (satu) Orang Minimum 0,6 m2", "Dilengkapi Tempat Duduk Prioritas", "Area Bersih"),
        "Toilet Pria" to
                listOf("Kesesuaian Jumlah WC (1)", "Kesesuaian Jumlah Wastafel (1)", "Tersedia Signage/Penanda", "Area Bersih", "Tidak Berbau dan Sirkulasi Udara Baik", "Tidak Ada Genangan Air", "Lampu Penerangan Minimal 150lux"),
        "Toilet Wanita" to
                listOf("Kesesuaian Jumlah WC (1)", "Kesesuaian Jumlah Wastafel (1)", "Tersedia Signage/Penanda", "Area Bersih", "Tidak Berbau dan Sirkulasi Udara Baik", "Tidak Ada Genangan Air", "Lampu Penerangan Minimal 150lux"),
        "1 Toilet Penumpang Difable" to
                listOf("Kesesuaian Jumlah WC (1) Dilengkapi Handrail", "Kesesuaian Jumlah Wastafel (1)", "Tersedia Signage/Penanda", "Area Bersih", "Tidak Berbau dan Sirkulasi Udara Baik", "Tidak Ada Genangan Air", "Lampu Penerangan Minimal 150lux"),
        "Mushola" to
                listOf("Dapat Memuat 6 Orang (Pria atau Wanita)", "Suhu Udara Maksimal 27 derajat C", "Area Bersih dan Tidak Berbau", "Tersedia Kursi untuk Difable"),
        "Lampu Penerangan dengan Intensitas Cahaya Minimal 200 Lux" to
                listOf("Area/Ruang Tunggu Penumpang", "Area Peron"),
        "Fasilitas Pengatur Sirkulasi Udara di Ruang Tunggu Tertutup" to
                listOf("Suhu Udara Maksimal 27 derajat C"),
        "Kebersihan Stasiun" to
                listOf("Area Bersih"),
        "Tempat Sampah dengan 2 Pembagian (Organik dan Anorganik)" to
                listOf("Tersedia"),
        "Himbauan Larangan Merokok" to
                listOf("Tersedia"),
    )

    val spmKemudahanItems = listOf(
        "Denah/Layout Stasiun" to
                listOf("Informasi Visual atau Passenger Information System", "Informasi Audio (Public Address System)"),
        "Nama Stasiun" to
                listOf("Informasi Visual atau Passenger Information System", "Informasi Audio (Public Address System)"),
        "Jadwal Operasi KA" to
                listOf("Informasi Visual atau Passenger Information System", "Informasi Audio (Public Address System)"),
        "Tarif KA" to
                listOf("Informasi Visual atau Passenger Information System", "Informasi Audio (Public Address System)"),
        "Arah Jalur Evakuasi Kondisi Darurat" to
                listOf("Informasi Visual atau Passenger Information System", "Informasi Audio (Public Address System)"),
        "Informasi Gangguan Perjalanan KA Diumumkan Maksimal 30 Menit setelah Terjadi Gangguan" to
                listOf("Tersedia"),
        "Papan Penunjuk Arah dan Lokasi Angkutan Lanjutan" to
                listOf("Mudah Terlihat", "Jelas Terbaca"),
        "Fasilitas Layanan Penumpang" to
                listOf("Tersedia Tempat dan 1 Meja Kerja", "Tersedia 1 Orang Petugas dan Memiliki Kecakapan Bahasa Inggris"),
        "Tempat Untuk Parkir Kendaraan Roda 4 dan Roda 2" to
                listOf("Tersedia"),
        "Penanda Penunjuk Arah" to
                listOf("Tersedia"),
    )

    val spmKesetaraanItems = listOf(
        "Fasilitas Bagi Penumpang Difable" to
                listOf("Tersedia Tempat Duduk Prioritas", "Tersedia Ramp Kemiringan Maksimal 10 derajat", "Tersedia Hand Rail Ketinggian 65-80 cm", "Tersedia Guiding Block", "Tersedia Lift dan/atau Escalator Jika Lantai Lebih dari 1"),
        "Loket Penyandang Disabilitas" to
                listOf("Tersedia Loket dan/atau Vending Machine Khusus", "Tinggi Loket Dapat Dijangkau Kursi Roda"),
        "Ruang Ibu Menyusui" to
                listOf("Tersedia")
    )

    // sarana / perjalanan
    val spmKeselamatanItemsPerjalanan = listOf(
        "Alat Pemadam Kebakaran (APAR) Ukuran 3kg 1 Unit di Setiap Kereta" to
                listOf("Masa Kadaluarsa", "Jarum Indikator Tekanan (Hijau)"),
        "Tombol Alarm Kondisi Darurat" to
                listOf("Berfungsi"),
        "Alat Pemecah Kaca" to
                listOf("Ketersediaan Alat", "Terdapat Stiker Informasi Alat Pemecah Kaca", "Terdapat Stiker Informasi Jendela Darurat"),
        "Prosedur Evakuasi" to
                listOf("Visual", "Audio"),
        "Tombol/tuas pembuka pintu otomatis" to
        listOf("Mudah terlihat", "Jelas terbaca"),
        "Perlengkapan P3K" to
                listOf("Tersedia", "Mudah Terlihat", "Terjangkau"),
        "Pintu Keluar atau Masuk Penumpang Secara Manual/Otomatis" to
                listOf("Berfungsi")
    )

    val spmKeamananItemsPerjalanan = listOf(
        "CCTV (Minimal 2 Per Rangkaian KA)" to
                listOf("Tersedia"),
        "Petugas Keamanan (Minimal 1 Per 6 Kereta)" to
                listOf("Tersedia"),
        "Stiker Berisi Nomor Telepon dan/atau SMS Pengaduan (Call Center)" to
                listOf("Jumlah Kesesuaian Stiker (Minimal 4", "Mudah Terlihat"),
        "Lampu Penerangan" to
                listOf("Intensitas Cahaya Minimal 200Lux")
    )

    val spmKehandalanItemsPerjalanan = listOf(
        "Ketepatan Jadwal Kereta Api" to
                listOf("Tersedia Informasi Ketepatan/Kepastian Waktu Keberangkatan dan Kedatangan KA", "Keterlambatan Maksimal 20% Dari Waktu Tempu")
    )

    val spmKenyamananItemsPerjalanan = listOf(
        "Tempat Duduk Dengan Konstruksi Tetap yang Mempunyai Sandaran" to
                listOf("Minimal 20% Dari Spektek Kereta", "Ruang Berdiri Maksimum 1m2 Untuk 6 Orang"),
        "Fasilitas Pengatur Sirkulasi Udara" to
                listOf("Suhu Udara Maksimal 27°C"),
        "Fasilitas Pegangan Penumpang (Hand Rail dan Hand Grip) yang Disediakan Untuk Penumpang Berdiri" to
                listOf("Tersedia"),
        "Rak Bagasi" to
                listOf("Tersedia"),
        "Kebersihan" to
                listOf("Tersedia Petugas Kebersihan", "Kondisi Bersih")
    )

    val spmKemudahanItemsPerjalanan = listOf(
        "Informasi Stasiun yang Akan Disinggahi/Dilewati Secara Berurutan" to
                listOf("Tersedia Informasi Visual", "Tersedia Informasi Audio"),
        "Informasi Gangguan Perjalanan KA Diumumkan Maksimal 30 Menit setelah Terjadi Gangguan" to
                listOf("Tersedia"),
        "Nama/Relasi Kereta Api dan Nomor Operasi Kereta" to
                listOf("Nama KA/Relasi KA di Bagian Luar Sisi Kiri dan Kanan", "Display Nama Relasi/Nomor Operasi Kereta Api Perkotaan Dipasang di Bagian Muka Kereta"),
        "Informasi Pelayanan" to
                listOf("Informasi Visual atau Passenger Information System", "Informasi Audio (Public Address System)"),
        "Kadar Gelap Kaca" to
                listOf("Tersedia Kadar Gelap Kaca Film Maksimal 40%")
    )

    val spmKesetaraanItemsPerjalanan = listOf(
        "Fasilitas Bagi Penumpang Dengan Kebutuhan Khusus" to
                listOf("Jumlah Tempat Duduk Prioritas Sesuai (12)", "Jumlah Stiker Informasi Tempat Duduk Prioritas Sesuai (12)"),
        "Tempat Khusus Kursi Roda yang Diberi Stiker/Penanda Khusus Kursi Roda" to
                listOf("Tersedia"),
    )

    fun contohLogikaKodeListMap(){
        val daftarHewan = listOf(
            "hewan air" to listOf("hiu", "paus", "udang"),
            "hewan udara" to listOf("elang", "gagak")
        )
        // Menampilkan daftar hewan
        for ((jenisHewan, hewan) in daftarHewan) {
            println(jenisHewan)
            for (namaHewan in hewan) {
                println("- $namaHewan")
            }
        }
    }

}