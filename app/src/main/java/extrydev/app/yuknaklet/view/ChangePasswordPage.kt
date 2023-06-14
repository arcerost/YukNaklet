package extrydev.app.yuknaklet.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient

@Composable
fun ChangePasswordPage(navController: NavController) {
    val x = ""
    val y = ""
    val z = ""
    changePW(x,y,z)
    navController.navigate("")
}


fun changePW(oldPw: String, newPw: String, token: String)
{
    val provider = CognitoIdentityProviderClient.builder()
        .region(software.amazon.awssdk.regions.Region.EU_WEST_2)
        .build()
    provider.changePassword {
        it.previousPassword(oldPw)
            .proposedPassword(newPw)
            .accessToken(token)
            .build()
    }
}