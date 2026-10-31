# Clean Architecture Code Examples

## Example 1: Pure Domain Layer (KMP commonMain)

```kotlin
package com.example.domain.model

/**
 * Type-safe value class representing a unique user identifier.
 */
@JvmInline
value class UserId(val value: String)

/**
 * Immutable domain model representing an authenticated user.
 */
data class User(
    val id: UserId,
    val username: String,
    val email: String,
    val isVerified: Boolean,
)
```

```kotlin
package com.example.domain.repository

import com.example.domain.model.User
import com.example.domain.model.UserId
import kotlinx.coroutines.flow.Flow

/**
 * Domain boundary contract for user data retrieval and observation.
 */
interface UserRepository {
    fun observeUser(userId: UserId): Flow<User>
    suspend fun refreshUser(userId: UserId): Result<Unit>
}
```

```kotlin
package com.example.domain.usecase

import com.example.domain.model.User
import com.example.domain.model.UserId
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Interactor responsible for observing live user profile updates.
 */
class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    /**
     * Emits continuous updates for the requested [userId].
     */
    operator fun invoke(userId: UserId): Flow<User> = userRepository.observeUser(userId)
}
```

---

## Example 2: Data Layer Extension Mappers & Repository

```kotlin
package com.example.data.mapper

import com.example.data.local.UserEntity
import com.example.data.remote.UserDto
import com.example.domain.model.User
import com.example.domain.model.UserId

fun UserEntity.toDomain(): User = User(
    id = UserId(id),
    username = username,
    email = email,
    isVerified = isVerified,
)

fun UserDto.toEntity(): UserEntity = UserEntity(
    id = id,
    username = userName,
    email = userEmail,
    isVerified = isEmailVerified,
)
```

```kotlin
package com.example.data.repository

import com.example.data.local.UserDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.data.remote.UserApiService
import com.example.domain.model.User
import com.example.domain.model.UserId
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: UserApiService,
) : UserRepository {

    override fun observeUser(userId: UserId): Flow<User> =
        userDao.observeUserById(userId.value).map { it.toDomain() }

    override suspend fun refreshUser(userId: UserId): Result<Unit> = runCatching {
        val dto = apiService.fetchUser(userId.value)
        userDao.upsertUser(dto.toEntity())
    }
}
```

---

## Example 3: Presentation Layer (`@HiltViewModel` & Non-Deprecated Compose Import)

```kotlin
package com.example.presentation.user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel // ✅ MANDATORY NON-DEPRECATED IMPORT
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.User
import com.example.domain.model.UserId
import com.example.domain.usecase.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ❌ FORBIDDEN DEPRECATED IMPORT:
// import androidx.hilt.navigation.compose.hiltViewModel

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
) : ViewModel() {
    fun observeUser(userId: UserId): Flow<User> = getUserUseCase(userId)
}

@Composable
fun UserScreen(
    modifier: Modifier = Modifier,              // ✅ Declaration: VERY TOP (#1 parameter)
    userId: UserId,
    viewModel: UserViewModel = hiltViewModel(), // ✅ Uses androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
) {
    val user by viewModel.observeUser(userId).collectAsStateWithLifecycle(initialValue = null)
    // Pass modifier and user to stateless UserContent(..., modifier = modifier)
}
```
