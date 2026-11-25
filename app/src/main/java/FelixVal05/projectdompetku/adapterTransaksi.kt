package FelixVal05.projectdompetku

import FelixVal05.projectdompetku.databinding.ItemTransaksiBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class adapterTransaksi(private val listTransaksi: ArrayList<dcTransaksi>) :
    RecyclerView.Adapter<adapterTransaksi.ListViewHolder>() {

    private lateinit var onItemClickCallBack: OnItemClickCallBack

    interface OnItemClickCallBack {
        fun onItemClicked(pos: Int)
        fun delData(pos: Int)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    class ListViewHolder(val binding: ItemTransaksiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemTransaksiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listTransaksi.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val transaksi = listTransaksi[position]
        holder.binding.tvKategori.text = transaksi.kategori
        holder.binding.tvTanggal.text = transaksi.tanggal
        holder.binding.tvKeterangan.text = transaksi.keterangan
        holder.binding.tvJumlah.text = "Rp ${transaksi.jumlah}"

        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION && ::onItemClickCallBack.isInitialized) {
                onItemClickCallBack.onItemClicked(currentPosition)
            }
        }

        holder.binding.btnHapus.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION && ::onItemClickCallBack.isInitialized) {
                onItemClickCallBack.delData(currentPosition)
            }
        }
    }
}