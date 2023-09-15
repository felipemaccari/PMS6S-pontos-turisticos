package com.example.pontos_turisticos.entidades

import android.database.Cursor
import com.example.pontos_turisticos.dao.DataBaseHandler

class TouristSpot() {

    var _id: Int = 0
    var name: String = ""
    var description: String = ""
    var address: String = ""
    var latitude: Float = 0.0F
    var longitute: Float = 0.0F

    constructor(
        database: DataBaseHandler, cursor: Cursor
    ) : this() {
        this._id = (database.getColumn(cursor, "_id")?.toInt() ?: 0)
        this.name = (database.getColumn(cursor, "name") ?: "")
        this.description = (database.getColumn(cursor, "description") ?: "")
        this.address = (database.getColumn(cursor, "address") ?: "")
        this.latitude = ((database.getColumn(cursor, "latitude")?.toFloat() ?: 0.0).toFloat())
        this.longitute = ((database.getColumn(cursor, "latitude")?.toFloat() ?: 0.0).toFloat())
    }
}
