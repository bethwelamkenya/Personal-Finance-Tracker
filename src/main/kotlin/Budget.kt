package org.example

// Track spending against budget categories
data class Budget(
    val category: String,
    val limit: Double,
    val spent: Double = 0.0
)
