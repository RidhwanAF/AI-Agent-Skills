# 💉 Koin DI Production Examples

Idiomatic, production-ready code samples demonstrating module definition, Compose screen injection, Application initialization, and unit test verification.

---

## 1. Modular Koin Modules (`di/AppModules.kt`)

```kotlin
package com.example.app.di

import androidx.room.Room
import com.example.app.data.local.AppDatabase
import com.example.app.data.remote.UserApiService
import com.example.app.data.repository.UserRepositoryImpl
import com.example.app.domain.repository.UserRepository
import com.example.app.domain.usecase.GetUserProfileUseCase
import com.example.app.presentation.profile.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

val networkModule = module {
    single {
        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(UserApiService::class.java)
    }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_database.db"
        ).build()
    }
    single { get<AppDatabase>().userDao() }
}

val repositoryModule = module {
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
}

val useCaseModule = module {
    factoryOf(::GetUserProfileUseCase)
}

val viewModelModule = module {
    viewModelOf(::ProfileViewModel)
}

val appModules = listOf(
    networkModule,
    databaseModule,
    repositoryModule,
    useCaseModule,
    viewModelModule,
)
```

---

## 2. Jetpack Compose Screen Injection (`ProfileScreen.kt`)

```kotlin
package com.example.app.presentation.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileContent(
        state = uiState,
        onRefresh = viewModel::onRefresh,
        modifier = modifier.fillMaxSize()
    )
}
```

---

## 3. Application Initialization (`MainApplication.kt`)

```kotlin
package com.example.app

import android.app.Application
import com.example.app.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MainApplication)
            modules(appModules)
        }
    }
}
```

---

## 4. Static Graph Verification Test (`KoinGraphTest.kt`)

```kotlin
package com.example.app.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.example.app.data.remote.UserApiService
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class KoinGraphTest : KoinTest {

    @Test
    fun verifyAppModules() {
        appModules.verify(
            extraTypes = listOf(
                Context::class,
                SavedStateHandle::class,
                UserApiService::class,
            )
        )
    }
}
```
