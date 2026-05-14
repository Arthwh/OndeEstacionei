package com.arthwh.ondeestacionei

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.arthwh.ondeestacionei.database.LocationDAO
import com.arthwh.ondeestacionei.databinding.ActivityHomeBinding;
import com.arthwh.ondeestacionei.model.Location

class HomeActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume(){
        super.onResume()

        getActiveLocation()
    }

    private fun getActiveLocation(){
        val locationDao = LocationDAO(this)
        val activeLocation: Location? = locationDao.getLastAddedLocation()

        if (activeLocation != null){
            println(activeLocation.toString())
            val intent = Intent(this, LocationViewActivity::class.java)

            intent.putExtra("LOCATION", activeLocation)

            startActivity(intent)
        }
    }

}