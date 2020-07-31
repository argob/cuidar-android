package ar.gob.coronavirus

import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

object GlobalActionsManager {

    private val publishSubject = PublishSubject.create<GlobalAction>()

    fun subscribe(action: (GlobalAction) -> Unit): Disposable {
        return publishSubject.subscribe(action)
    }

    fun post(action: GlobalAction) {
        publishSubject.onNext(action)
    }

}

enum class GlobalAction {
    FORCE_UPDATE, LOGOUT;
}