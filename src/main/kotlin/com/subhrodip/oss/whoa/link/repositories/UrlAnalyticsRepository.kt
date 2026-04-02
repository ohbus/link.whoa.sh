package com.subhrodip.oss.whoa.link.repositories

import com.subhrodip.oss.whoa.link.domain.UrlAnalyticsEntity
import com.subhrodip.oss.whoa.link.dto.ClickCountByIdProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface UrlAnalyticsRepository : JpaRepository<UrlAnalyticsEntity, Long> {
    @Query("select count(u) from UrlAnalyticsEntity u where u.urlEntity.id = ?1")
    fun countByUrlEntityId(id: Long): Long

    /**
     * Optimized bulk count using IDs to avoid JOIN with the URLs table.
     * a.urlEntity.id maps directly to the url_id FK column in url_analytics.
     */
    @Query(
        """
        select a.urlEntity.id as urlId, count(a) as totalClicks 
        from UrlAnalyticsEntity a 
        where a.urlEntity.id in ?1 group by a.urlEntity.id
        """,
    )
    fun countByUrlIds(ids: List<Long>): List<ClickCountByIdProjection>

    @Query("select count(a) from UrlAnalyticsEntity a")
    fun countAllClicks(): Long

    /**
     * Optimized delta sync using IDs to avoid JOIN with the URLs table.
     */
    @Query("select distinct a.urlEntity.id from UrlAnalyticsEntity a where a.urlEntity.id in ?1 and a.createdAt > ?2")
    fun findIdsWithActivitySince(
        ids: List<Long>,
        since: OffsetDateTime,
    ): List<Long>
}
