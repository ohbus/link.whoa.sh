package com.subhrodip.oss.whoa.link.repositories

import com.subhrodip.oss.whoa.link.domain.UrlEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface UrlRepository : JpaRepository<UrlEntity, Long> {
    fun findByShortCode(shortCode: String): UrlEntity?

    @Query("select u from UrlEntity u where u.createdAt < ?1 order by u.createdAt desc")
    fun findByCreatedAtBefore(
        cursor: OffsetDateTime,
        pageable: Pageable,
    ): List<UrlEntity>

    @Query("select u from UrlEntity u order by u.createdAt desc")
    fun findLatest(pageable: Pageable): List<UrlEntity>
}
