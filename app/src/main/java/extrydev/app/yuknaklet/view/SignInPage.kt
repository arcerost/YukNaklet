package extrydev.app.yuknaklet.view

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.room.Room
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.regions.Regions
import extrydev.app.yuknaklet.database.UserDatabase
import extrydev.app.yuknaklet.database.UserInfo
import extrydev.app.yuknaklet.service.AuthInterceptor
import extrydev.app.yuknaklet.viewmodel.SignInViewModel
import kotlinx.coroutines.launch
import java.lang.Exception

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInPage(navController: NavController, viewModel: SignInViewModel = hiltViewModel()) {
    val username = remember { mutableStateOf(TextFieldValue()) }
    val password = remember { mutableStateOf(TextFieldValue()) }
    val tr = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(value = username.value, onValueChange = {
            username.value = it
        }, placeholder = {
            Text(text = "Phone Number")
        })
        TextField(value = password.value, onValueChange = {
            password.value = it
        }, placeholder = {
            Text(text = "Password")
        })
        Button(onClick = { tr.value = !tr.value }) {

        }
    }
    if (tr.value) {
        SignIn(username = username.value.text, password = password.value.text, navController = navController)
        tr.value = !tr.value
    }
}

@Composable
fun SignIn(username: String, password: String, navController: NavController) {
    val viewModel: SignInViewModel = hiltViewModel()
    val authInterceptor = AuthInterceptor()
    val context = LocalContext.current
    val db: UserDatabase = Room.databaseBuilder(context, UserDatabase::class.java,"UserInfo").build()
    val userDao = db.userDao()
    val userPool = CognitoUserPool(
        context,
        "eu-west-2_TlvBXBLw1",
        "1lqrq6d4opel5sng5fus2lua14",
        null,
        Regions.EU_WEST_2
    )
    val cognitoUser = userPool.getUser(username)
    cognitoUser.getSessionInBackground(object : AuthenticationHandler {
        override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
            // Oturum başarıyla açıldı
            Log.d("onur","Giriş başarılı")
            val token = userSession!!.idToken.jwtToken
            authInterceptor.updateToken(token)
            val userr = UserInfo(token)
            viewModel.viewModelScope.launch {
                userDao.insert(userr)
                navController.navigate("homePage")
            }
        }

        override fun getAuthenticationDetails(authenticationContinuation: AuthenticationContinuation?, username: String?) {
            // Oturum açma detayları sağlanmalı
            val authenticationDetails = AuthenticationDetails(username, password, null)
            authenticationContinuation?.setAuthenticationDetails(authenticationDetails)
            authenticationContinuation?.continueTask()
        }

        override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {
            // Multi-factor authentication (MFA) kodu isteniyor
            // Eğer uygulamanız MFA kullanıyorsa bu metod içinde kodu alıp devam ettirebilirsiniz
        }

        override fun authenticationChallenge(continuation: ChallengeContinuation?) {
            // Auth challenge isteniyor
            // Bu method genellikle CUSTOM_CHALLENGE tipindeki auth challenge'ları handle etmek için kullanılır
        }

        override fun onFailure(exception: Exception?) {
            Log.d("onur","Login failure")
            // Oturum açma işlemi başarısız oldu
        }
    })
}