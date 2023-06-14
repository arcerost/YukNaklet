package extrydev.app.yuknaklet.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import extrydev.app.yuknaklet.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoilApi::class)
@Composable
fun OnboardingPage(navController: NavController, viewModel: MainViewModel = hiltViewModel()) {
    val currentPageIndex by viewModel.pageIndex.collectAsState()
    val onboardingPages by viewModel.onboardingPages.collectAsState()
    val currentPage = onboardingPages.getOrNull(currentPageIndex)
    if (currentPage != null) {
        Column {
            val painter = rememberImagePainter(data = currentPage.image, builder = {})
            Text(currentPage.title)
            Image(painter = painter, contentDescription = "image")
            Text(currentPage.desc)
            Button(
                onClick = {
                    viewModel.viewModelScope.launch {
                        if (currentPageIndex == onboardingPages.lastIndex) {
                            viewModel.setOnboardingCompleted()
                            navController.navigate("registerPage") {
                                popUpTo("onBoardingPage") { inclusive = true }
                            }
                        } else {
                            viewModel.nextPage()
                        }
                    }
                }
            ) {
                Text(text = currentPage.btnText)
            }
            TextButton(
                onClick = {
                    viewModel.viewModelScope.launch {
                        viewModel.setOnboardingCompleted()
                        navController.navigate("registerPage") {
                            popUpTo("onBoardingPage") { inclusive = true }
                        }
                    }
                }
            ) {
                Text("Skip")
            }
        }
    }
}


