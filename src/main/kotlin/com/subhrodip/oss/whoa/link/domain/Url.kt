package com.subhrodip.oss.whoa.link.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "urls")
data class Url(
    @Column(name = "original_url", nullable = false)
    val originalUrl: String,
    @Column(name = "short_code", nullable = false, unique = true)
    val shortCode: String,
) : BaseEntity()
