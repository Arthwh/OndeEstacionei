package com.arthwh.ondeestacionei

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.arthwh.ondeestacionei.database.LocationDAO
import com.arthwh.ondeestacionei.databinding.ActivityRegisterBinding
import com.arthwh.ondeestacionei.model.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import android.location.Location as AndroidLocation

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private var selectedImageUri: Uri? = null
    // Variável para guardar a URI temporária da câmera
    private var cameraImageUri: Uri? = null

    //Para escolher da galeria
//    private val pickImageLauncher = registerForActivityResult(
//        ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let {
//            selectedImageUri = it
//            binding.imgViewLocation.setImageURI(it) // Pré-visualização
//        }
//    }

    //Para usar a câmera
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            cameraImageUri?.let { uri ->
                selectedImageUri = uri // Repassa para a sua variável existente
                binding.imgViewLocation.setImageURI(uri) // Atualiza a pré-visualização na tela
            }
        } else {
            Toast.makeText(this, "Foto cancelada", Toast.LENGTH_SHORT).show()
        }
    }
        private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.imgViewLocation.setImageURI(it) // Pré-visualização
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient //Para obter a localização

    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getActualLocation()

        val btnBack = binding.btnBack
        val btnLoadImage = binding.btnLoadImage
        val btnSaveLocation = binding.btnSave

        btnBack.setOnClickListener {
            finish() // Encerra a tela e volta para a anterior
        }

        btnLoadImage.setOnClickListener {
            val uri = createTempImageUri()
            //Salva a referência na variável
            cameraImageUri = uri
            //Abre a câmera passando o arquivo vazio para ela preencher
            takePictureLauncher.launch(uri)

//            pickImageLauncher.launch("image/*")
        }

        btnSaveLocation.setOnClickListener {
            if (!checkFieldsCompleted()){
                Toast.makeText(
                    this,
                    "Preencha todos os campos!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else save()
        }
    }

    private fun save(){
        val title = binding.inputTitle.text.toString()
        println("Teste Titulo: $title")
        val description = binding.inputDescription.text.toString()
        println("Teste descrição: $description")
        val latitude = latitude ?: 0.0
        val longitude = longitude ?: 0.0

        // Se o usuário selecionou imagem, copia. Se não, usa vazio
        val imagePath = selectedImageUri?.let { copyImageToInternalStorage(it) } ?: ""

        val newLocation = Location(-1, title, description, latitude, longitude, imagePath, "")
        val locationDao = LocationDAO(this)

        if (locationDao.save(newLocation)) {
            Toast.makeText(this, "Localização salva!", Toast.LENGTH_SHORT).show()
            finish()
        }
        else{
            Toast.makeText(this, "Erro ao salvar localização!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyImageToInternalStorage(uri: Uri): String {
        val fileName = "location_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, fileName)

        contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return fileName // Salva apenas o nome ou o path absoluto
    }

    private fun getActualLocation(){
        checkLocationPermissions()
    }

    private fun setActualLocationOnXml(){
        val txtCoordinates = binding.txtCoordinates
        val txt = "Lat: ${latitude ?: "-"} / Long: ${longitude ?: "-"}"

        println(txt)

        txtCoordinates.text = txt
    }

    private fun checkFieldsCompleted(): Boolean{
        return true
    }

    private fun getLastKnownLocation() {
        // Note: Always check for permissions before calling this
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: AndroidLocation? ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    println("Current Location: $latitude, $longitude")
                    setActualLocationOnXml()
                } else {
                    // Location can be null if location services are off or cache is cleared
                    println("Location not found")
                }
            }
            .addOnFailureListener { e ->
                println("Error: ${e.message}")
            }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            // Permission granted, proceed to get location
            getLastKnownLocation()
        } else {
            // Permission denied, show user a message explaining why you need it
            println("Location permission denied by user.")
        }
    }

    private fun checkLocationPermissions() {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocation || hasCoarseLocation) {
            // Permissions are already granted
            getLastKnownLocation()
        } else {
            // Request both fine and coarse permissions
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Função para criar um arquivo temporário vazio onde a câmera vai salvar a foto
    private fun createTempImageUri(): Uri {
        val tempFile = File.createTempFile("localizacao_", ".jpg", cacheDir)
        return FileProvider.getUriForFile(this, "${packageName}.fileprovider", tempFile)
    }
}