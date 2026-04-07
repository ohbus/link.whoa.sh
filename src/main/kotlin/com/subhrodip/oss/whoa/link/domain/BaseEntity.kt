package com.subhrodip.oss.whoa.link.domain

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Transient
import java.time.OffsetDateTime

@MappedSuperclass
abstract class BaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pooled_sequence_generator")
    @SequenceGenerator(
        name = "pooled_sequence_generator",
        sequenceName = "global_id_sequence",
        allocationSize = 100,
    )
    open var id: Long = 0,
    @Column(name = "created_at", nullable = false)
    open var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Transient
    open val isNew: Boolean = true,
)
