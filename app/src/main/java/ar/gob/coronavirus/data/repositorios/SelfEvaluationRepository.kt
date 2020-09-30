package ar.gob.coronavirus.data.repositorios

import ar.gob.coronavirus.data.ConvertirClasesRemotasEnLocales.updateUser
import ar.gob.coronavirus.data.UserStatus
import ar.gob.coronavirus.data.local.UserDAO
import ar.gob.coronavirus.data.local.modelo.LocalUser
import ar.gob.coronavirus.data.remoto.Api
import ar.gob.coronavirus.data.remoto.modelo.SelfEvaluationResponse
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteSelfEvaluation
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SelfEvaluationRepository(private val api: Api, private val userDao: UserDAO) : ISelfEvaluationRepository {
    fun confirmSelfEvaluation(remoteSelfEvaluation: RemoteSelfEvaluation): Single<LocalUser> {
        return userDao.select()
                .flatMap { localUser: LocalUser ->
                    api.confirmSelfEvaluation(localUser.dni, localUser.gender, remoteSelfEvaluation)
                            .map { remoteUser: SelfEvaluationResponse -> updateUser(localUser, remoteUser) }
                }
                .flatMap { localUser: LocalUser -> userDao.update(localUser).toSingle { localUser } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getUserStatus(): Single<UserStatus> {
        return userDao
                .select()
                .map { it.currentState.userStatus }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getUser(): Single<LocalUser> {
        return userDao.select()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}