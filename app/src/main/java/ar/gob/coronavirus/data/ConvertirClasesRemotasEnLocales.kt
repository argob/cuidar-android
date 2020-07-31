package ar.gob.coronavirus.data

import ar.gob.coronavirus.data.local.modelo.*
import ar.gob.coronavirus.data.remoto.modelo.RemoteAddress
import ar.gob.coronavirus.data.remoto.modelo.RemoteStatus
import ar.gob.coronavirus.data.remoto.modelo.RemoteUser
import ar.gob.coronavirus.data.remoto.modelo.SelfEvaluationResponse

object ConvertirClasesRemotasEnLocales {
    @JvmStatic
    fun updateUser(currentUser: LocalUser, response: SelfEvaluationResponse): LocalUser {
        return currentUser.copy(dni = response.dni,
                gender = response.gender,
                currentState = createLocalState(response.currentState))
    }

    @JvmStatic
    fun convertirUsuario(remoteUser: RemoteUser): LocalUser {
        val geoBD = LocalLocation(
                remoteUser.location?.latitude ?: "",
                remoteUser.location?.longitude ?: ""
        )
        return LocalUser(
                remoteUser.dni,
                remoteUser.gender,
                remoteUser.birthDate,
                remoteUser.names,
                remoteUser.lastNames,
                remoteUser.phone,
                remoteUser.currentState?.circulationPermit?.sube ?: "",
                remoteUser.currentState?.circulationPermit?.plate ?: "",
                createLocalAddress(remoteUser.address),
                geoBD,
                createLocalState(remoteUser.currentState)
        )
    }

    private fun createLocalState(remoteStatus: RemoteStatus?): LocalState {
        return LocalState(
                userStatus = remoteStatus?.userStatus ?: UserStatus.UNKNOWN,
                expirationDate = remoteStatus?.expirationDate ?: "",
                coep = LocalCoep(remoteStatus?.coep?.coep
                        ?: "", remoteStatus?.coep?.contactInformation ?: ""),
                circulationPermit = LocalCirculationPermit(remoteStatus?.circulationPermit?.qr
                        ?: "",
                        remoteStatus?.circulationPermit?.permitExpirationDate ?: "",
                        remoteStatus?.circulationPermit?.serviceStatus ?: 0,
                        remoteStatus?.circulationPermit?.activityType ?: ""),
                pims = remoteStatus?.pims?.let { LocalPims(it.tag ?: "", it.reason ?: "") }
        )
    }

    private fun createLocalAddress(remoteAddress: RemoteAddress?): LocalAddress {
        return LocalAddress(remoteAddress?.province ?: "",
                remoteAddress?.locality ?: "",
                remoteAddress?.apartment ?: "",
                remoteAddress?.street ?: "",
                remoteAddress?.number ?: "",
                remoteAddress?.floor ?: "",
                remoteAddress?.door ?: "",
                remoteAddress?.postalCode ?: "",
                remoteAddress?.others ?: "")
    }
}