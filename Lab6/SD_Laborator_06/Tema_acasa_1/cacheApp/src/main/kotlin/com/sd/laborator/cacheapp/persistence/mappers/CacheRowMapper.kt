package com.sd.laborator.cacheapp.persistence.mappers

import com.sd.laborator.cacheapp.persistence.entities.CacheEntity
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.jvm.Throws

class CacheRowMapper : RowMapper<CacheEntity?> {
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): CacheEntity? {
        return CacheEntity(rs.getInt("id"), rs.getString("timestamp"), rs.getString("query"), rs.getString("result"))
    }

}