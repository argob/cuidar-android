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
    fun convertirUsuario(remoteUser: RemoteUser): UserWithPermits {
        val geoBD = LocalLocation(
                remoteUser.location?.latitude ?: "",
                remoteUser.location?.longitude ?: ""
        )
        val localUser = LocalUser(
                remoteUser.dni,
                remoteUser.gender,
                remoteUser.birthDate,
                remoteUser.names,
                remoteUser.lastNames,
                remoteUser.phone,
                createLocalAddress(remoteUser.address),
                geoBD,
                createLocalState(remoteUser.currentState)
        )
        return UserWithPermits(localUser, createCirculationPermits(remoteUser.currentState, remoteUser.dni))
    }

    private fun createLocalState(remoteStatus: RemoteStatus?): LocalState {
        return LocalState(
                userStatus = remoteStatus?.userStatus ?: UserStatus.UNKNOWN,
                expirationDate = remoteStatus?.expirationDate ?: "",
                coep = LocalCoep(remoteStatus?.coep?.coep
                        ?: "", remoteStatus?.coep?.contactInformation ?: ""),
                pims = remoteStatus?.pims?.let { LocalPims(it.tag ?: "", it.reason ?: "") }
        )
    }

    private fun createCirculationPermits(remoteStatus: RemoteStatus?, userId: Long): List<LocalCirculationPermit> {
        return remoteStatus?.circulationPermits?.map {
            LocalCirculationPermit(userId,
                    it.sube ?: "",
                    it.plate ?: "",
                    it.url,
                    it.permitExpirationDate,
                    it.activityType,
                    it.reason,
                    it.certificateId)
        } ?: emptyList()
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