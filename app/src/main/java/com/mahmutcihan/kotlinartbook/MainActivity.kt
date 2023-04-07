 package com.mahmutcihan.kotlinartbook

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahmutcihan.kotlinartbook.databinding.ActivityMainBinding

 class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    //private lateinit var binding2: RecylerRowBinding
    private lateinit var artList:ArrayList<Arts>
    private lateinit var artAdapter: ArtAdapter
    private lateinit var database1: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)

        val view=binding.root
        setContentView(view)
       // binding2=RecylerRowBinding.inflate(layoutInflater)
        artList= ArrayList<Arts>()
        artAdapter=ArtAdapter(artList)

        binding.recylerView.layoutManager=LinearLayoutManager(this)
        binding.recylerView.adapter=artAdapter
        database1=this.openOrCreateDatabase("Arts", MODE_PRIVATE,null)







        try {

            val cursor=database1.rawQuery("SELECT*FROM arts",null)
            val artNameIx=cursor.getColumnIndex("artname")
            val idIx=cursor.getColumnIndex("id")
            val imageIx=cursor.getColumnIndex("image")
            while (cursor.moveToNext()){
                val name=cursor.getString(artNameIx)
                val id =cursor.getInt(idIx)
                val image1=cursor.getBlob(imageIx)
                val art=Arts(name, id,image1)
                artList.add(art)

            }
            artAdapter.notifyDataSetChanged()//veri değiştiğinde güncelle

            cursor.close()

        }catch (e:Exception){
            e.printStackTrace()

        }


    }


     override fun onCreateOptionsMenu(menu: Menu?): Boolean {
         val menuInflater=menuInflater
         menuInflater.inflate(R.menu.art_menu,menu)
         return super.onCreateOptionsMenu(menu)
     }

     override fun onOptionsItemSelected(item: MenuItem): Boolean {
         if(item.itemId==R.id.addArtItem){
             val intent=Intent(this@MainActivity,ArtActivity::class.java)
             intent.putExtra("info","new")
             startActivity(intent)
         }
         return super.onOptionsItemSelected(item)
     }


}