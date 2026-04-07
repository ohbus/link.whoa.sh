package com.subhrodip.oss.whoa.link.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Objects

@Entity
@Table(name = "url_analytics")
class UrlAnalyticsEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false)
    val urlEntity: UrlEntity,
    @Column(name = "user_agent")
    val userAgent: String?,
    @Column(name = "ip_address")
    val ipAddress: String?,
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UrlAnalyticsEntity) return false
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = Objects.hash(id)

    override fun toString(): String = "UrlAnalyticsEntity(id=$id, ipAddress='$ipAddress')"
}
