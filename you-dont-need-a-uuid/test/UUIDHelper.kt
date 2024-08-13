package com.example.benchmark.uutils

import com.github.f4b6a3.uuid.UuidCreator
import com.github.f4b6a3.uuid.alt.GUID
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

object UUIDHelper {
    private val atomicLong = AtomicLong(2000)

    fun localId() = LocalId.newId()

    // More efficient for primitives than toString() due to StringBuilder usage
    fun atomicLong() = "" + atomicLong.getAndIncrement()

    fun uuid4() = UUID.randomUUID().toString()

    fun uuid6() = UuidCreator.getTimeOrdered().toString()

    fun uuid7() = UuidCreator.getTimeOrderedEpoch().toString()

    fun guid4() = GUID.v4().toString()

    fun guid7() = GUID.v7().toString()
}
