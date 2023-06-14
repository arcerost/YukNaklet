package extrydev.app.yuknaklet.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import extrydev.app.yuknaklet.repository.YukNakletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val repository: YukNakletRepository) : ViewModel() {
    private val errorMessage = MutableStateFlow("")
}