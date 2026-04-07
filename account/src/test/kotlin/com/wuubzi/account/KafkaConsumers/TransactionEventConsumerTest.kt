package com.wuubzi.account.KafkaConsumers

import com.wuubzi.account.application.DTOS.Events.EventType
import com.wuubzi.account.application.DTOS.Events.TransactionEvent
import com.wuubzi.account.application.DTOS.Events.TransactionStatus
import com.wuubzi.account.application.DTOS.Events.TransactionType
import com.wuubzi.account.application.Ports.out.CachePort
import com.wuubzi.account.infrastructure.KafkaConsumers.TransactionEventConsumer
import com.wuubzi.account.infrastructure.Persistence.Entities.AccountEntity
import com.wuubzi.account.infrastructure.Repositories.AccountRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.kafka.core.KafkaTemplate
import java.sql.Timestamp
import java.time.Instant
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class TransactionEventConsumerTest {

    @Mock
    lateinit var accountRepository: AccountRepository

    @Mock
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Mock
    lateinit var cachePort: CachePort

    private lateinit var consumer: TransactionEventConsumer

    private val transactionId = UUID.randomUUID()
    private val fromAccountId = UUID.randomUUID()
    private val toAccountId = UUID.randomUUID()
    private val userId = UUID.randomUUID()

    @BeforeEach
    fun setup() {
        consumer = TransactionEventConsumer(accountRepository, kafkaTemplate, cachePort)
    }

    private fun createAccountEntity(accountId: UUID, balance: Double): AccountEntity {
        return AccountEntity().apply {
            idAccount = accountId
            accountNumber = "12345678901234567890"
            userId = this@TransactionEventConsumerTest.userId
            accountType = "SAVINGS"
            currency = "USD"
            this.balance = balance
            availableBalance = balance
            status = "ACTIVE"
            alias = "Test"
            overdraftLimit = 0.0
            createdAt = Timestamp.from(Instant.now())
        }
    }

    @Test
    fun shouldProcessDepositSuccessfully() {
        // GIVEN
        val event = TransactionEvent(
            transactionId = transactionId,
            eventType = EventType.TRANSACTION_CREATED,
            type = TransactionType.DEPOSIT,
            amount = 500.0,
            toAccountId = toAccountId,
            currency = "USD",
            status = TransactionStatus.PENDING,
            description = "Test deposit",
            email = "test@email.com"
        )
        val account = createAccountEntity(toAccountId, 1000.0)
        whenever(accountRepository.findById(toAccountId)).thenReturn(Optional.of(account))
        whenever(accountRepository.save(any<AccountEntity>())).thenReturn(account)

        // WHEN
        consumer.listen(event)

        // THEN
        assertEquals(1500.0, account.balance)
        assertEquals(1500.0, account.availableBalance)
        verify(accountRepository).save(account)
        verify(kafkaTemplate).send(eq("transactions.events"), eq(transactionId.toString()), any())
    }

    @Test
    fun shouldProcessWithdrawSuccessfully() {
        // GIVEN
        val event = TransactionEvent(
            transactionId = transactionId,
            eventType = EventType.TRANSACTION_CREATED,
            type = TransactionType.WITHDRAW,
            amount = 200.0,
            fromAccountId = fromAccountId,
            currency = "USD",
            status = TransactionStatus.PENDING,
            description = "Test withdraw",
            email = "test@email.com"
        )
        val account = createAccountEntity(fromAccountId, 1000.0)
        whenever(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(account))
        whenever(accountRepository.save(any<AccountEntity>())).thenReturn(account)

        // WHEN
        consumer.listen(event)

        // THEN
        assertEquals(800.0, account.balance)
        assertEquals(800.0, account.availableBalance)
        verify(accountRepository).save(account)
    }

    @Test
    fun shouldProcessTransferSuccessfully() {
        // GIVEN
        val event = TransactionEvent(
            transactionId = transactionId,
            eventType = EventType.TRANSACTION_CREATED,
            type = TransactionType.TRANSFER,
            amount = 300.0,
            fromAccountId = fromAccountId,
            toAccountId = toAccountId,
            currency = "USD",
            status = TransactionStatus.PENDING,
            description = "Test transfer",
            email = "test@email.com"
        )
        val fromAccount = createAccountEntity(fromAccountId, 1000.0)
        val toAccount = createAccountEntity(toAccountId, 500.0)
        whenever(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount))
        whenever(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount))
        whenever(accountRepository.save(any<AccountEntity>())).thenReturn(fromAccount)

        // WHEN
        consumer.listen(event)

        // THEN
        assertEquals(700.0, fromAccount.balance)
        assertEquals(800.0, toAccount.balance)
    }

    @Test
    fun shouldIgnoreNonCreatedEvents() {
        // GIVEN
        val event = TransactionEvent(
            transactionId = transactionId,
            eventType = EventType.TRANSACTION_COMPLETED,
            type = TransactionType.DEPOSIT,
            amount = 100.0,
            toAccountId = toAccountId,
            currency = "USD",
            status = TransactionStatus.COMPLETED,
            description = "Completed event"
        )

        // WHEN
        consumer.listen(event)

        // THEN
        verify(accountRepository, never()).findById(any())
        verify(accountRepository, never()).save(any())
    }

    @Test
    fun shouldPublishFailedEventWhenDepositFails() {
        // GIVEN
        val event = TransactionEvent(
            transactionId = transactionId,
            eventType = EventType.TRANSACTION_CREATED,
            type = TransactionType.DEPOSIT,
            amount = 500.0,
            toAccountId = toAccountId,
            currency = "USD",
            status = TransactionStatus.PENDING,
            description = "Test deposit"
        )
        whenever(accountRepository.findById(toAccountId)).thenReturn(Optional.empty())

        // WHEN
        consumer.listen(event)

        // THEN
        verify(kafkaTemplate).send(eq("transactions.events"), eq(transactionId.toString()), any())
    }

    @Test
    fun shouldPublishFailedEventWhenWithdrawInsufficientBalance() {
        // GIVEN
        val event = TransactionEvent(
            transactionId = transactionId,
            eventType = EventType.TRANSACTION_CREATED,
            type = TransactionType.WITHDRAW,
            amount = 5000.0,
            fromAccountId = fromAccountId,
            currency = "USD",
            status = TransactionStatus.PENDING,
            description = "Test withdraw"
        )
        val account = createAccountEntity(fromAccountId, 100.0)
        whenever(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(account))

        // WHEN
        consumer.listen(event)

        // THEN
        verify(kafkaTemplate).send(eq("transactions.events"), eq(transactionId.toString()), any())
    }

    @Test
    fun shouldPublishFailedEventWhenDepositMissingToAccountId() {
        // GIVEN
        val event = TransactionEvent(
            transactionId = transactionId,
            eventType = EventType.TRANSACTION_CREATED,
            type = TransactionType.DEPOSIT,
            amount = 500.0,
            toAccountId = null,
            currency = "USD",
            status = TransactionStatus.PENDING,
            description = "Test deposit"
        )

        // WHEN
        consumer.listen(event)

        // THEN
        verify(kafkaTemplate).send(eq("transactions.events"), eq(transactionId.toString()), any())
    }

    @Test
    fun shouldPublishFailedEventWhenTransferMissingFromAccountId() {
        // GIVEN
        val event = TransactionEvent(
            transactionId = transactionId,
            eventType = EventType.TRANSACTION_CREATED,
            type = TransactionType.TRANSFER,
            amount = 300.0,
            fromAccountId = null,
            toAccountId = toAccountId,
            currency = "USD",
            status = TransactionStatus.PENDING
        )

        // WHEN
        consumer.listen(event)

        // THEN
        verify(kafkaTemplate).send(eq("transactions.events"), eq(transactionId.toString()), any())
    }

    @Test
    fun shouldPublishFailedEventWhenTransferMissingToAccountId() {
        // GIVEN
        val event = TransactionEvent(
            transactionId = transactionId,
            eventType = EventType.TRANSACTION_CREATED,
            type = TransactionType.TRANSFER,
            amount = 300.0,
            fromAccountId = fromAccountId,
            toAccountId = null,
            currency = "USD",
            status = TransactionStatus.PENDING
        )

        // WHEN
        consumer.listen(event)

        // THEN
        verify(kafkaTemplate).send(eq("transactions.events"), eq(transactionId.toString()), any())
    }

    @Test
    fun shouldIgnoreTransactionFailedEvent() {
        // GIVEN
        val event = TransactionEvent(
            transactionId = transactionId,
            eventType = EventType.TRANSACTION_FAILED,
            type = TransactionType.DEPOSIT,
            amount = 100.0,
            toAccountId = toAccountId,
            currency = "USD",
            status = TransactionStatus.FAILED,
            description = "Failed event"
        )

        // WHEN
        consumer.listen(event)

        // THEN
        verify(accountRepository, never()).findById(any())
    }
}

