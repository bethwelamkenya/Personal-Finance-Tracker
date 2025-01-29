package org.example

data class SavingsGoal(
    val id: Int,
    val accountNumber: String,
    val goalName: String,
    val targetAmount: Double,
    val savedAmount: Double
)

