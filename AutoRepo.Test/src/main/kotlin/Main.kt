package io.github.mattshoe.shoebox

import io.github.mattshoe.shoebox.annotations.AutoRepo
import io.github.mattshoe.shoebox.autorepo.MyServiceRepository

data class ServiceResponse(
    val foo: String
)

interface MyService {

    @AutoRepo.SingleMemoryCache(name = "MyServiceRepository")
    suspend fun get(string: String, derp: Boolean, flerp: Byte): ServiceResponse
}

fun main() {
    val repo: MyServiceRepository? = null
    println("Hello World!")
}