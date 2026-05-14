package com.arthwh.ondeestacionei.database

import com.arthwh.ondeestacionei.model.Location

interface ILocationDAO {
    fun save( location: Location ): Boolean
    fun getById( locationId: Int ): Location?
    fun delete( locationId: Int ): Boolean
}