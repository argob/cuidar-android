package ar.gob.coronavirus.data.remoto

import ar.gob.coronavirus.data.remoto.modelo.AdviceCount
import ar.gob.coronavirus.utils.many.TextUtils
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET

interface AdviceService {
    @GET("index.json")
    fun requestAdviceCount(): Single<AdviceCount>

    companion object {


        @JvmStatic
        fun create(): AdviceService {
            return Retrofit.Builder().run {
                baseUrl(TextUtils.ADVICE_URL)
                addConverterFactory(GsonConverterFactory.create())
                addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                build()
            }.create<AdviceService>()
        }
    }
}