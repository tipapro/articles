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
class IdBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun atomicLong() {
        benchmarkRule.measureRepeated {
            UUIDHelper.atomicLong()
        }
    }

    @Test
    fun localId() {
        benchmarkRule.measureRepeated {
            UUIDHelper.localId()
        }
    }

    @Test
    fun uuid4() {
        benchmarkRule.measureRepeated {
            UUIDHelper.uuid4()
        }
    }

    @Test
    fun uuid6() {
        benchmarkRule.measureRepeated {
            UUIDHelper.uuid6()
        }
    }

    @Test
    fun uuid7() {
        benchmarkRule.measureRepeated {
            UUIDHelper.uuid7()
        }
    }

    @Test
    fun guid4() {
        benchmarkRule.measureRepeated {
            UUIDHelper.guid4()
        }
    }

    @Test
    fun guid7() {
        benchmarkRule.measureRepeated {
            UUIDHelper.guid7()
        }
    }
}
