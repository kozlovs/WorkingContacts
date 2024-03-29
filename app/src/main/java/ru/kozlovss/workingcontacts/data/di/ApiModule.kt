package ru.kozlovss.workingcontacts.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.kozlovss.workingcontacts.BuildConfig
import ru.kozlovss.workingcontacts.data.eventsdata.api.EventApiService
import ru.kozlovss.workingcontacts.data.jobsdata.api.JobApiService
import ru.kozlovss.workingcontacts.data.mediadata.api.MediaApiService
import ru.kozlovss.workingcontacts.data.mywalldata.api.MyWallApiService
import ru.kozlovss.workingcontacts.data.postsdata.api.PostApiService
import ru.kozlovss.workingcontacts.data.userdata.api.UserApiService
import ru.kozlovss.workingcontacts.data.walldata.api.UserWallApiService
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    @Provides
    @Singleton
    fun provideLogging(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttp(
        logging: HttpLoggingInterceptor,
        appAuth: AppAuth
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .addInterceptor { chain ->
            appAuth.token?.let {
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", it)
                    .build()
                return@addInterceptor chain.proceed(request)
            }
            chain.proceed(chain.request())
        }
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideEventApiService(
        retrofit: Retrofit
    ): EventApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideJobApiService(
        retrofit: Retrofit
    ): JobApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideMediaApiService(
        retrofit: Retrofit
    ): MediaApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideMyWallApiService(
        retrofit: Retrofit
    ): MyWallApiService = retrofit.create()

    @Provides
    @Singleton
    fun providePostApiService(
        retrofit: Retrofit
    ): PostApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideUserApiService(
        retrofit: Retrofit
    ): UserApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideUserWallApiService(
        retrofit: Retrofit
    ): UserWallApiService = retrofit.create()

    companion object {
        const val BASE_URL = "${BuildConfig.BASE_URL}api/"
    }
}