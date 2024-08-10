package org.mattshoe.shoebox.kernl.nocache

import org.mattshoe.shoebox.kernl.annotations.Kernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse

interface SingleMemoryCacheScenarios {

    // Single primitive parameter, primitive return
    @Kernl.NoCache("NoCacheSingleParamPrimitiveReturn")
    suspend fun singleParamPrimitiveReturn(id: String): Int

    // Multiple same primitive parameters, primitive return
    @Kernl.NoCache("NoCacheMultiSameParamPrimitiveReturnKernl")
    suspend fun multiSameParamPrimitiveReturn(id: String, bar: String): Int

    // Multiple different primitive parameters, primitive return
    @Kernl.NoCache("NoCacheMultiParamPrimitiveReturnKernl")
    suspend fun multiParamPrimitiveReturn(id: String, bar: Int): Int

    // Single primitive parameter, complex return
    @Kernl.NoCache("NoCacheSingleParamComplexReturnKernl")
    suspend fun singleParamComplexReturn(id: String): ServiceResponse

    // Multiple different primitive parameters, complex return
    @Kernl.NoCache("NoCacheMultiParamComplexReturnKernl")
    suspend fun multiParamComplexReturn(id: String, bar: Int): ServiceResponse

    // Multiple same complex parameters, complex return
    @Kernl.NoCache("NoCacheMultiSameParamComplexReturnKernl")
    suspend fun multiSameParamComplexReturn(id: String, bar: ServiceResponse): ServiceResponse

    // Multiple different complex parameters, complex return
    @Kernl.NoCache("NoCacheMultiComplexParamComplexReturnKernl")
    suspend fun multiComplexParamComplexReturn(id: ServiceRequest, bar: ServiceResponse): ServiceResponse

    // Single nullable primitive parameter, primitive return
    @Kernl.NoCache("NoCacheSingleNullableParamPrimitiveReturnKernl")
    suspend fun singleNullableParamPrimitiveReturn(id: String?): Int

    // Multiple nullable same primitive parameters, primitive return
    @Kernl.NoCache("NoCacheMultiNullableSameParamPrimitiveReturnKernl")
    suspend fun multiNullableSameParamPrimitiveReturn(id: String?, bar: String?): Int

    // Multiple nullable different primitive parameters, primitive return
    @Kernl.NoCache("NoCacheMultiNullableParamPrimitiveReturnKernl")
    suspend fun multiNullableParamPrimitiveReturn(id: String?, bar: Int?): Int

    // Single nullable primitive parameter, complex return
    @Kernl.NoCache("NoCacheSingleNullableParamComplexReturnKernl")
    suspend fun singleNullableParamComplexReturn(id: String?): ServiceResponse

    // Multiple nullable same complex parameters, complex return
    @Kernl.NoCache("NoCacheMultiNullableSameParamComplexReturnKernl")
    suspend fun multiNullableSameParamComplexReturn(id: String?, bar: ServiceResponse?): ServiceResponse

    // Multiple nullable different complex parameters, complex return
    @Kernl.NoCache("NoCacheMultiNullableComplexParamComplexReturnKernl")
    suspend fun multiNullableComplexParamComplexReturn(id: ServiceRequest?, bar: ServiceResponse?): ServiceResponse

    // Single complex parameter, primitive return
    @Kernl.NoCache("NoCacheSingleComplexParamPrimitiveReturnKernl")
    suspend fun singleComplexParamPrimitiveReturn(id: ServiceRequest): Int

    // Single complex parameter, complex return
    @Kernl.NoCache("NoCacheSingleComplexParamComplexReturnKernl")
    suspend fun singleComplexParamComplexReturn(id: ServiceRequest): ServiceResponse

    // Multiple different complex parameters, primitive return
    @Kernl.NoCache("NoCacheMultiComplexParamPrimitiveReturnKernl")
    suspend fun multiComplexParamPrimitiveReturn(id: ServiceRequest, bar: ServiceResponse): Int

    // Single nullable complex parameter, primitive return
    @Kernl.NoCache("NoCacheSingleNullableComplexParamPrimitiveReturnKernl")
    suspend fun singleNullableComplexParamPrimitiveReturn(id: ServiceRequest?): Int

    // Multiple nullable different complex parameters, primitive return
    @Kernl.NoCache("NoCacheMultiNullableComplexParamPrimitiveReturnKernl")
    suspend fun multiNullableComplexParamPrimitiveReturn(id: ServiceRequest?, bar: ServiceResponse?): Int

    // Multiple mixed parameters (primitive and complex), primitive return
    @Kernl.NoCache("NoCacheMultiMixedParamPrimitiveReturnKernl")
    suspend fun multiMixedParamPrimitiveReturn(id: String, bar: ServiceRequest): Int

    // Multiple mixed parameters (primitive and complex), complex return
    @Kernl.NoCache("NoCacheMultiMixedParamComplexReturnKernl")
    suspend fun multiMixedParamComplexReturn(id: String, bar: ServiceRequest): ServiceResponse

    // Multiple mixed nullable parameters (primitive and complex), primitive return
    @Kernl.NoCache("NoCacheMultiNullableMixedParamPrimitiveReturnKernl")
    suspend fun multiNullableMixedParamPrimitiveReturn(id: String?, bar: ServiceRequest?): Int

    // Multiple mixed nullable parameters (primitive and complex), complex return
    @Kernl.NoCache("NoCacheMultiNullableMixedParamComplexReturnKernl")
    suspend fun multiNullableMixedParamComplexReturn(id: String?, bar: ServiceRequest?): ServiceResponse
}