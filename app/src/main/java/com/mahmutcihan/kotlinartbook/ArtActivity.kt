package com.mahmutcihan.kotlinartbook

import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import com.google.android.material.snackbar.Snackbar
import com.mahmutcihan.kotlinartbook.databinding.ActivityArtBinding
import com.mahmutcihan.kotlinartbook.databinding.ActivityMainBinding
import com.mahmutcihan.kotlinartbook.databinding.RecylerRowBinding
import java.io.ByteArrayOutputStream

class ArtActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArtBinding
    private lateinit var binding2:RecylerRowBinding
    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitMap:Bitmap?=null
    private lateinit var database: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityArtBinding.inflate(layoutInflater)
        binding2=RecylerRowBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)



        database=this.openOrCreateDatabase("Arts", MODE_PRIVATE,null)
        registerLauncher()

        val intent=intent
        val info=intent.getStringExtra("info")


        if(info.equals("new")){
            binding.artName.setText("")
            binding.year.setText("")
            binding.artistName.setText("")
            binding.button.visibility=View.VISIBLE
            val selectedImageBackground = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.click)
            binding.imageView.setImageBitmap(selectedImageBackground)



        }else{
            binding.button.visibility=View.INVISIBLE
            binding.artName.isEnabled=false
            binding.year.isEnabled=false
            binding.artistName.isEnabled=false
            binding.imageView.isEnabled=false

            val selectedId=intent.getIntExtra("id",1)

            val cursor=database.rawQuery("SELECT*FROM arts WHERE id=?", arrayOf(selectedId.toString()))


            val artNameIx=cursor.getColumnIndex("artname")
            val artistNameIx=cursor.getColumnIndex("artistname")
            val yearIx=cursor.getColumnIndex("year")
            val imageIx=cursor.getColumnIndex("image")
            while (cursor.moveToNext()){
                binding.artName.setText(cursor.getString(artNameIx))
                binding.artistName.setText(cursor.getString(artistNameIx))
                binding.year.setText(cursor.getString(yearIx))
                val byteArray=cursor.getBlob(imageIx)
                val bitmap=BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                binding.imageView.setImageBitmap(bitmap)






                



            }
            cursor.close()

        }
    }

    fun save(view:View){
        val artName=binding.artName.text.toString()
        val artistName=binding.artistName.text.toString()
        val year=binding.year.text.toString()


        if (selectedBitMap!=null){
            val smallBitmap=makeSmallerBitmap(selectedBitMap!!,300)
            val outputStream=ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream) //bitleri veritabanına kaydetmek için byte dizine kaydettik
            val byteArray=outputStream.toByteArray()

            try {

                database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY,artname VARCHAR,artistname VARCHAR,year VARCHAR,image BLOB)")
                val sqlString="INSERT INTO arts(artname,artistname,year,image) VALUES (?,?,?,?)"
                val statement=database.compileStatement(sqlString)
                statement.bindString(1,artName)
                statement.bindString(2,artistName)
                statement.bindString(3,year)
                statement.bindBlob(4,byteArray)
                statement.execute()




            }catch (e:Exception){
                e.printStackTrace()
            }

            val intent2=Intent(this@ArtActivity,MainActivity::class.java)
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //BUNDAN ÖNCEKİ TÜM AKTİVİTELERİ KAPAT
            startActivity(intent2)


        }




    }
    private fun makeSmallerBitmap(image:Bitmap,maximumSize:Int):Bitmap{
        var width=image.width
        var height=image.height

        val bitmapRatio:Double=width.toDouble()/height.toDouble()
        if(bitmapRatio>1){
            width=maximumSize
            val scaledHeight=width/bitmapRatio
            height=scaledHeight.toInt()
        }else{
            height=maximumSize
            val scaledWidth=height*bitmapRatio
            width=scaledWidth.toInt()
        }

        return Bitmap.createScaledBitmap(image,width,height,true)

    }

    fun selectImage(view:View){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
                //eğer daha önce izin verilmediyse
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_MEDIA_IMAGES)){
                    //izin ver diye gösterilicek bir snack bar kullanmalımıyız
                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                        permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)


                    }).show()
                }else{
                    //izin iste
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }


            }else{
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

                //eğer izin verildiyse intent yap galeriden foto seç

            }

        }else{
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                //eğer daha önce izin verilmediyse
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //izin ver diye gösterilicek bir snack bar kullanmalımıyız
                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)


                    }).show()
                }else{
                    //izin iste
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }


            }else{
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

                //eğer izin verildiyse intent yap galeriden foto seç

            }

        }







    }

    private fun registerLauncher(){ //izin isteme fonks.

        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if(result.resultCode== RESULT_OK){ //EĞER İZİN VERİLDİYSE
                val intentFromResult=result.data
                if(intentFromResult!=null){
                    val imageData=intentFromResult.data
                    //binding.imageView.setImageURI(imageData)
                    if(imageData!=null){
                        try {
                            if(Build.VERSION.SDK_INT>=28){
                                val source=ImageDecoder.createSource(this@ArtActivity.contentResolver,imageData)
                                selectedBitMap=ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitMap)


                            }else{
                                selectedBitMap=MediaStore.Images.Media.getBitmap(contentResolver,imageData)
                                binding.imageView.setImageBitmap(selectedBitMap)
                            }


                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }


            }

        }

        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if(result){
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)


            }else{
                Toast.makeText(this@ArtActivity,"Permission needed",Toast.LENGTH_LONG).show()
            }

        }

    }





}