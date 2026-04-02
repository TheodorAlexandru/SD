package com.sd.laborator.cacheapp.persistence.repositories

import com.sd.laborator.cacheapp.persistence.entities.CacheEntity
import com.sd.laborator.cacheapp.persistence.interfaces.ICachingRepository
import com.sd.laborator.cacheapp.persistence.mappers.CacheRowMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.UncategorizedSQLException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class CachingRepository : ICachingRepository{
    @Autowired
    private lateinit var _jdbcTemplate: JdbcTemplate
    private var _rowMapper: RowMapper<CacheEntity?> = CacheRowMapper()

    override fun createCacheTable() {
        _jdbcTemplate.execute(
            """CREATE TABLE IF NOT EXISTS cache(
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        timestamp VARCHAR(100) UNIQUE,
                                        query VARCHAR(100) UNIQUE,
                                        result VARCHAR(100) UNIQUE
                                        )"""
        )
    }

    override fun update(item: CacheEntity){
        try{

            _jdbcTemplate.update("UPDATE cache SET timestamp = ?, query = ?, result = ? WHERE id = ?", item.gsTimestamp, item.gsQuery, item.gsResult, item.gsId)
        } catch (e: UncategorizedSQLException){
            println("An error has occurred in ${this.javaClass.name}.add")
        }
    }

    override fun add(item: CacheEntity) {
        try{

            _jdbcTemplate.update("INSERT INTO cache (timestamp, query, result) VALUES (?, ?, ?)", item.gsTimestamp, item.gsQuery, item.gsResult)
        } catch (e: UncategorizedSQLException){
            println("An error has occurred in ${this.javaClass.name}.add")
        }
    }

    override fun getByQuery(query: String): CacheEntity? {
        return try{
            _jdbcTemplate.queryForObject("SELECT * FROM cache WHERE query = ?", _rowMapper, query)
        }catch (e: EmptyResultDataAccessException){
            null
        }
    }
}