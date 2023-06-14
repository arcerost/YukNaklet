package extrydev.app.yuknaklet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import extrydev.app.yuknaklet.model.UserInfoResponseList
import extrydev.app.yuknaklet.repository.YukNakletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(private val yukNakletRepository: YukNakletRepository) : ViewModel() {
    val errorMessage = MutableStateFlow("")
    val errorMessageForInfo = MutableStateFlow("")
    var user = MutableStateFlow(UserInfoResponseList("","","","","","","",""))

    fun editProfile(userName: String, userSurname: String, userPhoto: String?)
    {
        viewModelScope.launch {
            val result = yukNakletRepository.postUserEditProfile(userName, userSurname, userPhoto)
            if(result.message != null && result.message != "")
            {
                result.message.let {
                    errorMessage.value = it
                }
            }
            else{
                val resultForInfo = yukNakletRepository.postUserInfo()
                if(resultForInfo.message != null && resultForInfo.message != "")
                {
                    resultForInfo.message.let {
                        errorMessageForInfo.value = it
                    }
                }
                else
                {
                    user.value = resultForInfo.data!!.response
                }
            }
        }
    }
}