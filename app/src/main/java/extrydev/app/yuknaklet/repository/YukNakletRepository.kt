package extrydev.app.yuknaklet.repository

import dagger.hilt.android.scopes.ActivityScoped
import extrydev.app.yuknaklet.model.Config
import extrydev.app.yuknaklet.model.ConfigResponse
import extrydev.app.yuknaklet.model.CreateWork
import extrydev.app.yuknaklet.model.CreateWorkFromWhere
import extrydev.app.yuknaklet.model.CreateWorkResponse
import extrydev.app.yuknaklet.model.CreateWorkToWhere
import extrydev.app.yuknaklet.model.EditProfile
import extrydev.app.yuknaklet.model.EditProfileResponse
import extrydev.app.yuknaklet.model.ProfileDetail
import extrydev.app.yuknaklet.model.ProfileDetailResponse
import extrydev.app.yuknaklet.model.Register
import extrydev.app.yuknaklet.model.RegisterResponse
import extrydev.app.yuknaklet.model.UserInfo
import extrydev.app.yuknaklet.model.UserInfoResponse
import extrydev.app.yuknaklet.service.YukNakletApiService
import extrydev.app.yuknaklet.util.Resource
import javax.inject.Inject


@ActivityScoped
class YukNakletRepository @Inject constructor(private val api: YukNakletApiService) {
    suspend fun postRegisterApi(userName: String, userSurname: String, phone: String, userType: String) : Resource<RegisterResponse> {
        val request = Register(userName, userSurname, phone, userType)
        return try {
            val response = api.postClientRegisterApi(request)
            when (response.error) {
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("Register Error.")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }
    suspend fun postConfig(a: String = "a") : Resource<ConfigResponse>
    {
        val request = Config(a)
        return try {
            val response = api.postClientConfig(request)
            when(response.error){
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("Config Error")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }
    suspend fun postUserInfo(a: String = "a") : Resource<UserInfoResponse>
    {
        val request = UserInfo(a)
        return try {
            val response = api.postUserInfo(request)
            when(response.error){
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("User Info Error")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }
    suspend fun postUserEditProfile(userName: String, userSurname: String, userPhoto : String?) : Resource<EditProfileResponse>
    {
        val request = EditProfile(userName, userSurname, userPhoto)
        return try {
            val response = api.postEditProfile(request)
            when(response.error){
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("Edit Profile Error")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }
    suspend fun postProfileDetail(userId: String) : Resource<ProfileDetailResponse> {
        val request = ProfileDetail(userId)
        return try {
            val response = api.postProfileDetail(request)
            when (response.error) {
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("Profile Detail Error")
            }
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }
    suspend fun postCreateWork(fromWhere: CreateWorkFromWhere, toWhere: CreateWorkToWhere, fuel: Int, kdv: Int, price: Int, commision: Int, carType: String, category: String, description: String, image: ArrayList<String>) : Resource<CreateWorkResponse>
    {
        val request = CreateWork(fromWhere, toWhere, fuel, kdv, price, commision, carType, category, description, image)
        return try {
            val response = api.postCreateWork(request)
            when (response.error) {
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("Profile Detail Error")
            }
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }
}