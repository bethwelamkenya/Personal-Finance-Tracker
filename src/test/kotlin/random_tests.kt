import org.example.BankingTracker
import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertEquals

class RandomTests {
//    @Test
//    fun testCreateAccount() {
//        val tracker = BankingTracker()
//        tracker.createAccount("123", "John", "Bank", "1234")
//        assertEquals(1, tracker.getAccounts().size)
//    }

    fun isStrongPin(pin: String): Boolean {
        return pin.length >= 4 && pin.any { it.isDigit() }
    }

    @Test
    fun testAddBankAccount() {
        print("Enter account number: ")
        val accountNumber = readlnOrNull()
        print("Enter account holder: ")
        val accountHolder = readlnOrNull()
        print("Enter bank name: ")
        val bankName = readlnOrNull()
        var run = true
        while (run){
            print("Enter PIN: ")
            val pin = readlnOrNull()
            if (accountNumber != null && accountHolder != null && bankName != null && pin != null) {
                if (isStrongPin(pin)){
                    println("Bank account added successfully.")
                    run = false
                } else {
                    println("Invalid PIN. Must be at least 4 digits.")
                }
            } else {
                println("Invalid input. Bank account not added.")
                run = false
            }
        }
    }
}