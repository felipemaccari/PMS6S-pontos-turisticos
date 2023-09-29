package com.example.pontos_turisticos

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.example.pontos_turisticos.dao.TouristSpotDatabaseHandler
import com.example.pontos_turisticos.databinding.ActivityTouristSpotBinding
import com.example.pontos_turisticos.entidades.TouristSpot
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.json.JSONObject
import java.io.File
import java.util.Locale
import javax.net.ssl.HttpsURLConnection

class TouristSpotActivity : AppCompatActivity() {
    private val binding by lazy { ActivityTouristSpotBinding.inflate(layoutInflater) }
    private val touristSpotDatabaseHandler by lazy { TouristSpotDatabaseHandler(this) }
    private var touristSpot: TouristSpot = TouristSpot()
    private var isFormDirt: Boolean = false

    private lateinit var tiName : TextInputEditText
    private lateinit var tiDescription : TextInputEditText
    private lateinit var tiAddress : TextInputEditText
    private lateinit var btSave : Button
    private lateinit var ivMap : ImageView
    private val apiKey = "AIzaSyBaxM8fm7w36VVjLCuz8sSxxWDQ7rbdECE"

    private lateinit var sharedPreferences : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tourist_spot)

        tiName = findViewById(R.id.tiName);
        tiDescription = findViewById(R.id.tiDescription);
        tiAddress = findViewById(R.id.tiAddress);
        btSave = findViewById(R.id.btSave);
        ivMap = findViewById(R.id.ivMap)

        touristSpot._id = intent.getIntExtra("id", 0)

        if (touristSpot._id != 0) {
            findTouristPoint()
        }
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

    fun saveOnClick(view: View) {
        if (validateForm()) {
            touristSpot.name = tiName.getText().toString().trim()
            touristSpot.description = tiDescription.getText().toString().trim()
            touristSpot.address = tiAddress.getText().toString().trim()

            val apiUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=${touristSpot.address.replace(" ", "+")}&key=$apiKey"

            val client = HttpClient(CIO)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response: String = client.get(apiUrl)
                    val json = Json.parseToJsonElement(response).jsonObject
                    val location = json["results"]!!.jsonArray[0].jsonObject["geometry"]!!.jsonObject["location"]!!.jsonObject
                    val latitude = location["lat"]!!.jsonPrimitive.double
                    val longitude = location["lng"]!!.jsonPrimitive.double
                    touristSpot.latitude = latitude.toString()
                    touristSpot.longitude = longitude.toString()

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
}