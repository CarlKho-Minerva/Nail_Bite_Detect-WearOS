package com.example.my_wear_app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TIMESTAMP INTEGER," +
                "$COLUMN_RESPONSE TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "responses.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "responses"
        const val COLUMN_ID = "id"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_RESPONSE = "response"
    }
}
