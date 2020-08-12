package ar.gob.coronavirus.data.remoto

import ar.gob.coronavirus.data.remoto.modelo.AdviceCount
import io.reactivex.Single
import retrofit2.http.GET

interface AdviceService {
    @GET("index.json")
    fun requestAdviceCount(): Single<AdviceCount>
}