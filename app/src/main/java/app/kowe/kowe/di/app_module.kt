package app.kowe.kowe.di

import app.kowe.kowe.KoweSettings
import app.kowe.kowe.data.locals.RecordRepository
import app.kowe.kowe.data.locals.RecordRepositoryImpl
import app.kowe.kowe.data.locals.db.KoweDatabase
import app.kowe.kowe.services.http.HttpService
import app.kowe.kowe.ui.viewmodels.HomeViewModels
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

val appModule = module {

    single { KoweSettings(androidApplication()) }

    single { Executors.newCachedThreadPool() }

    single { KoweDatabase.getInstance(androidApplication()) }

    factory { get<KoweDatabase>().recordDao() }

    factory { RecordRepositoryImpl(get()) as RecordRepository }

    viewModel { HomeViewModels(get(), get()) }
}

val networkModule = module {

    factory {

        OkHttpClient.Builder()
                .callTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build()
    }

    single {

        val settings: KoweSettings = get()
        return@single Interceptor { chain ->

            val account = settings.getAuthenticatedAccount()
            val request = chain.request()
            if (account == null) {
                return@Interceptor chain.proceed(request)
            }

            val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${account.token}")
                    .build()

            return@Interceptor chain.proceed(newRequest)
        }
    }

    single {

        val BASE_URL = ""

        // retrofit builder
        val builder = Retrofit.Builder().baseUrl(BASE_URL)

        // Bearer token injector interceptor
        val interceptor: Interceptor = get()

        // OkHttpClient builder
        val clientBuilder: OkHttpClient.Builder = get()

        // add Interceptor
        clientBuilder.addInterceptor(interceptor)

        val client = clientBuilder.build()
        builder.client(client)

        return@single builder.build()
    }

    single {

        val retrofit: Retrofit = get()
        return@single retrofit.create(HttpService::class.java)
    }
}

val appModules = listOf(appModule, networkModule)