package extrydev.app.yuknaklet.view

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.navigation.NavHostController
import androidx.room.Room
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler
import com.amazonaws.regions.Regions
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult
import extrydev.app.yuknaklet.database.UserDatabase
import extrydev.app.yuknaklet.database.UserInfo
import extrydev.app.yuknaklet.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch
import java.lang.Exception

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(navController: NavHostController, viewModel: RegisterViewModel = hiltViewModel()) {
    val phoneNumber = remember { mutableStateOf(TextFieldValue()) }
    val pw = remember { mutableStateOf(TextFieldValue()) }
    val control = remember { mutableStateOf(false) }
    val userName = remember { mutableStateOf(TextFieldValue()) }
    val userSurname = remember { mutableStateOf(TextFieldValue()) }
    Scaffold(Modifier.fillMaxSize(), topBar = {
    }, content = { pd ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(pd), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(value = userName.value, onValueChange = {
                userName.value = it
            }, placeholder = {
                Text(text = "İsim")
            })
            TextField(value = userSurname.value, onValueChange = {
                userSurname.value = it
            }, placeholder = {
                Text(text = "Soyisim")
            })
            TextField(value = phoneNumber.value, onValueChange = {
                phoneNumber.value = it
            }, placeholder = {
                Text(text = "Telefon numarası")
            })
            TextField(value = pw.value, onValueChange = {
                pw.value = it
            }, placeholder = {
                Text(text = "Şifre")
            })
            Button(onClick = { control.value = !control.value }) {

            }
        }
    }, bottomBar = {})
    if(control.value)
    {
        Pool(userName = userName.value.text, userSurname = userSurname.value.text, phone = phoneNumber.value.text, password = pw.value.text, navController, viewModel)
        control.value = !control.value
    }
}
@Composable
fun Pool(userName: String, userSurname: String, phone: String, password: String, navController: NavController, viewModel: RegisterViewModel = hiltViewModel()) {
    var token : String
    val context = LocalContext.current
    var accessToken: String
    val db: UserDatabase = Room.databaseBuilder(context,UserDatabase::class.java,"UserInfo").build()
    val userDao = db.userDao()
    val userPool = CognitoUserPool(
        context,
        "eu-west-2_TlvBXBLw1",
        "1lqrq6d4opel5sng5fus2lua14",
        null,
        Regions.EU_WEST_2
    )
    val userAttributes = CognitoUserAttributes()
    userPool.signUpInBackground(phone,password,userAttributes,null,object: SignUpHandler {
        override fun onSuccess(user: CognitoUser?, signUpResult: SignUpResult?) {
            user!!.getSessionInBackground(object : AuthenticationHandler {
                override fun onSuccess(
                    userSession: CognitoUserSession?,
                    newDevice: CognitoDevice?
                ) {
                    token = userSession!!.idToken.jwtToken
                    Log.d("onur","idToken: $token")
                    accessToken = userSession.refreshToken.token
                    viewModel.setAuthToken(token)
                    viewModel.beRegister(userName, userSurname, phone, "normal")
                    viewModel.viewModelScope.launch {
                        viewModel.errorMessage.collect { message ->
                            if (message.isEmpty()) {
                                val userr = UserInfo(accessToken)
                                userDao.insert(userr)
                                navController.navigate("homePage")
                            }
                        }
                    }
                }
                override fun getAuthenticationDetails(
                    authenticationContinuation: AuthenticationContinuation?,
                    userId: String?
                ) {
                    // Oturum açma detayları sağlanmalı
                    val authenticationDetails = AuthenticationDetails(phone, password, null)
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
                    Log.d("onur","Register failure")
                    // Oturum açma işlemi başarısız oldu
                }
            })
            Log.d("onur","Sign Up is Success")
            Log.d("onur","userConfirm: ${signUpResult!!.userConfirmed}")
        }
        override fun onFailure(exception: Exception?) {
            Log.d("onur","Sign Up Failure: exception = $exception")
        }
    })
}