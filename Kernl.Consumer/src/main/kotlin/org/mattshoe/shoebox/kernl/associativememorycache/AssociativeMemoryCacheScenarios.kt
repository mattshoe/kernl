package org.mattshoe.shoebox.kernl.associativememorycache

import org.mattshoe.shoebox.kernl.annotations.Kernl
import org.mattshoe.shoebox.kernl.models.ServiceRequest
import org.mattshoe.shoebox.kernl.models.ServiceResponse

interface AssociativeMemoryCacheScenarios {

    // Single primitive parameter, primitive return
    @Kernl.AssociativeCache.InMemory("SingleParamPrimitiveReturnKernl")
    suspend fun singleParamPrimitiveReturn(id: String): Int

    // Multiple same primitive parameters, primitive return
    @Kernl.AssociativeCache.InMemory("MultiSameParamPrimitiveReturnKernl")
    suspend fun multiSameParamPrimitiveReturn(id: String, bar: String): Int

    // Multiple different primitive parameters, primitive return
    @Kernl.AssociativeCache.InMemory("MultiParamPrimitiveReturnKernl")
    suspend fun multiParamPrimitiveReturn(id: String, bar: Int): Int

    // Single primitive parameter, complex return
    @Kernl.AssociativeCache.InMemory("SingleParamComplexReturnKernl")
    suspend fun singleParamComplexReturn(id: String): ServiceResponse

    // Multiple different primitive parameters, complex return
    @Kernl.AssociativeCache.InMemory("MultiParamComplexReturnKernl")
    suspend fun multiParamComplexReturn(id: String, bar: Int): ServiceResponse

    // Multiple same complex parameters, complex return
    @Kernl.AssociativeCache.InMemory("MultiSameParamComplexReturnKernl")
    suspend fun multiSameParamComplexReturn(id: String, bar: ServiceResponse): ServiceResponse

    // Multiple different complex parameters, complex return
    @Kernl.AssociativeCache.InMemory("MultiComplexParamComplexReturnKernl")
    suspend fun multiComplexParamComplexReturn(id: ServiceRequest, bar: ServiceResponse): ServiceResponse

    // Single nullable primitive parameter, primitive return
    @Kernl.AssociativeCache.InMemory("SingleNullableParamPrimitiveReturnKernl")
    suspend fun singleNullableParamPrimitiveReturn(id: String?): Int

    // Multiple nullable same primitive parameters, primitive return
    @Kernl.AssociativeCache.InMemory("MultiNullableSameParamPrimitiveReturnKernl")
    suspend fun multiNullableSameParamPrimitiveReturn(id: String?, bar: String?): Int

    // Multiple nullable different primitive parameters, primitive return
    @Kernl.AssociativeCache.InMemory("MultiNullableParamPrimitiveReturnKernl")
    suspend fun multiNullableParamPrimitiveReturn(id: String?, bar: Int?): Int

    // Single nullable primitive parameter, complex return
    @Kernl.AssociativeCache.InMemory("SingleNullableParamComplexReturnKernl")
    suspend fun singleNullableParamComplexReturn(id: String?): ServiceResponse

    // Multiple nullable same complex parameters, complex return
    @Kernl.AssociativeCache.InMemory("MultiNullableSameParamComplexReturnKernl")
    suspend fun multiNullableSameParamComplexReturn(id: String?, bar: ServiceResponse?): ServiceResponse

    // Multiple nullable different complex parameters, complex return
    @Kernl.AssociativeCache.InMemory("MultiNullableComplexParamComplexReturnKernl")
    suspend fun multiNullableComplexParamComplexReturn(id: ServiceRequest?, bar: ServiceResponse?): ServiceResponse

    // Single complex parameter, primitive return
    @Kernl.AssociativeCache.InMemory("SingleComplexParamPrimitiveReturnKernl")
    suspend fun singleComplexParamPrimitiveReturn(id: ServiceRequest): Int

    // Single complex parameter, complex return
    @Kernl.AssociativeCache.InMemory("SingleComplexParamComplexReturn")
    suspend fun singleComplexParamComplexReturn(id: ServiceRequest): ServiceResponse

    // Multiple different complex parameters, primitive return
    @Kernl.AssociativeCache.InMemory("MultiComplexParamPrimitiveReturnKernl")
    suspend fun multiComplexParamPrimitiveReturn(id: ServiceRequest, bar: ServiceResponse): Int

    // Single nullable complex parameter, primitive return
    @Kernl.AssociativeCache.InMemory("SingleNullableComplexParamPrimitiveReturnKernl")
    suspend fun singleNullableComplexParamPrimitiveReturn(id: ServiceRequest?): Int

    // Multiple nullable different complex parameters, primitive return
    @Kernl.AssociativeCache.InMemory("MultiNullableComplexParamPrimitiveReturnKernl")
    suspend fun multiNullableComplexParamPrimitiveReturn(id: ServiceRequest?, bar: ServiceResponse?): Int

    // Multiple mixed parameters (primitive and complex), primitive return
    @Kernl.AssociativeCache.InMemory("MultiMixedParamPrimitiveReturnKernl")
    suspend fun multiMixedParamPrimitiveReturn(id: String, bar: ServiceRequest): Int

    // Multiple mixed parameters (primitive and complex), complex return
    @Kernl.AssociativeCache.InMemory("MultiMixedParamComplexReturnKernl")
    suspend fun multiMixedParamComplexReturn(id: String, bar: ServiceRequest): ServiceResponse

    // Multiple mixed nullable parameters (primitive and complex), primitive return
    @Kernl.AssociativeCache.InMemory("MultiNullableMixedParamPrimitiveReturnKernl")
    suspend fun multiNullableMixedParamPrimitiveReturn(id: String?, bar: ServiceRequest?): Int

    // Multiple mixed nullable parameters (primitive and complex), complex return
    @Kernl.AssociativeCache.InMemory("MultiNullableMixedParamComplexReturnKernl")
    suspend fun multiNullableMixedParamComplexReturn(id: String?, bar: ServiceRequest?): ServiceResponse
}