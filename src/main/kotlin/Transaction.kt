package org.example

import java.time.LocalDate

data class Transaction(
    val account: String,
    val date: LocalDate,
    val type: TransactionType,
    val description: String? = "",
    val amount: Double
)
