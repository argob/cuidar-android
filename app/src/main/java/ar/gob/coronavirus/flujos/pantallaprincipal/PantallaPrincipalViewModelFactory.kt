package ar.gob.coronavirus.flujos.pantallaprincipal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.gob.coronavirus.data.local.EncryptedDataBase
import ar.gob.coronavirus.data.remoto.AdviceService
import ar.gob.coronavirus.data.remoto.Api
import ar.gob.coronavirus.data.remoto.CovidRetrofit
import ar.gob.coronavirus.data.remoto.interceptores.HeadersInterceptor
import ar.gob.coronavirus.data.repositorios.RepositorioLogout

class PantallaPrincipalViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val headersInterceptor = HeadersInterceptor()
        val covidRetrofit = CovidRetrofit(headersInterceptor)
        val api = Api(covidRetrofit)
        val userDao = EncryptedDataBase.instance.userDao
        val repositorioLogout = RepositorioLogout(api, userDao)
        val pantallaPrincipalRepository = PantallaPrincipalRepository(api, userDao, AdviceService.create())
        try {
            return PantallaPrincipalViewModel(pantallaPrincipalRepository, repositorioLogout) as T
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw IllegalArgumentException("unexpected model class $modelClass")
    }
}