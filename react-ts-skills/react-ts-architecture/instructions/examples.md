# 🏛️ React & TypeScript Architecture Examples

Production-grade code patterns demonstrating pure TypeScript domain models, schema boundary validation, and DTO-to-Domain mappers.

---

## 1. Domain Layer: Pure Types & Branded Identifiers

```typescript
// src/domain/user/user.types.ts

/** Type-safe branded primitive preventing ID mix-ups. */
export type UserId = string & { readonly __brand: unique symbol };

/** Factory helper for creating type-safe UserId. */
export const toUserId = (id: string): UserId => id as UserId;

/** User role discriminated union variant. */
export type UserRole = 'admin' | 'moderator' | 'member';

/** Pure user domain entity with zero framework dependencies. */
export interface User {
  readonly id: UserId;
  readonly name: string;
  readonly email: string;
  readonly role: UserRole;
  readonly createdAt: Date;
}

/** Pure domain computation verifying administrator privileges. */
export const isAdminUser = (user: User): boolean => user.role === 'admin';
```

---

## 2. Data Layer: DTO Schema Validation & Single-Expression Mapper

```typescript
// src/data/user/user.dto.ts
import { z } from 'zod';
import { type User, toUserId } from '../../domain/user/user.types';

/** Zod schema validating incoming wire format from HTTP API. */
export const UserResponseDtoSchema = z.object({
  id: z.string().min(1),
  full_name: z.string(),
  email_address: z.string().email(),
  role_name: z.enum(['admin', 'moderator', 'member']),
  created_at: z.string().datetime(),
});

export type UserResponseDto = z.infer<typeof UserResponseDtoSchema>;

/**
 * Maps wire DTO to clean immutable domain entity.
 */
export const mapDtoToDomainUser = (dto: UserResponseDto): User => ({
  id: toUserId(dto.id),
  name: dto.full_name,
  email: dto.email_address,
  role: dto.role_name,
  createdAt: new Date(dto.created_at),
});
```

---

## 3. Exhaustive Union Helper

```typescript
// src/domain/common/assert-never.ts

/**
 * Asserts unreachable code path at compile-time and runtime.
 */
export function assertNever(value: never, message?: string): never {
  throw new Error(message ?? `Unexpected union variant reached: ${JSON.stringify(value)}`);
}
```
