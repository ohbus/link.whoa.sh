package com.subhrodip.oss.whoa.link.dto

/**
 * Interface-based projection for ultra-high performance click counting.
 * By using the ID directly, we eliminate the need for SQL JOINs.
 */
interface ClickCountByIdProjection {
    val urlId: Long
    val totalClicks: Long
}
