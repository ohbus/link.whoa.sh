package com.subhrodip.oss.whoa.link.repositories

import com.subhrodip.oss.whoa.link.domain.UrlAnalyticsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UrlAnalyticsRepository : JpaRepository<UrlAnalyticsEntity, Long> {
    @Query("select count(u) from UrlAnalyticsEntity u where u.urlEntity.id = ?1")
    fun countByUrlEntityId(id: Long): Long
}
