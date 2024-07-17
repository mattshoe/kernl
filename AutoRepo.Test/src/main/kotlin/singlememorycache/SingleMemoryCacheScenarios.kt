package io.github.mattshoe.shoebox.singlememorycache

import io.github.mattshoe.shoebox.annotations.AutoRepo
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse

interface SingleMemoryCacheScenarios {

    // Single primitive parameter, primitive return
    @AutoRepo.SingleMemoryCache("SingleParamPrimitiveReturn")
    suspend fun singleParamPrimitiveReturn(id: String): Int

    // Multiple same primitive parameters, primitive return
    @AutoRepo.SingleMemoryCache("MultiSameParamPrimitiveReturn")
    suspend fun multiSameParamPrimitiveReturn(id: String, bar: String): Int

    // Multiple different primitive parameters, primitive return
    @AutoRepo.SingleMemoryCache("MultiParamPrimitiveReturn")
    suspend fun multiParamPrimitiveReturn(id: String, bar: Int): Int

    // Single primitive parameter, complex return
    @AutoRepo.SingleMemoryCache("SingleParamComplexReturn")
    suspend fun singleParamComplexReturn(id: String): ServiceResponse

    // Multiple different primitive parameters, complex return
    @AutoRepo.SingleMemoryCache("MultiParamComplexReturn")
    suspend fun multiParamComplexReturn(id: String, bar: Int): ServiceResponse

    // Multiple same complex parameters, complex return
    @AutoRepo.SingleMemoryCache("MultiSameParamComplexReturn")
    suspend fun multiSameParamComplexReturn(id: String, bar: ServiceResponse): ServiceResponse

    // Multiple different complex parameters, complex return
    @AutoRepo.SingleMemoryCache("MultiComplexParamComplexReturn")
    suspend fun multiComplexParamComplexReturn(id: ServiceRequest, bar: ServiceResponse): ServiceResponse

    // Single nullable primitive parameter, primitive return
    @AutoRepo.SingleMemoryCache("SingleNullableParamPrimitiveReturn")
    suspend fun singleNullableParamPrimitiveReturn(id: String?): Int

    // Multiple nullable same primitive parameters, primitive return
    @AutoRepo.SingleMemoryCache("MultiNullableSameParamPrimitiveReturn")
    suspend fun multiNullableSameParamPrimitiveReturn(id: String?, bar: String?): Int

    // Multiple nullable different primitive parameters, primitive return
    @AutoRepo.SingleMemoryCache("MultiNullableParamPrimitiveReturn")
    suspend fun multiNullableParamPrimitiveReturn(id: String?, bar: Int?): Int

    // Single nullable primitive parameter, complex return
    @AutoRepo.SingleMemoryCache("SingleNullableParamComplexReturn")
    suspend fun singleNullableParamComplexReturn(id: String?): ServiceResponse

    // Multiple nullable same complex parameters, complex return
    @AutoRepo.SingleMemoryCache("MultiNullableSameParamComplexReturn")
    suspend fun multiNullableSameParamComplexReturn(id: String?, bar: ServiceResponse?): ServiceResponse

    // Multiple nullable different complex parameters, complex return
    @AutoRepo.SingleMemoryCache("MultiNullableComplexParamComplexReturn")
    suspend fun multiNullableComplexParamComplexReturn(id: ServiceRequest?, bar: ServiceResponse?): ServiceResponse

    // Single complex parameter, primitive return
    @AutoRepo.SingleMemoryCache("SingleComplexParamPrimitiveReturn")
    suspend fun singleComplexParamPrimitiveReturn(id: ServiceRequest): Int

    // Multiple different complex parameters, primitive return
    @AutoRepo.SingleMemoryCache("MultiComplexParamPrimitiveReturn")
    suspend fun multiComplexParamPrimitiveReturn(id: ServiceRequest, bar: ServiceResponse): Int

    // Single nullable complex parameter, primitive return
    @AutoRepo.SingleMemoryCache("SingleNullableComplexParamPrimitiveReturn")
    suspend fun singleNullableComplexParamPrimitiveReturn(id: ServiceRequest?): Int

    // Multiple nullable different complex parameters, primitive return
    @AutoRepo.SingleMemoryCache("MultiNullableComplexParamPrimitiveReturn")
    suspend fun multiNullableComplexParamPrimitiveReturn(id: ServiceRequest?, bar: ServiceResponse?): Int

    // Multiple mixed parameters (primitive and complex), primitive return
    @AutoRepo.SingleMemoryCache("MultiMixedParamPrimitiveReturn")
    suspend fun multiMixedParamPrimitiveReturn(id: String, bar: ServiceRequest): Int

    // Multiple mixed parameters (primitive and complex), complex return
    @AutoRepo.SingleMemoryCache("MultiMixedParamComplexReturn")
    suspend fun multiMixedParamComplexReturn(id: String, bar: ServiceRequest): ServiceResponse

    // Multiple mixed nullable parameters (primitive and complex), primitive return
    @AutoRepo.SingleMemoryCache("MultiNullableMixedParamPrimitiveReturn")
    suspend fun multiNullableMixedParamPrimitiveReturn(id: String?, bar: ServiceRequest?): Int

    // Multiple mixed nullable parameters (primitive and complex), complex return
    @AutoRepo.SingleMemoryCache("MultiNullableMixedParamComplexReturn")
    suspend fun multiNullableMixedParamComplexReturn(id: String?, bar: ServiceRequest?): ServiceResponse
}