package ar.gob.coronavirus.utils.extensions

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

@JvmOverloads
fun <T> Single<T>.applySchedulers(subscribeOn: Scheduler = Schedulers.io(),
                                  observeOn: Scheduler = AndroidSchedulers.mainThread()) =
        this.subscribeOn(subscribeOn)
                .observeOn(observeOn)

fun <T> delayedSingle(result: T, delay: Long): Single<T> = Single.just(result).delay(delay, TimeUnit.SECONDS)