import org.example.DBConnector
import org.example.EncryptionHelper
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*
import kotlin.test.Test

class RandomTests {
//    @Test
//    fun testCreateAccount() {
//        val tracker = BankingTracker()
//        tracker.createAccount("123", "John", "Bank", "1234")
//        assertEquals(1, tracker.getAccounts().size)
//    }

    private fun isStrongPin(pin: String): Boolean {
        return pin.length >= 4 && pin.any { it.isDigit() }
    }

    @Test
    fun testGenerateKey() {
        val encryptionHelper = EncryptionHelper()
        val key = encryptionHelper.generateAESKey()
        val stringKey = Base64.getEncoder().encodeToString(key.encoded)
        println("Generated Key: $stringKey")
        val key2 = encryptionHelper.getKeyFromString(stringKey)
        val stringKey2 = Base64.getEncoder().encodeToString(key2.encoded)
        assertEquals(stringKey, stringKey2)
    }

    @Test
    fun testEncryptDecrypt() {
        val dbConnector = DBConnector()
        val encryptionHelper = EncryptionHelper()
        val key = encryptionHelper.getKeyFromString(dbConnector.getSecretKey(1)!!)
        val data = "987654321"
        val encryptedData = encryptionHelper.encryptText(data, key)
        val decryptedData = encryptionHelper.decryptText(encryptedData, key)
        println(encryptedData)
        assertEquals(data, decryptedData)
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