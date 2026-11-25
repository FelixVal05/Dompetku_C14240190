package FelixVal05.projectdompetku

import FelixVal05.projectdompetku.databinding.ActivityMainBinding
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnKategori.setOnClickListener {
            val intent = Intent(this@MainActivity, KategoriActivity::class.java)
            startActivity(intent)
        }

        binding.btnPemasukan.setOnClickListener {
            val intent = Intent(this@MainActivity, CatatActivity::class.java)
            intent.putExtra("jenis","Pemasukan")
            startActivity(intent)
        }

        binding.btnPengeluaran.setOnClickListener {
            val intent = Intent(this@MainActivity, CatatActivity::class.java)
            intent.putExtra("jenis","Pengeluaran")
            startActivity(intent)
        }
    }
}