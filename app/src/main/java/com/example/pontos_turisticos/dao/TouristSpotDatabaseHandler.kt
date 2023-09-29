package com.example.pontos_turisticos.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.pontos_turisticos.entidades.TouristSpot


class TouristSpotDatabaseHandler(context: Context) : DataBaseHandler(context, "tourist_spot") {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS tourist_spot (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "description TEXT NOT NULL," +
                    "address TEXT NOT NULL," +
                    "latitude TEXT," +
                    "longitude TEXT);"

        )
    }

    fun save(rel: TouristSpot): Long {
        val registro = ContentValues()
        registro.put("name", rel.name)
        registro.put("description", rel.description)
        registro.put("address", rel.address)
        registro.put("latitude", rel.latitude)
        registro.put("longitude", rel.longitude)

        return super.save(registro)
    }
}