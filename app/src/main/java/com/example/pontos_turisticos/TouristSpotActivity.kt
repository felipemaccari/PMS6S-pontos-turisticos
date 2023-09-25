package com.example.pontos_turisticos

import android.content.SharedPreferences
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

class TouristSpotActivity : AppCompatActivity() {
    private val binding by lazy { ActivityTouristSpotBinding.inflate(layoutInflater) }
    private val touristSpotDatabaseHandler by lazy { TouristSpotDatabaseHandler(this) }
    private var touristSpot: TouristSpot = TouristSpot()
    private var isFormDirt: Boolean = false

    private lateinit var tiName : TextInputEditText
    private lateinit var tiDescription : TextInputEditText
    private lateinit var tiAddress : TextInputEditText
    private lateinit var btSave : Button

    private lateinit var sharedPreferences : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tourist_spot)

        tiName = findViewById(R.id.tiName);
        tiDescription = findViewById(R.id.tiDescription);
        tiAddress = findViewById(R.id.tiAddress);
        btSave = findViewById(R.id.btSave);

        touristSpot._id = intent.getIntExtra("id", 0)

//        if (touristSpot._id != 0) {
//            binding.toolbar.title = getString(R.string.edit)
//            findTouristPoint()
//        }
    }

    private fun findTouristPoint() {
        val cursor = touristSpotDatabaseHandler.findOneBy("_id", touristSpot._id.toString())
        if (ObjectUtils.isNotEmpty(cursor) && cursor != null && cursor.moveToNext()) {
            touristSpot = TouristSpot(touristSpotDatabaseHandler, cursor)
            tiName.setText(touristSpot.name)
            tiDescription.setText(touristSpot.description)
            tiAddress.setText(touristSpot.address)

        }
    }

    fun saveOnClick(view: View) {
        if (validateForm()) {
            touristSpot.name = tiName.getText().toString().trim()
            touristSpot.description = tiDescription.getText().toString().trim()
            touristSpot.address = tiAddress.getText().toString().trim()

            intent.putExtra("op", "save")
            touristSpotDatabaseHandler.save(touristSpot)
            finish()
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