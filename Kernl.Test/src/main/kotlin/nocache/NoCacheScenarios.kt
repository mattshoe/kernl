package io.github.mattshoe.shoebox.nocache

import io.github.mattshoe.shoebox.annotations.Kernl
import io.github.mattshoe.shoebox.models.ServiceRequest
import io.github.mattshoe.shoebox.models.ServiceResponse

interface SingleMemoryCacheScenarios {

    // Single primitive parameter, primitive return
    @Kernl.NoCache("NoCacheSingleParamPrimitiveReturn")
    suspend fun singleParamPrimitiveReturn(id: String): Int

    // Multiple same primitive parameters, primitive return
    @Kernl.NoCache("NoCacheMultiSameParamPrimitiveReturn")
    suspend fun multiSameParamPrimitiveReturn(id: String, bar: String): Int

    // Multiple different primitive parameters, primitive return
    @Kernl.NoCache("NoCacheMultiParamPrimitiveReturn")
    suspend fun multiParamPrimitiveReturn(id: String, bar: Int): Int

    // Single primitive parameter, complex return
    @Kernl.NoCache("NoCacheSingleParamComplexReturn")
    suspend fun singleParamComplexReturn(id: String): ServiceResponse

    // Multiple different primitive parameters, complex return
    @Kernl.NoCache("NoCacheMultiParamComplexReturn")
    suspend fun multiParamComplexReturn(id: String, bar: Int): ServiceResponse

    // Multiple same complex parameters, complex return
    @Kernl.NoCache("NoCacheMultiSameParamComplexReturn")
    suspend fun multiSameParamComplexReturn(id: String, bar: ServiceResponse): ServiceResponse

    // Multiple different complex parameters, complex return
    @Kernl.NoCache("NoCacheMultiComplexParamComplexReturn")
    suspend fun multiComplexParamComplexReturn(id: ServiceRequest, bar: ServiceResponse): ServiceResponse

    // Single nullable primitive parameter, primitive return
    @Kernl.NoCache("NoCacheSingleNullableParamPrimitiveReturn")
    suspend fun singleNullableParamPrimitiveReturn(id: String?): Int

    // Multiple nullable same primitive parameters, primitive return
    @Kernl.NoCache("NoCacheMultiNullableSameParamPrimitiveReturn")
    suspend fun multiNullableSameParamPrimitiveReturn(id: String?, bar: String?): Int

    // Multiple nullable different primitive parameters, primitive return
    @Kernl.NoCache("NoCacheMultiNullableParamPrimitiveReturn")
    suspend fun multiNullableParamPrimitiveReturn(id: String?, bar: Int?): Int

    // Single nullable primitive parameter, complex return
    @Kernl.NoCache("NoCacheSingleNullableParamComplexReturn")
    suspend fun singleNullableParamComplexReturn(id: String?): ServiceResponse

    // Multiple nullable same complex parameters, complex return
    @Kernl.NoCache("NoCacheMultiNullableSameParamComplexReturn")
    suspend fun multiNullableSameParamComplexReturn(id: String?, bar: ServiceResponse?): ServiceResponse

    // Multiple nullable different complex parameters, complex return
    @Kernl.NoCache("NoCacheMultiNullableComplexParamComplexReturn")
    suspend fun multiNullableComplexParamComplexReturn(id: ServiceRequest?, bar: ServiceResponse?): ServiceResponse

    // Single complex parameter, primitive return
    @Kernl.NoCache("NoCacheSingleComplexParamPrimitiveReturn")
    suspend fun singleComplexParamPrimitiveReturn(id: ServiceRequest): Int

    // Single complex parameter, complex return
    @Kernl.NoCache("NoCacheSingleComplexParamComplexReturn")
    suspend fun singleComplexParamComplexReturn(id: ServiceRequest): ServiceResponse

    // Multiple different complex parameters, primitive return
    @Kernl.NoCache("NoCacheMultiComplexParamPrimitiveReturn")
    suspend fun multiComplexParamPrimitiveReturn(id: ServiceRequest, bar: ServiceResponse): Int

    // Single nullable complex parameter, primitive return
    @Kernl.NoCache("NoCacheSingleNullableComplexParamPrimitiveReturn")
    suspend fun singleNullableComplexParamPrimitiveReturn(id: ServiceRequest?): Int

    // Multiple nullable different complex parameters, primitive return
    @Kernl.NoCache("NoCacheMultiNullableComplexParamPrimitiveReturn")
    suspend fun multiNullableComplexParamPrimitiveReturn(id: ServiceRequest?, bar: ServiceResponse?): Int

    // Multiple mixed parameters (primitive and complex), primitive return
    @Kernl.NoCache("NoCacheMultiMixedParamPrimitiveReturn")
    suspend fun multiMixedParamPrimitiveReturn(id: String, bar: ServiceRequest): Int

    // Multiple mixed parameters (primitive and complex), complex return
    @Kernl.NoCache("NoCacheMultiMixedParamComplexReturn")
    suspend fun multiMixedParamComplexReturn(id: String, bar: ServiceRequest): ServiceResponse

    // Multiple mixed nullable parameters (primitive and complex), primitive return
    @Kernl.NoCache("NoCacheMultiNullableMixedParamPrimitiveReturn")
    suspend fun multiNullableMixedParamPrimitiveReturn(id: String?, bar: ServiceRequest?): Int

    // Multiple mixed nullable parameters (primitive and complex), complex return
    @Kernl.NoCache("NoCacheMultiNullableMixedParamComplexReturn")
    suspend fun multiNullableMixedParamComplexReturn(id: String?, bar: ServiceRequest?): ServiceResponse
}