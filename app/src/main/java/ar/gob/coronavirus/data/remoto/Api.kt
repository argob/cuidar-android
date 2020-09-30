package ar.gob.coronavirus.data.remoto

import ar.gob.coronavirus.data.remoto.AppAuthenticator.Companion.setAccessToken
import ar.gob.coronavirus.data.remoto.modelo.*
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteSelfEvaluation
import ar.gob.coronavirus.utils.PreferencesManager.saveRefreshToken
import io.reactivex.Single

class Api(private val apiService: CovidApiService) {

    fun getUserInformation(dni: String, gender: String) =
            apiService.getUser(dni, gender)

    fun updateUser(dni: String, gender: String, phone: String, remoteAddress: RemoteAddress?, remoteLocation: RemoteLocation?) =
            apiService.updateUser(dni, gender, UserInformationUpdate(phone, remoteAddress, remoteLocation))

    fun confirmSelfEvaluation(dni: Long, gender: String, remoteSelfEvaluation: RemoteSelfEvaluation) =
            apiService.confirmSelfEvaluation(dni.toString(), gender, remoteSelfEvaluation)

    fun authorize(dni: String, gender: String, identification: String): Single<Token> {
        return apiService
                .authorize(UserAuthorization(dni, gender, identification))
                .doOnSuccess { (token, refreshToken) ->
                    setAccessToken(token)
                    saveRefreshToken(refreshToken)
                }
    }

    fun registerPush(dni: Long, gender: String, pushId: String) =
            apiService.registerToken(dni, gender, PushBody(pushId))

    fun unregisterPush(dni: String, gender: String, pushId: String) =
            apiService.unregisterToken(dni, gender, PushBody(pushId))

    fun refresh(token: String, something: String) =
            apiService.refresh(TokenRefreshBody(token, something))
}