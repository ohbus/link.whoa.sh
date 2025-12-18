package com.subhrodip.oss.whoa.link.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "url_analytics")
data class UrlAnalyticsEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false)
    val urlEntity: UrlEntity,
    @Column(name = "user_agent")
    val userAgent: String?,
    @Column(name = "ip_address")
    val ipAddress: String?,
) : BaseEntity()
