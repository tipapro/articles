package com.example.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.benchmark.uutils.UUIDHelper
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class IdBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun atomicLongString() = benchmarkRule.measureRepeated { UUIDHelper.atomicLongString() }
    @Test
    fun localIdString() = benchmarkRule.measureRepeated { UUIDHelper.localIdString() }
    @Test
    fun uuid4String() = benchmarkRule.measureRepeated { UUIDHelper.uuid4String() }
    @Test
    fun uuid6String() = benchmarkRule.measureRepeated { UUIDHelper.uuid6String() }
    @Test
    fun uuid7String() = benchmarkRule.measureRepeated { UUIDHelper.uuid7String() }
    @Test
    fun guid4String() = benchmarkRule.measureRepeated { UUIDHelper.guid4String() }
    @Test
    fun guid7String() = benchmarkRule.measureRepeated { UUIDHelper.guid7String() }


    @Test
    fun atomicLong() = benchmarkRule.measureRepeated { UUIDHelper.atomicLong() }
    @Test
    fun localId() = benchmarkRule.measureRepeated { UUIDHelper.localId() }
    @Test
    fun uuid4() = benchmarkRule.measureRepeated { UUIDHelper.uuid4() }
    @Test
    fun uuid6() = benchmarkRule.measureRepeated { UUIDHelper.uuid6() }
    @Test
    fun uuid7() = benchmarkRule.measureRepeated { UUIDHelper.uuid7() }
    @Test
    fun guid4() = benchmarkRule.measureRepeated { UUIDHelper.guid4() }
    @Test
    fun guid7() = benchmarkRule.measureRepeated { UUIDHelper.guid7() }
}
