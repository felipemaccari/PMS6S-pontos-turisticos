package com.example.pontos_turisticos.entidades

import android.database.Cursor
import com.example.pontos_turisticos.dao.DataBaseHandler

class TouristSpot() {

    var _id: Int = 0
    var name: String = ""
    var description: String = ""
    var address: String = ""
    var latitude: String = ""
    var longitude: String = ""

    constructor(
        database: DataBaseHandler, cursor: Cursor
    ) : this() {
        this._id = (database.getColumn(cursor, "_id")?.toInt() ?: 0)
        this.name = (database.getColumn(cursor, "name") ?: "")
        this.description = (database.getColumn(cursor, "description") ?: "")
        this.address = (database.getColumn(cursor, "address") ?: "")
        this.latitude = (database.getColumn(cursor, "latitude") ?: "")
        this.longitude = (database.getColumn(cursor, "longitude") ?: "")
    }
}
