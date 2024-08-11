package org.mattshoe.shoebox.kernl.singlememorycache

import org.mattshoe.shoebox.kernl.annotations.Kernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse

interface SingleMemoryCacheIntegrationTester {
    @Kernl.SingleCache.InMemory("SingleMemoryCacheIntegrationTester")
    suspend fun dummyMethod(foo: Int, bar: String): String
}