package FelixVal05.projectdompetku

import FelixVal05.projectdompetku.databinding.ActivityTransaksiBinding
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TransaksiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransaksiBinding
    private var _arPemasukan = ArrayList<dcTransaksi>()
    private var _arPengeluaran = ArrayList<dcTransaksi>()
    private val _arTampil = ArrayList<dcTransaksi>()
    private val _listUrut = arrayListOf("Tanggal Terbaru", "Tanggal Terlama", "Jumlah Terbesar", "Jumlah Terkecil")

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sp = getSharedPreferences("data_sp", MODE_PRIVATE)

        val adapterUrut = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, _listUrut)
        binding.spUrut.adapter = adapterUrut

        binding.btnTerapkan.setOnClickListener {
            prosesData()
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
        prosesData()
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

    private fun simpanKeSP(key: String, list: ArrayList<dcTransaksi>) {
        val gson = Gson()
        val json = gson.toJson(list)
        sp.edit {
            putString(key, json)
        }
    }

    private fun loadData() {
        _arPemasukan = ambilData("spPemasukan")
        for (t in _arPemasukan) {
            t.jenis = "Pemasukan"
        }
        _arPengeluaran = ambilData("spPengeluaran")
        for (t in _arPengeluaran) {
            t.jenis = "Pengeluaran"
        }
    }

    private fun ubahTanggal(tanggal: String): Int {
        val pecah = tanggal.split("/")
        return pecah[2].toInt() * 10000 + pecah[1].toInt() * 100 + pecah[0].toInt()
    }

    private fun prosesData() {
        _arTampil.clear()

        if (binding.rbSemua.isChecked) {
            _arTampil.addAll(_arPemasukan)
            _arTampil.addAll(_arPengeluaran)
        } else if (binding.rbFilterPemasukan.isChecked) {
            _arTampil.addAll(_arPemasukan)
        } else {
            _arTampil.addAll(_arPengeluaran)
        }

        val cari = binding.etCari.text.toString().lowercase()
        if (cari.isNotEmpty()) {
            val hasilCari = ArrayList<dcTransaksi>()
            for (t in _arTampil) {
                if (t.kategori.lowercase().contains(cari) || t.keterangan.lowercase().contains(cari)) {
                    hasilCari.add(t)
                }
            }
            _arTampil.clear()
            _arTampil.addAll(hasilCari)
        }

        when (binding.spUrut.selectedItemPosition) {
            0 -> _arTampil.sortByDescending { ubahTanggal(it.tanggal) }
            1 -> _arTampil.sortBy { ubahTanggal(it.tanggal) }
            2 -> _arTampil.sortByDescending { it.jumlah }
            3 -> _arTampil.sortBy { it.jumlah }
        }

        binding.tvInfo.text = "Menampilkan ${_arTampil.size} transaksi"
        tampilKanData()
    }

    private fun tampilKanData() {
        binding.rvSemuaTransaksi.layoutManager = LinearLayoutManager(this)

        val adapterTrx = adapterTransaksi(_arTampil)
        adapterTrx.setOnItemClickCallBack(object : adapterTransaksi.OnItemClickCallBack {
            override fun onItemClicked(pos: Int) {
                val item = _arTampil[pos]
                // cari posisi asli di list pemasukan / pengeluaran
                var posisiAsli = 0
                if (item.jenis == "Pemasukan") {
                    posisiAsli = _arPemasukan.indexOf(item)
                } else {
                    posisiAsli = _arPengeluaran.indexOf(item)
                }

                val intent = Intent(this@TransaksiActivity, CatatActivity::class.java)
                intent.putExtra("jenis", item.jenis)
                intent.putExtra("posisi", posisiAsli)
                startActivity(intent)
            }

            override fun delData(pos: Int) {
                val item = _arTampil[pos]
                AlertDialog.Builder(this@TransaksiActivity)
                    .setTitle("Konfirmasi HAPUS DATA")
                    .setMessage("Apakah Benar Data ${item.kategori} Rp ${item.jumlah} akan dihapus ?")
                    .setPositiveButton("Hapus") { _, _ ->
                        if (item.jenis == "Pemasukan") {
                            _arPemasukan.remove(item)
                            simpanKeSP("spPemasukan", _arPemasukan)
                        } else {
                            _arPengeluaran.remove(item)
                            simpanKeSP("spPengeluaran", _arPengeluaran)
                        }
                        prosesData()
                        Toast.makeText(
                            this@TransaksiActivity,
                            "Data berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .setNegativeButton("Batal", null).show()
            }
        })
        binding.rvSemuaTransaksi.adapter = adapterTrx
    }
}