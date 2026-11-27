package FelixVal05.projectdompetku

import FelixVal05.projectdompetku.databinding.ActivityMainBinding
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sp = getSharedPreferences("data_sp", MODE_PRIVATE)

        binding.btnKategori.setOnClickListener {
            val intent = Intent(this@MainActivity, KategoriActivity::class.java)
            startActivity(intent)
        }

        binding.btnPemasukan.setOnClickListener {
            val intent = Intent(this@MainActivity, CatatActivity::class.java)
            intent.putExtra("jenis", "Pemasukan")
            startActivity(intent)
        }

        binding.btnPengeluaran.setOnClickListener {
            val intent = Intent(this@MainActivity, CatatActivity::class.java)
            intent.putExtra("jenis", "Pengeluaran")
            startActivity(intent)
        }

        binding.btnTransaksi.setOnClickListener {
            val intent = Intent(this@MainActivity, TransaksiActivity::class.java)
            startActivity(intent)
        }

        binding.btnLaporan.setOnClickListener {
            val intent = Intent(this@MainActivity, LaporanActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        hitungRingkasan()
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

    private fun hitungRingkasan() {
        val arPemasukan = ambilData("spPemasukan")
        val arPengeluaran = ambilData("spPengeluaran")

        var totalPemasukan = 0
        for (t in arPemasukan) {
            totalPemasukan += t.jumlah
        }

        var totalPengeluaran = 0
        for (t in arPengeluaran) {
            totalPengeluaran += t.jumlah
        }

        val saldo = totalPemasukan - totalPengeluaran

        binding.tvSaldo.text = "Rp $saldo"
        binding.tvTotalPemasukan.text = "Rp $totalPemasukan"
        binding.tvTotalPengeluaran.text = "Rp $totalPengeluaran"
        binding.tvJumlahTransaksi.text = "${arPemasukan.size + arPengeluaran.size} transaksi"
    }
}