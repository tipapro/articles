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
class ListIdBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun localId_10() = testInList(10) { UUIDHelper.localId() }
    @Test
    fun localId_100() = testInList(100) { UUIDHelper.localId() }
    @Test
    fun localId_1000() = testInList(1_000) { UUIDHelper.localId() }
    @Test
    fun localId_10000() = testInList(10_000) { UUIDHelper.localId() }
    @Test
    fun localId_100000() = testInList(100_000) { UUIDHelper.localId() }
    @Test
    fun localId_1000000() = testInList(1_000_000) { UUIDHelper.localId() }
    @Test
    fun uuid4_10() = testInList(10) { UUIDHelper.uuid4() }
    @Test
    fun uuid4_100() = testInList(100) { UUIDHelper.uuid4() }
    @Test
    fun uuid4_1000() = testInList(1_000) { UUIDHelper.uuid4() }
    @Test
    fun uuid4_10000() = testInList(10_000) { UUIDHelper.uuid4() }
    @Test
    fun uuid4_100000() = testInList(100_000) { UUIDHelper.uuid4() }
    @Test
    fun uuid4_1000000() = testInList(1_000_000) { UUIDHelper.uuid4() }


    @Test
    fun localIdString_10() = testInList(10) { UUIDHelper.localIdString() }
    @Test
    fun localIdString_100() = testInList(100) { UUIDHelper.localIdString() }
    @Test
    fun localIdString_1000() = testInList(1_000) { UUIDHelper.localIdString() }
    @Test
    fun localIdString_10000() = testInList(10_000) { UUIDHelper.localIdString() }
    @Test
    fun localIdString_100000() = testInList(100_000) { UUIDHelper.localIdString() }
    @Test
    fun localIdString_1000000() = testInList(1_000_000) { UUIDHelper.localIdString() }
    @Test
    fun uuid4String_10() = testInList(10) { UUIDHelper.uuid4String() }
    @Test
    fun uuid4String_100() = testInList(100) { UUIDHelper.uuid4String() }
    @Test
    fun uuid4String_1000() = testInList(1_000) { UUIDHelper.uuid4String() }
    @Test
    fun uuid4String_10000() = testInList(10_000) { UUIDHelper.uuid4String() }
    @Test
    fun uuid4String_100000() = testInList(100_000) { UUIDHelper.uuid4String() }
    @Test
    fun uuid4String_1000000() = testInList(1_000_000) { UUIDHelper.uuid4String() }



    private inline fun testInList(size: Int, crossinline idBuilder: () -> Any) {
        benchmarkRule.measureRepeated {
            List(size) {
                SomeEntity(
                    id = idBuilder(),
                    name = "My Name",
                    role = "Some Role",
                    price = 123.456,
                )
            }
        }
    }

    private class SomeEntity(
        val id: Any,
        val name: String,
        val role: String,
        val price: Double,
    )
}
