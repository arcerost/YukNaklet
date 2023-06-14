package extrydev.app.yuknaklet.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import extrydev.app.yuknaklet.repository.YukNakletRepository
import extrydev.app.yuknaklet.service.AuthInterceptor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authInterceptor: AuthInterceptor, private val repository: YukNakletRepository) : ViewModel() {
    val errorMessage = MutableStateFlow("")
    fun beRegister(userName: String, userSurname: String, phone: String, userType: String){
        viewModelScope.launch {
            val result = repository.postRegisterApi(userName, userSurname, phone, userType)
            if(result.message != null && result.message != "")
            {
                result.message.let {
                    errorMessage.value = it
                }
            }
        }
    }
    fun setAuthToken(token: String) {
        authInterceptor.updateToken(token)
    }
}