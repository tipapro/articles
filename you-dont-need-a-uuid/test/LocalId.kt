package com.example.benchmark.uutils

import java.util.Random
import java.util.concurrent.atomic.AtomicLong

private const val BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
private const val BASE62_LENGTH = BASE62_CHARS.length

// Seed for Random number generation to ensure reproducibility
private const val RANDOM_SEED = 1234L

// Maximum value for generating a random prefix.
// This sets an upper limit on the random value used for prefix creation,
// ensuring the prefix is within a specific range for consistency.
private const val MAX_RANDOM_PREFIX_VALUE = 1000000

// Mask for extracting the lower 40 bits of the current time in milliseconds
// This limits the range of time-related values to 2^40 - 1 (about 34.8 years worth of milliseconds),
// ensuring the time component of the ID fits within 40 bits.
private const val CURRENT_TIME_MASK = 0xFFFFFFFFFL

// Initial counter value for ID generation.
// This is the starting point for the sequential counter used in generating unique IDs,
// allowing for a predefined starting number.
private const val INITIAL_COUNTER_VALUE = 2000L

fun Long.toBase62(): String {
    if (this == 0L) return "0"
    var number = this
    val result = StringBuilder()
    while (number > 0) {
        result.insert(0, BASE62_CHARS[(number % BASE62_LENGTH).toInt()])
        number /= BASE62_LENGTH
    }
    return result.toString()
}

fun Int.toBase62(): String {
    if (this == 0) return "0"
    var number = this
    val result = StringBuilder()
    while (number > 0) {
        result.insert(0, BASE62_CHARS[(number % BASE62_LENGTH).toInt()])
        number /= BASE62_LENGTH
    }
    return result.toString()
}

object LocalId {
    private val prefix = Random(RANDOM_SEED).nextInt(MAX_RANDOM_PREFIX_VALUE).toBase62() +
            System.currentTimeMillis().and(CURRENT_TIME_MASK).toBase62()
    private val counter = AtomicLong(INITIAL_COUNTER_VALUE)

    fun newId(): String {
        return prefix + counter.getAndIncrement()
    }
}
