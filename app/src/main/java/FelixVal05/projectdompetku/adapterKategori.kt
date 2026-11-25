package FelixVal05.projectdompetku

import FelixVal05.projectdompetku.databinding.ItemKategoriBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class adapterKategori(private val listKategori: ArrayList<dcKategori>) : RecyclerView.Adapter<adapterKategori.ListViewHolder>(){
    private lateinit var onItemClickCallBack: OnItemClickCallBack

    interface OnItemClickCallBack{
        fun onItemClicked(pos: Int)
        fun delData(pos: Int)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack){
        this.onItemClickCallBack = onItemClickCallBack
    }

    class ListViewHolder(val binding: ItemKategoriBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemKategoriBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listKategori.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val kategori = listKategori[position]
        holder.binding.tvNamaKategori.text = kategori.nama
        holder.binding.tvJenisKategori.text = kategori.jenis
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