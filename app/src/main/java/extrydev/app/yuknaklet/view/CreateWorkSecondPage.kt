package extrydev.app.yuknaklet.view

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import extrydev.app.yuknaklet.model.CreateWorkFromWhere
import extrydev.app.yuknaklet.model.CreateWorkToWhere
import extrydev.app.yuknaklet.viewmodel.CreateWorkViewModel
import java.util.ArrayList

@Composable
fun CreateWorkSecondPage(fromWhere: CreateWorkFromWhere, toWhere: CreateWorkToWhere, navController: NavController, viewModel: CreateWorkViewModel = hiltViewModel() ) {
    val fuel = 0
    val kdv = 0
    val price = 0
    val commision = 0
    val carType = ""
    val category = ""
    val description = ""
    val images = ArrayList<String>()
    viewModel.createWork(fromWhere,toWhere,fuel,kdv,price,commision,carType,category,description,images)
    viewModel.errorMessage
    navController.navigate("")
}