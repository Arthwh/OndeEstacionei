package com.arthwh.ondeestacionei.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, VERSION
) {

    companion object {
        const val DATABASE_NAME = "OndeEstacionei.db"
        const val VERSION = 1
        const val LOCATION_TABLE_NAME = "location"
        const val LOCATION_COLUMN_ID = "id_location"
        const val LOCATION_COLUMN_TITLE = "title"
        const val LOCATION_COLUMN_DESCRIPTION = "description"
        const val LOCATION_COLUMN_LATITUDE = "latitude"
        const val LOCATION_COLUMN_LONGITUDE = "longitude"
        const val LOCATION_COLUMN_IMAGE_PATH = "image_path"
        const val LOCATION_COLUMN_ADDED_AT = "added_at"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE IF NOT EXISTS $LOCATION_TABLE_NAME(" +
                "$LOCATION_COLUMN_ID INTEGER not NULL PRIMARY KEY AUTOINCREMENT," +
                "$LOCATION_COLUMN_TITLE VARCHAR(100)," +
                "$LOCATION_COLUMN_DESCRIPTION TEXT," +
                "$LOCATION_COLUMN_LATITUDE INT," +
                "$LOCATION_COLUMN_LONGITUDE VARCHAR(100)," +
                "$LOCATION_COLUMN_IMAGE_PATH VARCHAR(255)," +
                "$LOCATION_COLUMN_ADDED_AT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ");"

        try {
            db?.execSQL( sql )
            Log.i("info_db", "The Table Was Created Successfully!")
        }catch (e: Exception){
            e.printStackTrace()
            Log.i("info_db", "An Error Occurred While Creating The Table!")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}
