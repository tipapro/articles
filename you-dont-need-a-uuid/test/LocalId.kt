package com.example.benchmark.uutils

import java.security.SecureRandom
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

private const val CHARS = "BJmkQCdLHlPobfKZsiuRqDwtzINTWOYA"
private const val MASK = 0x1FL
private const val BASE32_MAX_LENGTH = 13

fun Long.toCompactString(): String {
    val result = CharArray(BASE32_MAX_LENGTH)
    var number = this
    var index = BASE32_MAX_LENGTH - 1

    do {
        result[index--] = CHARS[(number and MASK).toInt()]
        number = number ushr 5
    } while (number != 0L)

    return String(result, index + 1, BASE32_MAX_LENGTH - index - 1)
}

// Initial counter value for ID generation.
// This is the starting point for the sequential counter used in generating unique IDs,
// allowing for a predefined starting number. The starting point is 2000L to avoid
// using small integers that could potentially fall within the Integer cache pool in Java.
private const val INITIAL_COUNTER_VALUE = 2000L

class LocalId(
    private val mostSigBits: Long,
    private val leastSigBits: Long
) {

    override fun hashCode(): Int {
        return (mostSigBits xor leastSigBits).hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if ((null == other) || (other.javaClass != LocalId::class.java)) return false
        val id = other as LocalId
        return (mostSigBits == id.mostSigBits &&
                leastSigBits == id.leastSigBits)
    }

    override fun toString(): String {
        return mostSigBits.toCompactString() + "-" + leastSigBits.toCompactString()
    }

    fun toUUID(): UUID {
        return UUID(mostSigBits, leastSigBits)
    }

    companion object {
        private val counter = AtomicLong(INITIAL_COUNTER_VALUE)
        private val mostSigBits = generateMSB()
        private val prefix = mostSigBits.toCompactString() + "-"

        private fun generateMSB(): Long {
            val random24Bits = SecureRandom().nextLong() and 0xFFFFFFL
            val currentTimeMillis = System.currentTimeMillis() and 0xFFFFFFFFFFL
            return (currentTimeMillis shl 24) or random24Bits
        }

        fun newId() = LocalId(
            mostSigBits = mostSigBits,
            leastSigBits = counter.getAndIncrement(),
        )

        fun newIdString(): String {
            return prefix + counter.getAndIncrement().toCompactString()
        }
    }
}
