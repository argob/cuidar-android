package ar.gob.coronavirus.data

import ar.gob.coronavirus.data.local.modelo.LocalAddress
import ar.gob.coronavirus.data.local.modelo.LocalLocation
import ar.gob.coronavirus.data.remoto.modelo.RemoteAddress
import ar.gob.coronavirus.data.remoto.modelo.RemoteLocation

object LocalToRemoteMapper {
    @JvmStatic
    fun mapLocalToRemoteAddress(localAddress: LocalAddress): RemoteAddress {
        return RemoteAddress(localAddress.province,
                localAddress.locality,
                localAddress.street,
                localAddress.number,
                localAddress.floor,
                localAddress.door,
                localAddress.postalCode,
                localAddress.others,
                localAddress.apartment)
    }

    @JvmStatic
    fun mapLocalToRemoteLocation(localGeo: LocalLocation?): RemoteLocation? {
        return if (localGeo == null || localGeo.latitude.isNullOrEmpty() || localGeo.longitude.isNullOrEmpty()) {
            null
        } else {
            RemoteLocation(localGeo.latitude, localGeo.longitude)
        }
    }
}