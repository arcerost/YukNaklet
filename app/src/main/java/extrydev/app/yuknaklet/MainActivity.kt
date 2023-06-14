package extrydev.app.yuknaklet

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import dagger.hilt.android.AndroidEntryPoint
import extrydev.app.yuknaklet.database.UserDatabase
import extrydev.app.yuknaklet.ui.theme.YukNakletTheme
import extrydev.app.yuknaklet.view.ChangePasswordPage
import extrydev.app.yuknaklet.view.CreateWorkPage
import extrydev.app.yuknaklet.view.EditProfilePage
import extrydev.app.yuknaklet.view.HomePage
import extrydev.app.yuknaklet.view.OnboardingPage
import extrydev.app.yuknaklet.view.RegisterPage
import extrydev.app.yuknaklet.view.SignInPage

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("FlowOperatorInvokedInComposition", "NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YukNakletTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MainViewModel by viewModels()
                    val vmDes by viewModel.getStartDestination().collectAsState(initial = null)
                    val db: UserDatabase = Room.databaseBuilder(this, UserDatabase::class.java,"UserInfo")
                        .build()
                    val userDao = db.userDao()
                    val startDes = remember { mutableStateOf<String?>(null) }
                    LaunchedEffect(key1 = userDao, key2 = viewModel) {
                        var token: String? = null
                        if(userDao.anyData() != 0) {
                            token = userDao.getUser().token
                        }
                        startDes.value = when {
                            vmDes == "true" && !token.isNullOrEmpty() -> "createWorkPage"
                            vmDes == "false" && token.isNullOrEmpty() -> "createWorkPage"
                            vmDes == "true" && token.isNullOrEmpty() -> "createWorkPage"
                            else -> "signInPage"
                        }
                    }
                    startDes.value?.let { startDestination ->
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = startDestination)
                        {
                            composable("registerPage")
                            {
                                RegisterPage(navController)
                            }
                            composable("signInPage")
                            {
                                SignInPage(navController)
                            }
                            composable("onBoardingPage")
                            {
                                OnboardingPage(navController)
                            }
                            composable("homePage")
                            {
                                HomePage(navController = navController)
                            }
                            composable("changePasswordPage")
                            {
                                ChangePasswordPage(navController = navController)
                            }
                            composable("editProfilePage/{userId}/{genericUserId}", arguments = listOf(
                                navArgument("userId"){
                                    type = NavType.StringType
                                },
                                navArgument("genericUserId"){
                                    type = NavType.StringType
                                }
                            ))
                            {
                                val userId = remember { it.arguments!!.getString("userId")}
                                val genericUserId = remember { it.arguments!!.getString("genericUserId")}
                                EditProfilePage(userId!!,genericUserId!!)
                            }
                            composable("createWorkPage")
                            {
                                CreateWorkPage(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

