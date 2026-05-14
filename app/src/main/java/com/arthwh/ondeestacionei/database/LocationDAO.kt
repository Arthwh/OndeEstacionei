package com.arthwh.ondeestacionei.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.arthwh.ondeestacionei.model.Location

class LocationDAO(context: Context) : ILocationDAO {
    private val write = DatabaseHelper(context).writableDatabase
    private val read = DatabaseHelper(context).readableDatabase

    override fun save(location: Location): Boolean {
        val content = ContentValues()

        content.put(DatabaseHelper.LOCATION_COLUMN_TITLE, location.title)
        content.put(DatabaseHelper.LOCATION_COLUMN_DESCRIPTION, location.description)
        content.put(DatabaseHelper.LOCATION_COLUMN_LATITUDE, location.latitude)
        content.put(DatabaseHelper.LOCATION_COLUMN_LONGITUDE, location.longitude)
        content.put(DatabaseHelper.LOCATION_COLUMN_IMAGE_PATH, location.imagePath)

        try {
            write.insert(
                DatabaseHelper.LOCATION_TABLE_NAME,
                null,
                content
            )
            Log.i("info_db", "The Location Was Saved Successfully!")
        }catch (e: Exception){
            e.printStackTrace()
            Log.i("info_db", "An Error Occurred While Saving The Location!")
            return false
        }
        return true
    }

    @SuppressLint("Range")
    override fun getById(locationId: Int): Location? {
        val cursor = read.query(
            DatabaseHelper.LOCATION_TABLE_NAME,          // Table name
            null,                  // Columns (null for all)
            "id_location = ?",       // Selection criteria
            arrayOf(locationId.toString()), // Selection arguments
            null,                  // Group by
            null,                  // Having
            null                   // Order by
        )

        var location: Location? = null

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.LOCATION_COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOCATION_COLUMN_TITLE))
            val description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOCATION_COLUMN_DESCRIPTION))
            val latitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LOCATION_COLUMN_LATITUDE))
            val longitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LOCATION_COLUMN_LONGITUDE))
            val imagePath = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOCATION_COLUMN_IMAGE_PATH))
            val addedAt = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOCATION_COLUMN_ADDED_AT))

            // Initialize the model with extracted values
            location = Location(
                id,
                title,
                description,
                latitude,
                longitude,
                imagePath,
                addedAt,
            )
        }

        cursor.close()
        return location
    }

    fun getLastAddedLocation(): Location? {
        val cursor = read.query(
            DatabaseHelper.LOCATION_TABLE_NAME,          // Table name
            null,                  // Columns (null for all)
            null,       // Selection criteria
            null, // Selection arguments
            null,                  // Group by
            null,                  // Having
            "added_at DESC", // Order by
            "1"
        )

        var location: Location? = null

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.LOCATION_COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOCATION_COLUMN_TITLE))
            val description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOCATION_COLUMN_DESCRIPTION))
            val latitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LOCATION_COLUMN_LATITUDE))
            val longitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LOCATION_COLUMN_LONGITUDE))
            val imagePath = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOCATION_COLUMN_IMAGE_PATH))
            val addedAt = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOCATION_COLUMN_ADDED_AT))

            // Initialize the model with extracted values
            location = Location(
                id,
                title,
                description,
                latitude,
                longitude,
                imagePath,
                addedAt,
            )
        }

        cursor.close()
        return location
    }

    override fun delete(locationId: Int): Boolean {
        val args = arrayOf(locationId.toString())

        try {
            write.delete(
                DatabaseHelper.LOCATION_TABLE_NAME,
                "${DatabaseHelper.LOCATION_COLUMN_ID} = ?",
                args
            )
            Log.i("info_db", "The Location Was Removed Successfully!")
        } catch (e: Exception) {
            Log.e("info_db", "Error Removing Location: ${e.message}")
            return false
        }
        return true
    }
}