package com.subhrodip.oss.whoa.link.repositories

import com.subhrodip.oss.whoa.link.domain.Url
import com.subhrodip.oss.whoa.link.domain.UrlAnalytics
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UrlAnalyticsRepository : JpaRepository<UrlAnalytics, Long> {
    fun countByUrl(url: Url): Long
}
