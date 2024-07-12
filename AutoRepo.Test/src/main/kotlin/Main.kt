package io.github.mattshoe.shoebox

import io.github.mattshoe.shoebox.annotations.AutoRepo
import io.github.mattshoe.shoebox.autorepo.MyRepository
import kotlinx.coroutines.runBlocking

data class MyResponseData(
    val foo: String
)

interface MyService {
    @AutoRepo.SingleMemoryCache("MyRepository")
    suspend fun getMyResponse(id: String, someParam: Int, otherParam: Boolean): MyResponseData
}

fun main() = runBlocking {
    val service: MyService = object : MyService {
        override suspend fun getMyResponse(id: String, someParam: Int, otherParam: Boolean): MyResponseData {
            TODO("Not yet implemented")
        }
    }
    val repo = MyRepository.Factory { id, someParam, otherParam ->
        service.getMyResponse(id, someParam, otherParam)
    }

    println("Hello World!")
}