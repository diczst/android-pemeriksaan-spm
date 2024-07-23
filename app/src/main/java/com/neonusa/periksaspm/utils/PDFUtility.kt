package com.neonusa.periksaspm.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.neonusa.periksaspm.R
import com.neonusa.periksaspm.data.Pemeriksaan
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale


object PDFUtility {
    // fonts
    private val FONT_TITLE = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
    val FONT_SUBTITLE = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL)

    // table fonts
    private val FONT_CELL_HEADER = Font(Font.FontFamily.HELVETICA, 11f, Font.BOLD)
    private val FONT_CELL = Font(Font.FontFamily.HELVETICA, 11f, Font.NORMAL)

    private val FONT_CELL_STATUS = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
    private val FONT_COLUMN = Font(Font.FontFamily.TIMES_ROMAN, 14f, Font.NORMAL)

    val storageRef = FirebaseStorage.getInstance().reference
    val downloadedFilesFromDB = mutableListOf<File>()

    fun addHeader(
        mContext: Context,
        document: Document,
        headerTitle: String,
        headerSubtitle: String
    ) {
        val table = PdfPTable(3)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(2f, 7f, 2f))
        table.defaultCell.border = PdfPCell.NO_BORDER
        table.defaultCell.setVerticalAlignment(Element.ALIGN_CENTER)
        table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
        var cell: PdfPCell
        run {

            /*LEFT TOP LOGO*/
            val d =
                ContextCompat.getDrawable(mContext, R.drawable.logo)
            val bmp = (d as BitmapDrawable?)!!.bitmap
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val logo: Image = Image.getInstance(stream.toByteArray())
            logo.setWidthPercentage(100f)
            logo.scaleToFit(105f, 55f)
            cell = PdfPCell(logo)
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
            cell.isUseAscender = true
            cell.border = PdfPCell.NO_BORDER
            cell.setPadding(2f)
            table.addCell(cell)
        }
        run {

            /*MIDDLE TEXT*/cell = PdfPCell()
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.border = PdfPCell.NO_BORDER
            cell.setPadding(8f)
            cell.isUseAscender = true
            var temp = Paragraph(headerTitle, FONT_TITLE)
            temp.alignment = Element.ALIGN_CENTER
            cell.addElement(temp)
            temp = Paragraph(headerSubtitle, FONT_SUBTITLE)
            temp.alignment = Element.ALIGN_CENTER
            cell.addElement(temp)
            table.addCell(cell)
        }
        /* RIGHT TOP LOGO*/run {
            val logoTable = PdfPTable(1)
            logoTable.widthPercentage = 100f
            logoTable.defaultCell.border = PdfPCell.NO_BORDER
            logoTable.horizontalAlignment = Element.ALIGN_CENTER
            logoTable.defaultCell.setVerticalAlignment(Element.ALIGN_CENTER)
            val drawable =
                ContextCompat.getDrawable(mContext, R.drawable.logo)
            val bmp = (drawable as BitmapDrawable?)!!.bitmap
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val logo: Image = Image.getInstance(stream.toByteArray())
            logo.setWidthPercentage(80f)
            logo.scaleToFit(38f, 38f)
            var logoCell = PdfPCell(logo)
            logoCell.horizontalAlignment = Element.ALIGN_CENTER
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE)
            logoCell.border = PdfPCell.NO_BORDER
            logoTable.addCell(logoCell)
            logoCell = PdfPCell(Phrase("Periksa SPM", FONT_CELL))
            logoCell.horizontalAlignment = Element.ALIGN_CENTER
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE)
            logoCell.border = PdfPCell.NO_BORDER
            logoCell.setPadding(4f)
            logoTable.addCell(logoCell)
            cell = PdfPCell(logoTable)
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
            cell.isUseAscender = true
            cell.border = PdfPCell.NO_BORDER
            cell.setPadding(2f)
            table.addCell(cell)

        }

        //Paragraph paragraph=new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN, 2.0f, Font.NORMAL, BaseColor.BLACK));
        //paragraph.add(table);
        //document.add(paragraph);
        document.add(table)
    }

    fun addEmptyLine(document: Document, number: Int) {
        for (i in 0 until number) {
            document.add(Paragraph(" "))
        }
    }

    // todo : filter berdasarkan tanggal karena ini masih nampilkan seluruh data di document
    fun createDataTableSPM( bitmapPairList: MutableList<Pair<String,Bitmap>>,mContext: Context,documentList: MutableList<Pemeriksaan>) : PdfPTable{
        val table1 = PdfPTable(7)
        table1.widthPercentage = 100f
        table1.setWidths(floatArrayOf(0.2f, 0.7f,1f,1f,0.2f,1f,1f))
//        table1.setHeaderRows(1)
        table1.defaultCell.setVerticalAlignment(Element.ALIGN_CENTER)
        table1.defaultCell.horizontalAlignment = Element.ALIGN_CENTER

        val listTableColumnHeader = listOf("No", "Jenis Pelayanan", "Sub Fasilitas", "Kondisi", "Keterangan", "Dokumentasi")

        // karena kita akan menambahkan colspan ke kondisi maka sebenarnya ilustrasi kolomnya adalah sbb
        // 1|2|3|45|6|7
        // header kolom 4 dan 5 bergabung tapi nanti anaknya akan berpisah jika kita tambah cell lagi setelah cell ke empat
        var cell: PdfPCell
        run {
            for (title in listTableColumnHeader){
                cell = PdfPCell(Phrase(title, FONT_CELL_HEADER))
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
                cell.setPadding(4f)

                // menambahkan colspan = 2 untuk kolom kondisi (ini berarti kita menambahkan 2 cell sekaligus yaitu
                // cell kondisi dan cell kosong untuk digabungkan dengan cell kondisi
                if(title.equals("Kondisi")){
                    cell.colspan = 2
                }
                table1.addCell(cell)
            }

            val top_bottom_Padding = 4f
            val left_right_Padding = 4f
            var alternate = false

            val lt_gray = BaseColor(221, 221, 221) //#DDDDDD

            var cell_color: BaseColor?

            // catatan : jika ada 7 column tapi hanya menambahkan < 7 cell
            //  maka cellnya tidak akan tampil di pdf

            documentList.forEachIndexed { index, doc ->
                doc.items!!.forEachIndexed { nestedIndex, item ->
                    val subitems = item.subitems!!

                    // Tambahkan sel pertama dan kedua dengan rowspan yang sesuai
                    if (nestedIndex == 0) {
                        val totalSubitems = doc.items!!.sumOf { it.subitems!!.size }
                        val indexText = (index + 1).toString()
                        val aspectText = documentList[index].aspek

                        // 1. index / nomor
                        val indexCell = PdfPCell(Phrase(indexText, FONT_CELL))
                        indexCell.horizontalAlignment = Element.ALIGN_CENTER
                        indexCell.verticalAlignment = Element.ALIGN_MIDDLE
                        indexCell.paddingLeft = left_right_Padding
                        indexCell.paddingRight = left_right_Padding
                        indexCell.paddingTop = top_bottom_Padding
                        indexCell.paddingBottom = top_bottom_Padding
                        indexCell.rowspan = totalSubitems
                        table1.addCell(indexCell)

                        // Sel kedua untuk aspek
                        val aspectCell = PdfPCell(Phrase(aspectText, FONT_CELL))
                        aspectCell.horizontalAlignment = Element.ALIGN_LEFT
                        aspectCell.verticalAlignment = Element.ALIGN_MIDDLE
                        aspectCell.paddingLeft = left_right_Padding
                        aspectCell.paddingRight = left_right_Padding
                        aspectCell.paddingTop = top_bottom_Padding
                        aspectCell.paddingBottom = top_bottom_Padding
                        aspectCell.rowspan = totalSubitems
                        table1.addCell(aspectCell)
                    }

                    // Tambahkan sel ketiga dengan rowspan sesuai jumlah subitems dalam satu perulangan item
                    if (subitems.isNotEmpty()) {
                        val itemName = item.nama
                        val itemCell = PdfPCell(Phrase(itemName, FONT_CELL))
                        itemCell.horizontalAlignment = Element.ALIGN_LEFT
                        itemCell.verticalAlignment = Element.ALIGN_MIDDLE
                        itemCell.paddingLeft = left_right_Padding
                        itemCell.paddingRight = left_right_Padding
                        itemCell.paddingTop = top_bottom_Padding
                        itemCell.paddingBottom = top_bottom_Padding
                        itemCell.rowspan = subitems.size
                        table1.addCell(itemCell)
                    }

                    // Tambahkan sel keempat (nama item) dengan rowspan sesuai jumlah subitems dalam satu perulangan item
                    subitems.forEachIndexed { nestedNestedIndex, subitem ->

                        // 4. deskripsi / kondisi
                        val deskripsiCell = PdfPCell(Phrase("${subitem.deskripsi}", FONT_CELL))
                        deskripsiCell.horizontalAlignment = Element.ALIGN_LEFT
                        deskripsiCell.verticalAlignment = Element.ALIGN_MIDDLE
                        deskripsiCell.paddingLeft = left_right_Padding
                        deskripsiCell.paddingRight = left_right_Padding
                        deskripsiCell.paddingTop = top_bottom_Padding
                        deskripsiCell.paddingBottom = top_bottom_Padding
                        table1.addCell(deskripsiCell)

                        // Tambahkan sel untuk status (5)
                        val convertedStatus = if(subitem.status == 1) {
                            "V"
                        } else {
                            "X"
                        }

                        // JIKA INGIN MENERAPKAN WARNA BEDA KONDISI
//                        val customGreen = BaseColor(112, 173, 71) // Example RGB values for custom green
//                        // Define the font with the desired color
//                        val fontWithColor = Font(FONT_CELL)
//                        fontWithColor.color = if(subitem.status == 1) {
//                            customGreen
//                        } else {
//                            BaseColor.RED
//                        }

                        // 5. status (bergabung dengan 4)
                        val statusCell = PdfPCell(Phrase(convertedStatus, FONT_CELL))
                        statusCell.horizontalAlignment = Element.ALIGN_CENTER
                        statusCell.verticalAlignment = Element.ALIGN_MIDDLE
                        statusCell.paddingLeft = left_right_Padding
                        statusCell.paddingRight = left_right_Padding
                        statusCell.paddingTop = top_bottom_Padding
                        statusCell.paddingBottom = top_bottom_Padding


                        table1.addCell(statusCell)

                        // 6. kolom deskripsi
                        if (nestedNestedIndex == 0) {
                            val itemNameCell = PdfPCell(Phrase(item.keterangan, FONT_CELL))
                            itemNameCell.horizontalAlignment = Element.ALIGN_LEFT
                            itemNameCell.verticalAlignment = Element.ALIGN_MIDDLE
                            itemNameCell.paddingLeft = left_right_Padding
                            itemNameCell.paddingRight = left_right_Padding
                            itemNameCell.paddingTop = top_bottom_Padding
                            itemNameCell.paddingBottom = top_bottom_Padding
                            itemNameCell.rowspan = subitems.size
                            table1.addCell(itemNameCell)
                        }

                        // 7. kolom gambar
                        if (nestedNestedIndex == 0) {
                            // nested index : urutan item ke-n
                            // nested-nested index : urutan subitem ke-n
                            // 1 gambar == 1 item


//
//                            val itemGambarCell = PdfPCell()
//                            itemGambarCell.horizontalAlignment = Element.ALIGN_CENTER
//                            itemGambarCell.verticalAlignment = Element.ALIGN_MIDDLE
//                            itemGambarCell.paddingLeft = left_right_Padding
//                            itemGambarCell.paddingRight = left_right_Padding
//                            itemGambarCell.paddingTop = top_bottom_Padding
//                            itemGambarCell.paddingBottom = top_bottom_Padding
//                            itemGambarCell.rowspan = subitems.size
//                            repeat(3){ index ->
//                                val stream = ByteArrayOutputStream()
////                                val bitmap = bitmapPairList.find { it.first.equals(item.gambar) }?.second // ambil bitmap (nilai) berdasarkan key (nama gambar)
//                                val bitmap = bitmapPairList[0].second // ambil bitmap (nilai) berdasarkan key (nama gambar)
//                                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
//                                val gambar: Image = Image.getInstance(stream.toByteArray())
//                                gambar.setWidthPercentage(100f)
//                                gambar.scaleToFit(105f, 55f)
//                                itemGambarCell.addElement(gambar)
//                            }
//                            table1.addCell(itemGambarCell)

                            // ========================================================================
                            val itemGambarCell = PdfPCell()
                            itemGambarCell.horizontalAlignment = Element.ALIGN_CENTER
                            itemGambarCell.verticalAlignment = Element.ALIGN_MIDDLE
                            itemGambarCell.paddingLeft = left_right_Padding
                            itemGambarCell.paddingRight = left_right_Padding
                            itemGambarCell.paddingTop = 10f
                            itemGambarCell.paddingBottom = top_bottom_Padding
                            itemGambarCell.rowspan = subitems.size
                            repeat(3){ deepIndex ->
//                                val stream = ByteArrayOutputStream()
//                                val bitmap = bitmapPairList.find { it.first.equals(item.gambar!![deepIndex]) }?.second // ambil bitmap (nilai) berdasarkan key (nama gambar)
//                                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
//                                val gambar: Image = Image.getInstance(stream.toByteArray())
//                                gambar.setWidthPercentage(100f)
//                                gambar.scaleToFit(105f, 55f)

//                                itemGambarCell.addElement(Phrase("TEST $deepIndex"))
                                if(!item.gambar!![deepIndex].equals("")){
                                    val stream = ByteArrayOutputStream()
                                    val bitmap = bitmapPairList.find { it.first.equals(item.gambar!![deepIndex]) }?.second // ambil bitmap (nilai) berdasarkan key (nama gambar)
                                    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                                    val gambar: Image = Image.getInstance(stream.toByteArray())
                                    gambar.setWidthPercentage(100f)
                                    gambar.scaleToFit(105f, 55f)
                                    gambar.alignment = Element.ALIGN_CENTER

                                    itemGambarCell.addElement(gambar)
                                    // Add spacing after each image
                                    if (deepIndex < 2) { // Don't add spacing after the last image
                                        itemGambarCell.addElement(Phrase(" ")) // Add an empty Phrase to create space
                                    }
//                                itemGambarCell.addElement(Phrase("TEST $deepIndex"))
                                }
                            }
                            table1.addCell(itemGambarCell)
                            // ========================================================================


                            // ========================================================================
                            // menampilkan list nama gambar
//                            val itemNameCell = PdfPCell(Phrase(item.gambar.toString(), FONT_CELL))
//                            itemNameCell.horizontalAlignment = Element.ALIGN_LEFT
//                            itemNameCell.verticalAlignment = Element.ALIGN_MIDDLE
//                            itemNameCell.paddingLeft = left_right_Padding
//                            itemNameCell.paddingRight = left_right_Padding
//                            itemNameCell.paddingTop = top_bottom_Padding
//                            itemNameCell.paddingBottom = top_bottom_Padding
//                            itemNameCell.rowspan = subitems.size
//                            table1.addCell(itemNameCell)
                            // ========================================================================


                            // Buat tabel baru untuk menampung tiga gambar

//                            val itemNameCell = PdfPCell(Phrase("TES GAMBAR", FONT_CELL))
//                            itemNameCell.horizontalAlignment = Element.ALIGN_LEFT
//                            itemNameCell.verticalAlignment = Element.ALIGN_MIDDLE
//                            itemNameCell.paddingLeft = left_right_Padding
//                            itemNameCell.paddingRight = left_right_Padding
//                            itemNameCell.paddingTop = top_bottom_Padding
//                            itemNameCell.paddingBottom = top_bottom_Padding
//                            itemNameCell.rowspan = subitems.size
//                            table1.addCell(itemNameCell)

//                            val innerTable = PdfPTable(1)
//                            innerTable.widthPercentage = 100f
//                            repeat(3){
//                            val itemcell = PdfPCell(Phrase("TES", FONT_CELL))
//                                innerTable.addCell(itemcell)
//                            }
//                            table1.addCell(innerTable)
                        }
                    } // end of perulangan subitems
                }  // end of perulangan items
            } // end of perulangan docs

            return table1
        }
    }

    fun createDataTableKesimpulan(documentList: MutableList<Pemeriksaan>): PdfPTable{
        val top_bottom_Padding = 8f
        val left_right_Padding = 4f

        val table1 = PdfPTable(2)
        table1.widthPercentage = 80f
        table1.setWidths(floatArrayOf(1f,1f))
        table1.defaultCell.setVerticalAlignment(Element.ALIGN_CENTER)
        table1.defaultCell.horizontalAlignment = Element.ALIGN_LEFT

        var cell: PdfPCell
        cell = PdfPCell(Phrase("Kesimpulan", FONT_CELL_HEADER))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
        cell.setPadding(4f)
        cell.colspan = 2
        table1.addCell(cell)


        var totalPersentase = 0f
        documentList.forEachIndexed { index, doc ->
            totalPersentase+= doc.persentase!!.toFloat()
        }

        documentList.forEachIndexed { index, doc ->
            val kesimpulanCell = PdfPCell(Phrase("${doc.aspek} ${doc.persentase}% memenuhi SPM", FONT_CELL))
            kesimpulanCell.horizontalAlignment = Element.ALIGN_CENTER
            kesimpulanCell.verticalAlignment = Element.ALIGN_MIDDLE
            kesimpulanCell.paddingLeft = left_right_Padding
            kesimpulanCell.paddingRight = left_right_Padding
            kesimpulanCell.paddingTop = top_bottom_Padding
            kesimpulanCell.paddingBottom = top_bottom_Padding
            table1.addCell(kesimpulanCell)

            if(index == 0){
                val formattedPersentase = String.format(Locale.US, "%.2f", totalPersentase/documentList.size)
                val totalPersentaseCell = PdfPCell(Phrase("Total $formattedPersentase% memenuhi SPM", FONT_CELL))
                totalPersentaseCell.horizontalAlignment = Element.ALIGN_CENTER
                totalPersentaseCell.verticalAlignment = Element.ALIGN_MIDDLE
                totalPersentaseCell.paddingLeft = left_right_Padding
                totalPersentaseCell.paddingRight = left_right_Padding
                totalPersentaseCell.paddingTop = top_bottom_Padding
                totalPersentaseCell.paddingBottom = top_bottom_Padding
                totalPersentaseCell.rowspan = documentList.size
                table1.addCell(totalPersentaseCell)
            }
        }
        return table1

    }

