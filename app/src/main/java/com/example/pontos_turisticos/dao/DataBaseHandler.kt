package com.example.pontos_turisticos.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.pontos_turisticos.utils.ObjectUtils


abstract class DataBaseHandler protected constructor(
    context: Context?,
    val tablename: String
) :
    SQLiteOpenHelper(context, "potosturisticos", null, 1) {

    init {
        afterCreate()
    }

    private fun afterCreate() {
        onCreate(this.writableDatabase)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $tablename;")
    }

    protected fun save(values: ContentValues?): Long {
        Log.i(this.tablename, "save")

        return this.writableDatabase.insert(tablename, null, values)
    }

    protected fun update(registro: ContentValues?, id: Int): Int {
        Log.i(this.tablename, "update > $id")
        return this.writableDatabase.update(tablename, registro, "_id = ?", arrayOf(id.toString()))
    }

    fun delete(id: Int): Int {
        Log.i(this.tablename, "delete > $id")
        return this.writableDatabase.delete(tablename, "_id = ?", arrayOf(id.toString()))
    }

    fun findOneBy(field: String, value: String): Cursor? {
        val registros = this.writableDatabase.query(
            tablename,
            null, "$field = ?", arrayOf(value),
            null, null, null
        )
        if (ObjectUtils.isNotEmpty(registros) && registros.moveToNext()) {
            registros.moveToPrevious()
            return registros
        }
        return null

    }

    fun findList(orderBy: String? = null, whereClause: String? = null): Cursor? {
        val registros =
            this.writableDatabase.query(tablename, null, whereClause, null, null, null, orderBy)
        if (ObjectUtils.isNotEmpty(registros) && registros.moveToNext()) {
            registros.moveToPrevious()
            return registros
        }
        return null
    }

    fun getColumn(cursor: Cursor, columnName: String?): String? {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName))
    }
}