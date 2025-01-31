package org.example

import java.time.LocalDate

data class Transaction(
    val id: Int? = 0,
    val account: String,
    val date: LocalDate,
    val type: TransactionType,
    val description: String? = "",
    val amount: Double
) {
    constructor(account: String, date: LocalDate, type: TransactionType, description: String?, amount: Double) : this(
        id = null,
        account = account,
        date = date,
        type = type,
        description = description,
        amount = amount
    )
}
