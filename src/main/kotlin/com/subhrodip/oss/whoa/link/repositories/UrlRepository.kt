package com.subhrodip.oss.whoa.link.repositories

import com.subhrodip.oss.whoa.link.domain.Url
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UrlRepository : JpaRepository<Url, Long> {
    fun findByShortCode(shortCode: String): Url?
}
