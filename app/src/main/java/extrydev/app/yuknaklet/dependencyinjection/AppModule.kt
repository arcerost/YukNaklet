package extrydev.app.yuknaklet.dependencyinjection


import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import extrydev.app.yuknaklet.database.UserDao
import extrydev.app.yuknaklet.database.UserDatabase
import extrydev.app.yuknaklet.repository.YukNakletRepository
import extrydev.app.yuknaklet.service.AuthInterceptor
import extrydev.app.yuknaklet.service.GooglePlacesApiService
import extrydev.app.yuknaklet.service.YukNakletApiService
import extrydev.app.yuknaklet.util.Constants.BASE_URL
import extrydev.app.yuknaklet.util.Constants.GOOGLE_PLACES_BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor() = AuthInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

    // YukNaklet servisi için Retrofit istemcisi
    @Provides
    @Singleton
    @Named("YukNaklet")
    fun provideYukNakletRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL) // YukNaklet API'yi hedefleyen temel URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // Google Places servisi için Retrofit istemcisi
    @Provides
    @Singleton
    @Named("GooglePlaces")
    fun provideGooglePlacesRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(GOOGLE_PLACES_BASE_URL) // Google Places API'yi hedefleyen temel URL
            .baseUrl("https://maps.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // YukNaklet API servisi
    @Provides
    @Singleton
    fun provideYukNakletApiService(@Named("YukNaklet") yukNakletRetrofit: Retrofit): YukNakletApiService =
        yukNakletRetrofit.create(YukNakletApiService::class.java)

    // Google Places API servisi
    @Provides
    @Singleton
    fun provideGooglePlacesApiService(@Named("GooglePlaces") googlePlacesRetrofit: Retrofit): GooglePlacesApiService =
        googlePlacesRetrofit.create(GooglePlacesApiService::class.java)

    @Singleton
    @Provides
    fun provideYukNakletRepository(api:YukNakletApiService) = YukNakletRepository(api)

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java, "user_database"
        ).build()
    }

    @Provides
    fun provideUserDao(database: UserDatabase): UserDao {
        return database.userDao()
    }
}
