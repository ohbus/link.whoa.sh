package com.subhrodip.oss.whoa.link.repositories

import com.subhrodip.oss.whoa.link.domain.UrlEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UrlRepository : JpaRepository<UrlEntity, Long> {
    fun findByShortCode(shortCode: String): UrlEntity?
}
