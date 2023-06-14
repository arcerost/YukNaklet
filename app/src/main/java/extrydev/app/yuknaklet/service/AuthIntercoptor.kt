package extrydev.app.yuknaklet.service

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {
    @Volatile
    var token: String? = null
    @Synchronized
    fun updateToken(token: String) {
        this.token = token
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (token != null) {
            val url = request.url
            request = request.newBuilder()
                .url(url)
                .addHeader("Authorization", "$token")
                .build()
        }
        return chain.proceed(request)
    }
}
