package com.subhrodip.oss.whoa.link.repositories

import com.subhrodip.oss.whoa.link.domain.UrlAnalyticsEntity
import com.subhrodip.oss.whoa.link.dto.ClickCountProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface UrlAnalyticsRepository : JpaRepository<UrlAnalyticsEntity, Long> {
    @Query("select count(u) from UrlAnalyticsEntity u where u.urlEntity.id = ?1")
    fun countByUrlEntityId(id: Long): Long

    @Query(
        """
        select u.shortCode as shortCode, count(a) as totalClicks 
        from UrlEntity u left join UrlAnalyticsEntity a on a.urlEntity = u 
        where u.shortCode in ?1 group by u.shortCode
        """,
    )
    fun countByShortCodes(shortCodes: List<String>): List<ClickCountProjection>

    @Query("select count(a) from UrlAnalyticsEntity a")
    fun countAllClicks(): Long

    @Query("select distinct u.shortCode from UrlAnalyticsEntity a join a.urlEntity u where u.shortCode in ?1 and a.createdAt > ?2")
    fun findShortCodesWithActivitySince(
        shortCodes: List<String>,
        since: OffsetDateTime,
    ): List<String>
}
