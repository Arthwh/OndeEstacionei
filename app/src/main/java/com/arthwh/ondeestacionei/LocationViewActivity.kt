package com.arthwh.ondeestacionei

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.arthwh.ondeestacionei.database.LocationDAO
import com.arthwh.ondeestacionei.databinding.ActivityLocationViewBinding
import com.arthwh.ondeestacionei.model.Location
import java.io.File

class LocationViewActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLocationViewBinding.inflate(layoutInflater)
    }

    lateinit var location: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        location = (intent.extras?.getSerializable("LOCATION") ?: finish()) as Location

        binding.btnDelete.setOnClickListener {
            delete(location.id)
        }

        binding.btnLoadMap.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)

            intent.putExtra("LATITUDE", location.latitude)
            intent.putExtra("LONGITUDE", location.longitude)

            startActivity(intent)
        }

        setDataToXml()
    }

    private fun setDataToXml(){
        val txtTitleLocation = binding.txtTitleLocation
        val txtCoordinates = binding.txtCoordinates
        val txtDescriptionLocation = binding.txtDescriptionLocation
        val imgViewLocation = binding.imgViewLocation

        txtTitleLocation.text = location.title ?: ""
        txtCoordinates.text = "Lat: ${location.latitude} / Long: ${location.longitude}"
        txtDescriptionLocation.text = location.description

        if (location.imagePath.isNotEmpty()) {
            //Tenta carregar como arquivo do armazenamento interno
            val imgFile = File(this.filesDir, location.imagePath)

            if (imgFile.exists()) {
                imgViewLocation.setImageURI(android.net.Uri.fromFile(imgFile))
            }
        } else {
            imgViewLocation.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    private fun delete(locationId: Int){
        val locationDao = LocationDAO(this)

        if (locationDao.delete(locationId)){
            Toast.makeText(
                this,
                "Localização removida com sucesso!",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }else{
            Toast.makeText(
                this,
                "Houve um erro ao remover a localização!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}