//    lateinit var spmItems: List<Pair<String, List<String>>>
    fun createTableTidakMemenuhi(list: List<List<List<String>>>):PdfPTable{
        val table = PdfPTable(3)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(0.7f, 1f,1f))
//        table1.setHeaderRows(1)
        table.defaultCell.setVerticalAlignment(Element.ALIGN_CENTER)
        table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER

        val listTableColumnHeader = listOf("Jenis Pelayanan", "Sub Fasilitas yang Tidak Memenuhi SPM", "Keterangan")
        var cell: PdfPCell
        run {
            for (title in listTableColumnHeader){
                cell = PdfPCell(Phrase(title, FONT_CELL_HEADER))
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
                cell.setPadding(4f)

                table.addCell(cell)
            }
            val top_bottom_Padding = 4f
            val left_right_Padding = 4f

//            val keamanan = listOf(
//                listOf("keamanan", "ruang ibu menyusui", "ayam bakar mana"),
//                listOf("keamanan", "ruang anu", "butuh galon"),
//                listOf("keamanan", "ruang ibu", "gacor")
//            )
//
//            val kesetaraan = listOf(
//                listOf("kesetaraan", "ruang itu", "aman"),
//                listOf("kesetaraan", "ruang anu", "kurang ac"),
//                listOf("kesetaraan", "ruang ono", "kurang ajib")
//            )
//
//            val aspekList = listOf(
//                keamanan,
//                kesetaraan
//            )

            for (aspek in list){
                for((index, row) in aspek.withIndex()){
                    for ((indexData,data) in row.withIndex()){
                        val cell = PdfPCell(Phrase(data, FONT_CELL))
                        // baris
                        // pertama
                        if(index == 0){
                            // aspek baris pertama kolom pertama
                            if(indexData == 0){
                                cell.rowspan = aspek.size
                                table.addCell(cell)
                            } else {
                                // aspek baris pertama tapi bukan kolom pertama (0+n)
                                table.addCell(cell)
                            }
                        } else {
                            // aspek kolom pertama baris ke 0 + n
                            if(indexData == 0){
                                // skip
                                // karena udah dikasih rowspan
                            } else {
                                // aspek baris dan kolom bukan pertama
                                table.addCell(cell)
                            }
                        }
                    }
                }

                // 2x
            }

//            for (aspek in aspekList){
//                for((index, row) in aspek.withIndex()){
//                    for (cell in row){
//                        table.addCell(cell)
//                    }
//                }
//            }

//            for (row in data) {
//                for (cell in row) {
//                    table.addCell(cell)
//                }
//            }

//            list.forEachIndexed { index, strings ->
//
//            }



            return table
        }
    }

    fun createDataTableSPMManualBasic() : PdfPTable{
        val table1 = PdfPTable(7)
        table1.widthPercentage = 100f
        table1.setWidths(floatArrayOf(0.5f, 1f, 1f,1f,1f,1f,1f))
//        table1.setHeaderRows(1)
        table1.defaultCell.setVerticalAlignment(Element.ALIGN_CENTER)
        table1.defaultCell.horizontalAlignment = Element.ALIGN_CENTER

        var cell: PdfPCell
        run {
            cell = PdfPCell(Phrase("No", FONT_COLUMN))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
            cell.setPadding(4f)
            table1.addCell(cell)
            cell = PdfPCell(Phrase("Nama", FONT_COLUMN))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
            cell.setPadding(4f)
            table1.addCell(cell)
            cell = PdfPCell(Phrase("Aspek", FONT_COLUMN))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
            cell.setPadding(4f)
            table1.addCell(cell)
            cell = PdfPCell(Phrase("Item", FONT_COLUMN))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
            cell.setPadding(4f)
            table1.addCell(cell)
            cell = PdfPCell(Phrase("Subitem", FONT_COLUMN))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
            cell.setPadding(4f)
            table1.addCell(cell)
            cell = PdfPCell(Phrase("Keterangan", FONT_COLUMN))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
            cell.setPadding(4f)
            table1.addCell(cell)
            cell = PdfPCell(Phrase("Gambar", FONT_COLUMN))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
            cell.setPadding(4f)
            table1.addCell(cell)
//             end of table header

            return table1
        }
    }

    fun createDataTable(dataTable: MutableList<Array<String>>): PdfPTable {
        val table1 = PdfPTable(2)
        table1.widthPercentage = 100f
        table1.setWidths(floatArrayOf(1f, 2f))
        table1.setHeaderRows(1)
        table1.defaultCell.setVerticalAlignment(Element.ALIGN_CENTER)
        table1.defaultCell.horizontalAlignment = Element.ALIGN_CENTER

        var cell: PdfPCell
        run {
            cell = PdfPCell(Phrase("COLUMN - 1", FONT_COLUMN))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
            cell.setPadding(4f)
            table1.addCell(cell)
            cell = PdfPCell(Phrase("COLUMN - 2", FONT_COLUMN))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
            cell.setPadding(4f)
            table1.addCell(cell)
        }

        val top_bottom_Padding = 8f
        val left_right_Padding = 4f
        var alternate = false

        val lt_gray = BaseColor(221, 221, 221) //#DDDDDD

        var cell_color: BaseColor?

        val size: Int = dataTable.size

        for (i in 0 until size) {
            cell_color = if (alternate) lt_gray else BaseColor.WHITE
            val temp: Array<String> = dataTable.get(i)
            cell = PdfPCell(Phrase(temp[0], FONT_CELL))
            cell.horizontalAlignment = Element.ALIGN_LEFT
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
            cell.paddingLeft = left_right_Padding
            cell.paddingRight = left_right_Padding
            cell.paddingTop = top_bottom_Padding
            cell.paddingBottom = top_bottom_Padding
            cell.backgroundColor = cell_color
            table1.addCell(cell)
            cell = PdfPCell(Phrase(temp[1], FONT_CELL))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
            cell.paddingLeft = left_right_Padding
            cell.paddingRight = left_right_Padding
            cell.paddingTop = top_bottom_Padding
            cell.paddingBottom = top_bottom_Padding
            cell.backgroundColor = cell_color
            table1.addCell(cell)
            alternate = !alternate
        }

        return table1
    }

    fun getSampleData(): MutableList<Array<String>> {
        var count = 20
        val temp: MutableList<Array<String>> = ArrayList()
        for (i in 0 until count) {
            temp.add(arrayOf("C1-R" + (i + 1), "C2-R" + (i + 1)))
        }
        return temp
    }
}