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
