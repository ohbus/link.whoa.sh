package com.subhrodip.oss.whoa.link.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.Objects

@Entity
@Table(name = "urls")
class UrlEntity(
    @Column(name = "original_url", nullable = false)
    val originalUrl: String,
    @Column(name = "short_code", nullable = false, unique = true)
    val shortCode: String,
    @Column(name = "total_clicks", nullable = false)
    var totalClicks: Long = 0,
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UrlEntity) return false
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = Objects.hash(id)

    override fun toString(): String = "UrlEntity(id=$id, shortCode='$shortCode')"
}
