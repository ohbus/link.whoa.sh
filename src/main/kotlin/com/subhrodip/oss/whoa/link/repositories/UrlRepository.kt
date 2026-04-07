package com.subhrodip.oss.whoa.link.repositories

import com.subhrodip.oss.whoa.link.domain.UrlEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface UrlRepository : JpaRepository<UrlEntity, Long> {
    fun findByShortCode(shortCode: String): UrlEntity?

    @Query(value = "SELECT nextval('short_code_seq')", nativeQuery = true)
    fun getNextShortCodeId(): Long

    @Modifying
    @Query("update UrlEntity u set u.totalClicks = u.totalClicks + 1 where u.id = :id")
    fun incrementClickCount(
        @Param("id") id: Long,
    )

    @Query("select u from UrlEntity u where u.createdAt < :cursor order by u.createdAt desc")
    fun findByCreatedAtBefore(
        @Param("cursor") cursor: OffsetDateTime,
        pageable: Pageable,
    ): List<UrlEntity>

    @Query("select u from UrlEntity u order by u.createdAt desc")
    fun findLatest(pageable: Pageable): List<UrlEntity>
}
