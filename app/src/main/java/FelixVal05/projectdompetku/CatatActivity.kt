package FelixVal05.projectdompetku

import FelixVal05.projectdompetku.databinding.ActivityCatatBinding
import android.app.DatePickerDialog
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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import java.util.TimeZone

class CatatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCatatBinding
    private var _arTransaksi = ArrayList<dcTransaksi>()
    private val _listKategori = ArrayList<String>()
    private var jenis = "Pemasukan"
    private var posisiEdit = -1

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCatatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dataJenis = intent.getStringExtra("jenis")
        if (dataJenis != null){
            jenis = dataJenis
        }
        binding.tvJudulCatat.text = "Catat $jenis"

        sp = getSharedPreferences("data_sp",MODE_PRIVATE)
        val gson = Gson()

        val isiKategori = sp.getString("spKategori",null)
        val typeKategori = object : TypeToken<ArrayList<dcKategori>>() {}.type
        if (isiKategori != null){
            val arKategori: ArrayList<dcKategori> = gson.fromJson(isiKategori, typeKategori)
            for (k in arKategori){
                if (k.jenis == jenis){
                    _listKategori.add(k.nama)
                }
            }
        }
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, _listKategori)
        binding.spKategori.adapter = adapterSpinner

        val isiSP = sp.getString("sp$jenis",null)
        val type = object : TypeToken<ArrayList<dcTransaksi>>() {}.type
        if (isiSP != null){
            _arTransaksi = gson.fromJson(isiSP,type)
        }

        tampilKanData()
        hitungTotal()

        binding.etTanggal.setOnClickListener {
            pilihTanggal()
        }

        binding.btnSimpan.setOnClickListener {
            val tanggal = binding.etTanggal.text.toString()
            val jumlah = binding.etJumlah.text.toString()
            val keterangan = binding.etKeterangan.text.toString()

            if (_listKategori.isEmpty()) {
                Toast.makeText(this, "Tambahkan kategori $jenis dulu", Toast.LENGTH_SHORT).show()
            }
            else if (tanggal.isEmpty() || jumlah.isEmpty()) {
                Toast.makeText(this, "Tanggal dan jumlah harus diisi", Toast.LENGTH_SHORT).show()
            }
            else {
                val kategori = binding.spKategori.selectedItem.toString()
                val transaksi = dcTransaksi(
                    tanggal = tanggal,
                    kategori = kategori,
                    jumlah = jumlah.toInt(),
                    keterangan = keterangan
                )

                if (posisiEdit == -1) {
                    _arTransaksi.add(transaksi)
                    Toast.makeText(this, "$jenis berhasil dicatat", Toast.LENGTH_SHORT).show()
                }
                else {
                    _arTransaksi[posisiEdit] = transaksi
                    Toast.makeText(this, "$jenis berhasil diupdate", Toast.LENGTH_SHORT).show()
                }
                simpanKeSP()
                tampilKanData()
                hitungTotal()
                resetForm()
            }

            binding.btnBatal.setOnClickListener {
                resetForm()
            }
        }
    }

    private fun pilihTanggal() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                binding.etTanggal.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun simpanKeSP() {
        val gson = Gson()
        val json = gson.toJson(_arTransaksi)
        sp.edit{
            putString("sp$jenis", json)
        }
    }

    private fun hitungTotal() {
        var total = 0
        for (t in _arTransaksi) {
            total += t.jumlah
        }
        binding.tvTotal.text = "Total $jenis : Rp $total"
    }

    private fun resetForm() {
        binding.etTanggal.setText("")
        binding.etJumlah.setText("")
        binding.etKeterangan.setText("")
        if (_listKategori.isNotEmpty()) {
            binding.spKategori.setSelection(0)
        }
        binding.btnSimpan.text = "Simpan"
        posisiEdit = -1
    }

    private fun tampilKanData() {
        binding.rvTransaksi.itemAnimator = DefaultItemAnimator().apply {
            removeDuration = 400L
        }
        binding.rvTransaksi.layoutManager = LinearLayoutManager(this)

        val adapterTrx = adapterTransaksi(_arTransaksi)
        adapterTrx.setOnItemClickCallBack(object : adapterTransaksi.OnItemClickCallBack {
            override fun onItemClicked(pos: Int) {
                val data = _arTransaksi[pos]
                posisiEdit = pos
                binding.etTanggal.setText(data.tanggal)
                binding.etJumlah.setText(data.jumlah.toString())
                binding.etKeterangan.setText(data.keterangan)
                val idx = _listKategori.indexOf(data.kategori)
                if (idx >= 0) {
                    binding.spKategori.setSelection(idx)
                }
                binding.btnSimpan.text = "Update"
            }

            override fun delData(pos: Int) {
                val item = _arTransaksi[pos]
                AlertDialog.Builder(this@CatatActivity)
                    .setTitle("Konfirmasi HAPUS DATA")
                    .setMessage("Apakah Benar Data ${item.kategori} Rp ${item.jumlah} akan dihapus ?")
                    .setPositiveButton("Hapus") { _, _ ->
                        _arTransaksi.removeAt(pos)
                        simpanKeSP()
                        adapterTrx.notifyItemRemoved(pos)
                        adapterTrx.notifyItemRangeChanged(pos, _arTransaksi.size - pos)
                        hitungTotal()
                        resetForm()
                        Toast.makeText(
                            this@CatatActivity,
                            "Data berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .setNegativeButton("Batal", null).show()
            }
        })
        binding.rvTransaksi.adapter = adapterTrx
    }
}