# 📐 App Design Specification Examples

Reference template for an architecture specification in `/docs/architecture/`.

---

## Sample Architecture Document: `docs/architecture/user-authentication-design.md`

```markdown
# 📐 System Architecture: User Authentication Subsystem

## 📋 System Overview
Provides secure biometric and token-based user authentication with offline credential caching and token refresh handling.

---

## 🏛️ Layered Component Architecture
```mermaid
classDiagram
    class AuthViewModel {
        +StateFlow~AuthState~ authState
        +loginWithBiometrics()
        +loginWithCredentials(email, password)
    }

    class LoginUseCase {
        +invoke(credentials) Flow~AuthResult~
    }

    class AuthRepository {
        <<interface>>
        +authenticate(credentials) Flow~AuthResult~
        +refreshToken() Flow~Token~
    }

    class AuthRepositoryImpl {
        -SecureKeyStore keyStore
        -AuthRemoteApi remoteApi
        +authenticate(credentials) Flow~AuthResult~
    }

    AuthViewModel --> LoginUseCase
    LoginUseCase --> AuthRepository
    AuthRepositoryImpl ..|> AuthRepository
```

---

## 🔄 Sequence Diagram: Biometric Authentication Flow
```mermaid
sequenceDiagram
    autonumber
    actor User
    participant Screen as BiometricPromptUI
    participant VM as AuthViewModel
    participant UC as AuthenticateBiometricsUseCase
    participant Repo as AuthRepositoryImpl
    participant Keystore as AndroidKeyStore / WebCrypto

    User->>Screen: Tap 'Sign in with Biometrics'
    Screen->>VM: triggerBiometrics()
    VM->>UC: invoke()
    UC->>Repo: decryptSavedToken()
    Repo->>Keystore: Request Crypto Cipher
    Keystore-->>Repo: Decrypted Auth Token
    Repo-->>UC: Success(AuthToken)
    UC-->>VM: Emit Authenticated State
    VM-->>Screen: Navigate to Dashboard
```

---

## 📝 Use Case Specification: UC-010 Biometric Login

| Field | Detail |
| :--- | :--- |
| **Identifier** | `UC-010: Biometric Login` |
| **Actor** | Mobile User |
| **Pre-Condition** | Biometrics registered on device; user logged in previously. |
| **Trigger** | User launches app when session is expired. |
| **Happy Path** | 1. Prompt displays system biometric dialog.<br/>2. User scans fingerprint/face.<br/>3. KeyStore decrypts session token.<br/>4. User is directed to home screen. |
| **Exception Flow** | Biometric scan fails 3 times ➔ Fallback to master password prompt. |
```
