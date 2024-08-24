/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    fun localId_10() {
        testInList(10) { UUIDHelper.localId() }
    }

    @Test
    fun localId_100() {
        testInList(100) { UUIDHelper.localId() }
    }

    @Test
    fun localId_1000() {
        testInList(1_000) { UUIDHelper.localId() }
    }

    @Test
    fun localId_10000() {
        testInList(10_000) { UUIDHelper.localId() }
    }

    @Test
    fun localId_100000() {
        testInList(100_000) { UUIDHelper.localId() }
    }

    @Test
    fun localId_1000000() {
        testInList(1_000_000) { UUIDHelper.localId() }
    }

    @Test
    fun uuid4_10() {
        testInList(10) { UUIDHelper.uuid4() }
    }

    @Test
    fun uuid4_100() {
        testInList(100) { UUIDHelper.uuid4() }
    }

    @Test
    fun uuid4_1000() {
        testInList(1_000) { UUIDHelper.uuid4() }
    }

    @Test
    fun uuid4_10000() {
        testInList(10_000) { UUIDHelper.uuid4() }
    }

    @Test
    fun uuid4_100000() {
        testInList(100_000) { UUIDHelper.uuid4() }
    }

    @Test
    fun uuid4_1000000() {
        testInList(1_000_000) { UUIDHelper.uuid4() }
    }

    private inline fun testInList(size: Int, crossinline idBuilder: () -> String) {
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
        val id: String,
        val name: String,
        val role: String,
        val price: Double,
    )
}
