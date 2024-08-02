package org.mattshoe.shoebox.kernl.singlememorycache

import org.mattshoe.shoebox.kernl.annotations.Kernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse

interface SingleMemoryCacheScenarios {

    // Single primitive parameter, primitive return
    @Kernl.SingleMemoryCache("SingleParamPrimitiveReturn")
    suspend fun singleParamPrimitiveReturn(id: String): Int

    // Multiple same primitive parameters, primitive return
    @Kernl.SingleMemoryCache("MultiSameParamPrimitiveReturn")
    suspend fun multiSameParamPrimitiveReturn(id: String, bar: String): Int

    // Multiple different primitive parameters, primitive return
    @Kernl.SingleMemoryCache("MultiParamPrimitiveReturn")
    suspend fun multiParamPrimitiveReturn(id: String, bar: Int): Int

    // Single primitive parameter, complex return
    @Kernl.SingleMemoryCache("SingleParamComplexReturn")
    suspend fun singleParamComplexReturn(id: String): ServiceResponse

    // Multiple different primitive parameters, complex return
    @Kernl.SingleMemoryCache("MultiParamComplexReturn")
    suspend fun multiParamComplexReturn(id: String, bar: Int): ServiceResponse

    // Multiple same complex parameters, complex return
    @Kernl.SingleMemoryCache("MultiSameParamComplexReturn")
    suspend fun multiSameParamComplexReturn(id: String, bar: ServiceResponse): ServiceResponse

    // Multiple different complex parameters, complex return
    @Kernl.SingleMemoryCache("MultiComplexParamComplexReturn")
    suspend fun multiComplexParamComplexReturn(id: ServiceRequest, bar: ServiceResponse): ServiceResponse

    // Single nullable primitive parameter, primitive return
    @Kernl.SingleMemoryCache("SingleNullableParamPrimitiveReturn")
    suspend fun singleNullableParamPrimitiveReturn(id: String?): Int

    // Multiple nullable same primitive parameters, primitive return
    @Kernl.SingleMemoryCache("MultiNullableSameParamPrimitiveReturn")
    suspend fun multiNullableSameParamPrimitiveReturn(id: String?, bar: String?): Int

    // Multiple nullable different primitive parameters, primitive return
    @Kernl.SingleMemoryCache("MultiNullableParamPrimitiveReturn")
    suspend fun multiNullableParamPrimitiveReturn(id: String?, bar: Int?): Int

    // Single nullable primitive parameter, complex return
    @Kernl.SingleMemoryCache("SingleNullableParamComplexReturn")
    suspend fun singleNullableParamComplexReturn(id: String?): ServiceResponse

    // Multiple nullable same complex parameters, complex return
    @Kernl.SingleMemoryCache("MultiNullableSameParamComplexReturn")
    suspend fun multiNullableSameParamComplexReturn(id: String?, bar: ServiceResponse?): ServiceResponse

    // Multiple nullable different complex parameters, complex return
    @Kernl.SingleMemoryCache("MultiNullableComplexParamComplexReturn")
    suspend fun multiNullableComplexParamComplexReturn(id: ServiceRequest?, bar: ServiceResponse?): ServiceResponse

    // Single complex parameter, primitive return
    @Kernl.SingleMemoryCache("SingleComplexParamPrimitiveReturn")
    suspend fun singleComplexParamPrimitiveReturn(id: ServiceRequest): Int

    // Single complex parameter, complex return
    @Kernl.SingleMemoryCache("SingleComplexParamComplexReturn")
    suspend fun singleComplexParamComplexReturn(id: ServiceRequest): ServiceResponse

    // Multiple different complex parameters, primitive return
    @Kernl.SingleMemoryCache("MultiComplexParamPrimitiveReturn")
    suspend fun multiComplexParamPrimitiveReturn(id: ServiceRequest, bar: ServiceResponse): Int

    // Single nullable complex parameter, primitive return
    @Kernl.SingleMemoryCache("SingleNullableComplexParamPrimitiveReturn")
    suspend fun singleNullableComplexParamPrimitiveReturn(id: ServiceRequest?): Int

    // Multiple nullable different complex parameters, primitive return
    @Kernl.SingleMemoryCache("MultiNullableComplexParamPrimitiveReturn")
    suspend fun multiNullableComplexParamPrimitiveReturn(id: ServiceRequest?, bar: ServiceResponse?): Int

    // Multiple mixed parameters (primitive and complex), primitive return
    @Kernl.SingleMemoryCache("MultiMixedParamPrimitiveReturn")
    suspend fun multiMixedParamPrimitiveReturn(id: String, bar: ServiceRequest): Int

    // Multiple mixed parameters (primitive and complex), complex return
    @Kernl.SingleMemoryCache("MultiMixedParamComplexReturn")
    suspend fun multiMixedParamComplexReturn(id: String, bar: ServiceRequest): ServiceResponse

    // Multiple mixed nullable parameters (primitive and complex), primitive return
    @Kernl.SingleMemoryCache("MultiNullableMixedParamPrimitiveReturn")
    suspend fun multiNullableMixedParamPrimitiveReturn(id: String?, bar: ServiceRequest?): Int

    // Multiple mixed nullable parameters (primitive and complex), complex return
    @Kernl.SingleMemoryCache("MultiNullableMixedParamComplexReturn")
    suspend fun multiNullableMixedParamComplexReturn(id: String?, bar: ServiceRequest?): ServiceResponse
}