package com.example.pontos_turisticos

import android.Manifest
import android.app.DownloadManager.Request
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pontos_turisticos.dao.TouristSpotDatabaseHandler
import com.example.pontos_turisticos.databinding.ActivityTouristSpotBinding
import com.example.pontos_turisticos.entidades.TouristSpot
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pontos_turisticos.utils.ObjectUtils
import com.google.android.material.textfield.TextInputEditText
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.helpers.Util
import java.io.ByteArrayOutputStream

class TouristSpotActivity : AppCompatActivity(), LocationListener {
    private val binding by lazy { ActivityTouristSpotBinding.inflate(layoutInflater) }
    private val touristSpotDatabaseHandler by lazy { TouristSpotDatabaseHandler(this) }
    private var touristSpot: TouristSpot = TouristSpot()
    private var isFormDirt: Boolean = false
    private var capturedImage: Bitmap? = null
    private lateinit var tiName : TextInputEditText
    private lateinit var tiDescription : TextInputEditText
    private lateinit var tiAddress : TextInputEditText
    private lateinit var btSave : Button
    private lateinit var btnExcluir : Button
    private lateinit var btnGetPhoto: Button
    private lateinit var ivMap : ImageView
    private lateinit var ivSpotPhoto : ImageView
    private lateinit var formattedAddress : String
    private val apiKey = "AIzaSyCi_c4q90raXN_EEgmQ21-I0ya4nsQvkpY"
    private var register =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                image: Bitmap? -> image?.let {
                    capturedImage = image
                    val ivFoto = findViewById<ImageView>(R.id.ivSpotPhoto)
                    ivFoto.setImageBitmap(image)
                }
        }

    private var latitude : String? = null
    private var longitude : String? = null

    private lateinit var locationManager : LocationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tourist_spot)

        tiName = findViewById(R.id.tiName);
        tiDescription = findViewById(R.id.tiDescription);
        tiAddress = findViewById(R.id.tiAddress);
        btSave = findViewById(R.id.btSave);
        btnGetPhoto = findViewById(R.id.btnGetPhoto)
        ivMap = findViewById(R.id.ivMap)
        ivSpotPhoto = findViewById(R.id.ivSpotPhoto)
        btnExcluir = findViewById(R.id.btnExcluir)

        touristSpot._id = intent.getIntExtra("id", 0)

        if (touristSpot._id == 0){
            btnExcluir.visibility = View.GONE
        }

        if (touristSpot._id == 0){
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
            }
            locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0,  0f, this )
        }

        if (touristSpot._id != 0) {
            findTouristPoint()
        }
    }

    override fun onLocationChanged(p0: Location) {
        latitude = p0.latitude.toString()
        longitude = p0.longitude.toString()
        getAddressFromCoordinates()
    }

    private fun findTouristPoint() {
        val cursor = touristSpotDatabaseHandler.findOneBy("_id", touristSpot._id.toString())
        if (ObjectUtils.isNotEmpty(cursor) && cursor != null && cursor.moveToNext()) {
            touristSpot = TouristSpot(touristSpotDatabaseHandler, cursor)
            tiName.setText(touristSpot.name)
            tiDescription.setText(touristSpot.description)
            tiAddress.setText(touristSpot.address)

            val client = HttpClient(CIO)

            val zoom = 15
            val size = "600x400"
            val mapUrl = "https://maps.googleapis.com/maps/api/staticmap?center=${touristSpot.latitude},${touristSpot.longitude}&zoom=$zoom&size=$size&key=$apiKey"

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response: HttpResponse = client.get(mapUrl)
                    val bytes = response.readBytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    CoroutineScope(Dispatchers.Main).launch {
                        ivMap.setImageBitmap(bitmap)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    client.close()
                }
            }
        }
    }

    private fun getAddressFromCoordinates() {
        val client = HttpClient(CIO)
        val apiUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$latitude,$longitude&key=$apiKey"
        CoroutineScope(Dispatchers.IO).launch {
            val response: String = client.get(apiUrl)
            val json = Json.parseToJsonElement(response).jsonObject

            val results = json["results"]!!.jsonArray
            if (results.isNotEmpty()) {
                formattedAddress = results[0].jsonObject["formatted_address"]!!.jsonPrimitive.content
                updateUI(formattedAddress)
                val pele = 1
            } else {
                // Não foram encontrados resultados
            }
        }
    }

    private suspend fun updateUI(address: String) {
        withContext(Dispatchers.Main) {
            tiAddress.setText(address)
        }
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap?): ByteArray? {
        if (bitmap == null) {
            return null
        }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun saveOnClick(view: View) {
        if (validateForm()) {
            touristSpot.name = tiName.getText().toString().trim()
            touristSpot.description = tiDescription.getText().toString().trim()
            touristSpot.address = tiAddress.getText().toString().trim()
            touristSpot.latitude = latitude.toString()
            touristSpot.longitude = longitude.toString()

            val imageByteArray = convertBitmapToByteArray(capturedImage)
            if (imageByteArray != null) {
                touristSpot.spotImage = imageByteArray
            }

            val apiUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=${touristSpot.address.replace(" ", "+")}&key=$apiKey"

            val client = HttpClient(CIO)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response: String = client.get(apiUrl)
                    val json = Json.parseToJsonElement(response).jsonObject
                    val location = json["results"]!!.jsonArray[0].jsonObject["geometry"]!!.jsonObject["location"]!!.jsonObject
                    val new_latitude = location["lat"]!!.jsonPrimitive.double
                    val new_longitude = location["lng"]!!.jsonPrimitive.double
                    touristSpot.latitude = new_latitude.toString()
                    touristSpot.longitude = new_longitude.toString()

                    touristSpotDatabaseHandler.save(touristSpot)
                    finish()

                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    client.close()
                }
            }
        }
    }


    private fun validateForm(): Boolean {
        return validateName() && validateDescription() && validateAddress() && isFormDirt
    }

    private fun validateName(): Boolean {
        isFormDirt = true
        touristSpot.name = tiName.text.toString()
        val notIsValid = ObjectUtils.isEmpty(touristSpot.name) || touristSpot.name.length < 5
        if (notIsValid) {
            binding.tiName.error = "Campo Inválido"
        } else {
            touristSpot.name =
                touristSpot.name.substring(0, 1).uppercase() + touristSpot.name.substring(1)
        }
        Log.i(
            this.localClassName,
            "Campo \"Name\" é ${if (notIsValid) "Inválido" else "Válido"}"
        )
        return !notIsValid
    }

    private fun validateDescription(): Boolean {
        isFormDirt = true
        touristSpot.description = tiDescription.text.toString()
        val notIsValid = ObjectUtils.isEmpty(touristSpot.description) || touristSpot.description.length < 5
        if (notIsValid) {
            binding.tiDescription.error = "Campo Inválido"
        } else {
            touristSpot.description =
                touristSpot.description.substring(0, 1).uppercase() + touristSpot.description.substring(1)
        }
        Log.i(
            this.localClassName,
            "Campo \"Descrição\" é ${if (notIsValid) "Inválido" else "Válido"}"
        )
        return !notIsValid
    }

    private fun validateAddress(): Boolean {
        isFormDirt = true
        touristSpot.address = tiAddress.text.toString()
        val notIsValid = ObjectUtils.isEmpty(touristSpot.address) || touristSpot.address.length < 5
        if (notIsValid) {
            binding.tiAddress.error = "Campo Inválido"
        } else {
            touristSpot.address =
                touristSpot.address.substring(0, 1).uppercase() + touristSpot.address.substring(1)
        }
        Log.i(
            this.localClassName,
            "Campo \"Endereço\" é ${if (notIsValid) "Inválido" else "Válido"}"
        )
        return !notIsValid
    }

    fun btnGetPhotoOnClick(view: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).let { register.launch(null) }

    }

    fun excluirOnclick(view: View) {
        if(touristSpot._id != 0){
            touristSpotDatabaseHandler.delete(touristSpot._id)
            finish()
        }
    }
}