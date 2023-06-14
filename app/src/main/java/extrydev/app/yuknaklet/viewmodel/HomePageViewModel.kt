package extrydev.app.yuknaklet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import extrydev.app.yuknaklet.database.UserDao
import extrydev.app.yuknaklet.model.UserInfoResponse
import extrydev.app.yuknaklet.model.UserInfoResponseList
import extrydev.app.yuknaklet.repository.YukNakletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(private val userDao: UserDao, private val repository: YukNakletRepository) : ViewModel() {
    val errorMessage = MutableStateFlow("")
    var user = MutableStateFlow(UserInfoResponseList("","","","","","","", ""))
    init {
        viewModelScope.launch {
            val result = repository.postUserInfo()
            if(result.message != null && result.message != "")
            {
                result.message.let {
                    errorMessage.value = it
                }
            }
            else
            {
                user.value = result.data!!.response
            }
        }
    }
}