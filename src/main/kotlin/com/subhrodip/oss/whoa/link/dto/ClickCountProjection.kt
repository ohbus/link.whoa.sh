package com.subhrodip.oss.whoa.link.dto

/**
 * Interface-based projection for optimized bulk click counting.
 * This allows Spring Data JPA to generate an optimized proxy for SQL results,
 * avoiding the overhead of raw Object arrays.
 */
interface ClickCountProjection {
    val shortCode: String
    val totalClicks: Long
}
