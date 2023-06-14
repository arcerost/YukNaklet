package extrydev.app.yuknaklet.view

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.room.Room
import extrydev.app.yuknaklet.database.UserDatabase
import extrydev.app.yuknaklet.viewmodel.HomePageViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun HomePage(navController: NavController, viewModel : HomePageViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val db: UserDatabase = Room.databaseBuilder(context, UserDatabase::class.java,"UserInfo")
        .fallbackToDestructiveMigration()
        .build()
    val userDao = db.userDao()
    var userToken: String?
    LaunchedEffect(key1 = userDao){
        userDao.getUser()
    }
    runBlocking {
        userToken = if(userDao.anyData() == 0) {
            ""
        } else
            userDao.getUser().token
        Log.d("onur",userToken!!)
    }
    val message = viewModel.errorMessage.collectAsState()
    val user = viewModel.user.collectAsState()
}