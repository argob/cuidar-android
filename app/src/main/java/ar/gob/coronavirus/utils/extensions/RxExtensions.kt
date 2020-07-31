package ar.gob.coronavirus.utils.extensions

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@JvmOverloads
fun <T> Single<T>.applySchedulers(subscribeOn: Scheduler = Schedulers.io(),
                                  observeOn: Scheduler = AndroidSchedulers.mainThread()) =
        this.subscribeOn(subscribeOn)
                .observeOn(observeOn)