# ⚡ React State Management Examples

Code examples showing TanStack Query custom hooks with typed key factories and granular Zustand store selectors.

---

## 1. Typed Query Key Factory & Query Hook

```typescript
// src/features/profile/profileQueries.ts
import { useQuery } from '@tanstack/react-query';
import { fetchUserProfile } from './profileApi';
import type { UserId, UserProfile } from './profileTypes';

export const profileQueryKeys = {
  all: ['profile'] as const,
  detail: (userId: UserId) => [...profileQueryKeys.all, 'detail', userId] as const,
  preferences: (userId: UserId) => [...profileQueryKeys.detail(userId), 'preferences'] as const,
};

/**
 * Custom hook to load user profile with fresh cache window.
 */
export function useUserProfile(userId: UserId) {
  return useQuery({
    queryKey: profileQueryKeys.detail(userId),
    queryFn: () => fetchUserProfile(userId),
    staleTime: 1000 * 60 * 3, // 3 minutes
    gcTime: 1000 * 60 * 15,
  });
}
```

---

## 2. Granular Zustand Store with Actions

```typescript
// src/stores/filterStore.ts
import { create } from 'zustand';

interface FilterState {
  readonly searchTerm: string;
  readonly selectedCategory: string | null;
  readonly isOnlyInStock: boolean;
  setSearchTerm: (term: string) => void;
  setSelectedCategory: (category: string | null) => void;
  toggleInStock: () => void;
  resetFilters: () => void;
}

export const useFilterStore = create<FilterState>((set) => ({
  searchTerm: '',
  selectedCategory: null,
  isOnlyInStock: false,
  setSearchTerm: (term) => set({ searchTerm: term }),
  setSelectedCategory: (category) => set({ selectedCategory: category }),
  toggleInStock: () => set((state) => ({ isOnlyInStock: !state.isOnlyInStock })),
  resetFilters: () => set({ searchTerm: '', selectedCategory: null, isOnlyInStock: false }),
}));
```

---

## 3. Atomic Subscription at Call Site

```tsx
// src/features/products/ProductSearchHeader.tsx
import React from 'react';
import { useFilterStore } from '../../stores/filterStore';

export function ProductSearchHeader() {
  // Granular subscription: re-renders ONLY when searchTerm changes
  const searchTerm = useFilterStore((state) => state.searchTerm);
  const setSearchTerm = useFilterStore((state) => state.setSearchTerm);

  return (
    <input
      type="search"
      value={searchTerm}
      onChange={(e) => setSearchTerm(e.target.value)}
      placeholder="Search products..."
      className="rounded-lg border px-3 py-2"
    />
  );
}
```
