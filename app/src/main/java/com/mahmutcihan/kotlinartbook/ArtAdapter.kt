package com.mahmutcihan.kotlinartbook

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mahmutcihan.kotlinartbook.databinding.RecylerRowBinding

class ArtAdapter(val artList:ArrayList<Arts>):RecyclerView.Adapter<ArtAdapter.ArtHolder>(){
    class ArtHolder(val binding:RecylerRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {
        val binding=RecylerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArtHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtHolder, position: Int) {
        holder.binding.rvTextView.text=artList.get(position).name
        val deger=artList.get(position).picture
        val bitmap= BitmapFactory.decodeByteArray(deger,0,deger.size)
        holder.binding.mainMenuImage.setImageBitmap(bitmap)
        holder.itemView.setOnClickListener {
            val intent=Intent(holder.itemView.context,ArtActivity::class.java)
            intent.putExtra("info","old")
            intent.putExtra("id",artList[position].id)


            holder.itemView.context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return artList.size
    }
}