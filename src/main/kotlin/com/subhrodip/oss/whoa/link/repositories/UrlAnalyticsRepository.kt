package com.subhrodip.oss.whoa.link.repositories

import com.subhrodip.oss.whoa.link.domain.UrlAnalyticsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UrlAnalyticsRepository : JpaRepository<UrlAnalyticsEntity, Long> {
    @Query("select count(u) from UrlAnalyticsEntity u where u.urlEntity.id = ?1")
    fun countByUrlEntityId(id: Long): Long

    @Query("select u.shortCode, count(a) from UrlEntity u left join UrlAnalyticsEntity a on a.urlEntity = u where u.shortCode in ?1 group by u.shortCode")
    fun countByShortCodes(shortCodes: List<String>): List<Array<Any>>

    @Query("select count(a) from UrlAnalyticsEntity a")
    fun countAllClicks(): Long
}
