package ar.gob.coronavirus.flujos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel : ViewModel() {
    private val disposable = CompositeDisposable()

    private val _locationPermissionResultLiveData = MutableLiveData<Boolean>()
    val locationPermissionResultLiveData: LiveData<Boolean>
        get() = _locationPermissionResultLiveData
    private val _showLocationPermissionDialogLiveData = MutableLiveData<Int>()
    val showLocationPermissionDialogLiveData: LiveData<Int>
        get() = _showLocationPermissionDialogLiveData

    fun setPermissionDialogResult(result: Boolean) {
        _locationPermissionResultLiveData.value = result
    }

    fun showLocationPermission(permissionType: Int) {
        _showLocationPermissionDialogLiveData.value = permissionType
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    protected fun addDisposable(disposable: Disposable) {
        this.disposable.add(disposable)
    }
}