package io.github.mattshoe.shoebox

import io.github.mattshoe.shoebox.annotations.AutoRepo
import io.github.mattshoe.shoebox.autorepo.MyServiceRepository
import kotlinx.coroutines.runBlocking

data class ServiceResponse(
    val foo: String
)

interface MyService {

    @AutoRepo.SingleMemoryCache("MyServiceRepository")
    suspend fun get(string: String, derp: Boolean, flerp: Byte): ServiceResponse
}

fun main() = runBlocking {
    val repo: MyServiceRepository? = null
    repo?.let {
        it.initialize(
            MyServiceRepository.Params("", true, 0),
            forceRefresh = true
        )
    }
    println("Hello World!")
}