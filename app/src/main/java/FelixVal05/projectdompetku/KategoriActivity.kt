package FelixVal05.projectdompetku

import FelixVal05.projectdompetku.databinding.ActivityKategoriBinding
import FelixVal05.projectdompetku.databinding.ActivityMainBinding
import android.content.SharedPreferences
import android.os.Bundle
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

class KategoriActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKategoriBinding
    private var _arKategori = ArrayList<dcKategori>()
    private var posisiEdit = -1

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityKategoriBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sp = getSharedPreferences("data_sp", MODE_PRIVATE)
        val gson = Gson()
        val isiSP = sp.getString("spKategori", null)
        val type = object : TypeToken<ArrayList<dcKategori>>() {}.type
        if (isiSP != null) {
            _arKategori = gson.fromJson(isiSP, type)
        }

        tampilKanData()

        binding.btnSimpanKategori.setOnClickListener {
            val nama = binding.etNamaKategori.text.toString()
            var jenis = "Pemasukan"
            if (binding.rbPengeluaran.isChecked) {
                jenis = "Pengeluaran"
            }

            if (nama.isEmpty()) {
                Toast.makeText(this, "Nama kategori harus diisi", Toast.LENGTH_SHORT).show()
            }
            else {
                if (posisiEdit == -1) {
                    _arKategori.add(dcKategori(nama, jenis))
                    Toast.makeText(this, "$nama berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                }
                else {
                    _arKategori[posisiEdit] = dcKategori(nama, jenis)
                    Toast.makeText(this, "$nama berhasil diupdate", Toast.LENGTH_SHORT).show()
                }
                simpanKeSP()
                tampilKanData()
                resetForm()
            }
        }

        binding.btnBatal.setOnClickListener {
            resetForm()
        }
    }

    private fun simpanKeSP() {
        val gson = Gson()
        val json = gson.toJson(_arKategori)
        sp.edit{
            putString("spKategori", json)
        }
    }

    private fun resetForm() {
        binding.etNamaKategori.setText("")
        binding.rbPemasukan.isChecked = true
        binding.btnSimpanKategori.text = "Simpan"
        posisiEdit = -1
    }

    private fun tampilKanData() {
        binding.rvKategori.itemAnimator = DefaultItemAnimator().apply {
            removeDuration = 400L
        }
        binding.rvKategori.layoutManager = LinearLayoutManager(this)

        val adapterKat = adapterKategori(_arKategori)
        adapterKat.setOnItemClickCallBack(object : adapterKategori.OnItemClickCallBack {
            override fun onItemClicked(pos: Int) {
                posisiEdit = pos
                binding.etNamaKategori.setText(_arKategori[pos].nama)
                if (_arKategori[pos].jenis == "Pemasukan") {
                    binding.rbPemasukan.isChecked = true
                } else {
                    binding.rbPengeluaran.isChecked = true
                }
                binding.btnSimpanKategori.text = "Update"
            }

            override fun delData(pos: Int) {
                val item = _arKategori[pos]
                AlertDialog.Builder(this@KategoriActivity)
                    .setTitle("Konfirmasi HAPUS DATA")
                    .setMessage("Apakah Benar Kategori ${item.nama} akan dihapus ?")
                    .setPositiveButton("Hapus") { _, _ ->
                        _arKategori.removeAt(pos)
                        simpanKeSP()
                        adapterKat.notifyItemRemoved(pos)
                        adapterKat.notifyItemRangeChanged(pos, _arKategori.size - pos)
                        resetForm()
                        Toast.makeText(
                            this@KategoriActivity,
                            "${item.nama} berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .setNegativeButton("Batal", null).show()
            }
        })
        binding.rvKategori.adapter = adapterKat
    }
}