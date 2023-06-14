package extrydev.app.yuknaklet.service

import extrydev.app.yuknaklet.model.Config
import extrydev.app.yuknaklet.model.ConfigResponse
import extrydev.app.yuknaklet.model.CreateWork
import extrydev.app.yuknaklet.model.CreateWorkResponse
import extrydev.app.yuknaklet.model.EditProfile
import extrydev.app.yuknaklet.model.EditProfileResponse
import extrydev.app.yuknaklet.model.ProfileDetail
import extrydev.app.yuknaklet.model.ProfileDetailResponse
import extrydev.app.yuknaklet.model.Register
import extrydev.app.yuknaklet.model.RegisterResponse
import extrydev.app.yuknaklet.model.UserInfo
import extrydev.app.yuknaklet.model.UserInfoResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface YukNakletApiService {
    @Headers("Content-Type:application/json")
    @POST("yuknaklet-client-register")
    suspend fun postClientRegisterApi(@Body request: Register) : RegisterResponse

    @Headers("Content-Type:application/json")
    @POST("yuknaklet-client-config")
    suspend fun postClientConfig(@Body request: Config) : ConfigResponse

    @Headers("Content-Type:application/json")
    @POST("yuknaklet-client-config")
    suspend fun postUserInfo(@Body request: UserInfo) : UserInfoResponse

    @Headers("Content-Type:application/json")
    @POST("yuknaklet-client-editprofile")
    suspend fun postEditProfile(@Body request: EditProfile) : EditProfileResponse

    @Headers("Content-Type:application/json")
    @POST("yuknaklet-client-profiledetail")
    suspend fun postProfileDetail(@Body request: ProfileDetail) : ProfileDetailResponse

    @Headers("Content-Type:application/json")
    @POST("yuknaklet-client-creatework")
    suspend fun postCreateWork(@Body request: CreateWork) : CreateWorkResponse
}