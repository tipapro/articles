package com.example.benchmark.uutils

import com.github.f4b6a3.uuid.UuidCreator
import com.github.f4b6a3.uuid.alt.GUID
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

object UUIDHelper {
    private val atomicLong = AtomicLong(2000)

    fun localIdString() = LocalId.newIdString()

    // More efficient for primitives than toString() due to StringBuilder usage
    fun atomicLongString() = "" + atomicLong.getAndIncrement()
    fun uuid4String() = UUID.randomUUID().toString()
    fun uuid6String() = UuidCreator.getTimeOrdered().toString()
    fun uuid7String() = UuidCreator.getTimeOrderedEpoch().toString()
    fun guid4String() = GUID.v4().toString()
    fun guid7String() = GUID.v7().toString()


    fun localId(): Any = LocalId.newId()
    fun atomicLong() = atomicLong.getAndIncrement()
    fun uuid4(): Any = UUID.randomUUID()
    fun uuid6(): Any = UuidCreator.getTimeOrdered()
    fun uuid7(): Any = UuidCreator.getTimeOrderedEpoch()
    fun guid4(): Any = GUID.v4()
    fun guid7(): Any = GUID.v7()
}
