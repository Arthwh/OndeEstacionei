package com.arthwh.ondeestacionei

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.arthwh.ondeestacionei.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.core.net.toUri

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding by lazy {
        ActivityMapsBinding.inflate(layoutInflater)
    }

    private var googleMap: GoogleMap? = null
    private var savedLatitude: Double? = null
    private var savedLongitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish() // Encerra a tela e volta para a anterior
        }

        binding.fabGetRouteToLocation.setOnClickListener {
            // Cria uma URI para o Google Maps traçar a rota entre os dois pontos
            val gmmIntentUri = "google.navigation:q=$savedLatitude,$savedLongitude&mode=w".toUri() // 'w' para walking
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps") // Garante que abrirá o app do Maps

            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            }
        }

        savedLatitude = intent.getSerializableExtra("LATITUDE") as Double
        savedLongitude = intent.getSerializableExtra("LONGITUDE") as Double


        // 2. Inicializa o MapView e passa o ciclo de vida
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
    }

    //Método que roda quando o mapa está pronto para ser usado
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Verifica se as permissões foram dadas
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            // Ativa o "ponto azul" e o botão de centralizar
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
        }

            // Cria o objeto com a latitude e longitude
            val coordenadas = LatLng(savedLatitude?: 0.0 , savedLongitude?: 0.0)

            // Adiciona um marcador vermelho no local
            googleMap?.addMarker(
                MarkerOptions()
                    .position(coordenadas)
                    .title("Seu veículo está aqui")
            )

            // Move a câmera para a coordenada com um nível de zoom
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 17f))
    }
}