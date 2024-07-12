package io.github.mattshoe.shoebox

import io.github.mattshoe.shoebox.annotations.AutoRepo
import io.github.mattshoe.shoebox.autorepo.MyRepository
import kotlinx.coroutines.runBlocking

data class MyResponseData(
    val foo: String
)

interface MyService {
    @AutoRepo.SingleMemoryCache("MyRepository")
    suspend fun getMyResponse(id: String): MyResponseData
}

fun main() = runBlocking {
    val service: MyService = object : MyService {
        override suspend fun getMyResponse(id: String): MyResponseData {
            TODO("Not yet implemented")
        }
    }
    val repo = MyRepository.Factory { id ->
        service.getMyResponse(id)
    }

    println("Hello World!")
}