package io.github.mattshoe.shoebox

import io.github.mattshoe.shoebox.annotations.AutoRepo
import io.github.mattshoe.shoebox.autorepo.MyServiceRepository
import io.github.mattshoe.shoebox.data.repo.singleCacheLiveRepository
import kotlinx.coroutines.runBlocking

data class ServiceResponse(
    val foo: String
)

interface MyService {
    @AutoRepo.SingleMemoryCache("MyServiceRepository")
    suspend fun get(string: String, derp: Boolean, flerp: Byte): ServiceResponse
}

fun main() = runBlocking {
    val service: MyService = object : MyService {
        override suspend fun get(string: String, derp: Boolean, flerp: Byte): ServiceResponse {
            TODO("Not yet implemented")
        }
    }
    val repo: MyServiceRepository = MyServiceRepository.Factory(service::get)
    repo.let {
        it.fetch(
            MyServiceRepository.Params(
                "loerm ipsum",
                true,
                42
            )
        )
    }
    println("Hello World!")
}