package FelixVal05.projectdompetku

import FelixVal05.projectdompetku.databinding.ActivityLaporanBinding
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import java.util.TimeZone

class LaporanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLaporanBinding
    private val _listBulan = arrayListOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sp = getSharedPreferences("data_sp", MODE_PRIVATE)

        val adapterBulan = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, _listBulan)
        binding.spBulan.adapter = adapterBulan

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        binding.spBulan.setSelection(calendar.get(Calendar.MONTH))
        binding.etTahun.setText(calendar.get(Calendar.YEAR).toString())

        hitungLaporan()

        binding.btnHitung.setOnClickListener {
            hitungLaporan()
        }
    }

    private fun ambilData(key: String): ArrayList<dcTransaksi> {
        val gson = Gson()
        val isiSP = sp.getString(key, null)
        val type = object : TypeToken<ArrayList<dcTransaksi>>() {}.type
        var hasil = ArrayList<dcTransaksi>()
        if (isiSP != null) {
            hasil = gson.fromJson(isiSP, type)
        }
        return hasil
    }

    private fun hitungLaporan() {
        val tahunText = binding.etTahun.text.toString()
        if (tahunText.isEmpty()) {
            Toast.makeText(this, "Tahun harus diisi", Toast.LENGTH_SHORT).show()
        }
        else {
            val bulan = binding.spBulan.selectedItemPosition + 1
            val tahun = tahunText.toInt()

            val arPemasukan = ambilData("spPemasukan")
            val arPengeluaran = ambilData("spPengeluaran")

            var totalPemasukan = 0
            var totalPengeluaran = 0
            var jumlahTransaksi = 0

            for (t in arPemasukan) {
                val pecah = t.tanggal.split("/")
                if (pecah[1].toInt() == bulan && pecah[2].toInt() == tahun) {
                    totalPemasukan += t.jumlah
                    jumlahTransaksi++
                }
            }

            for (t in arPengeluaran) {
                val pecah = t.tanggal.split("/")
                if (pecah[1].toInt() == bulan && pecah[2].toInt() == tahun) {
                    totalPengeluaran += t.jumlah
                    jumlahTransaksi++
                }
            }

            val selisih = totalPemasukan - totalPengeluaran

            binding.tvPeriode.text = "Periode : ${_listBulan[bulan - 1]} $tahun"
            binding.tvLapPemasukan.text = "Total Pemasukan : Rp $totalPemasukan"
            binding.tvLapPengeluaran.text = "Total Pengeluaran : Rp $totalPengeluaran"
            binding.tvLapSelisih.text = "Selisih : Rp $selisih"
            binding.tvLapJumlah.text = "Jumlah Transaksi : $jumlahTransaksi"
        }
    }
}