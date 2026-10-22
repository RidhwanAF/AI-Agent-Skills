# 🧪 React Testing Examples

Production-grade code patterns for testing React components with Vitest, React Testing Library, and MSW.

---

## 1. Component Test with Accessible Roles & User Event

```typescript
// src/features/counter/Counter.test.tsx
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import React, { useState } from 'react';

function Counter({ initial = 0 }: { initial?: number }) {
  const [count, setCount] = useState(initial);
  return (
    <div>
      <p aria-live="polite">Current count: {count}</p>
      <button type="button" onClick={() => setCount(c => c + 1)}>Increment</button>
    </div>
  );
}

describe('Counter Component', () => {
  it('increments counter when button is clicked by user', async () => {
    const user = userEvent.setup();
    render(<Counter initial={5} />);

    expect(screen.getByText(/current count: 5/i)).toBeInTheDocument();

    const button = screen.getByRole('button', { name: /increment/i });
    await user.click(button);

    expect(screen.getByText(/current count: 6/i)).toBeInTheDocument();
  });
});
```

---

## 2. Mock Service Worker (MSW) Network Handler Setup

```typescript
// src/mocks/handlers.ts
import { http, HttpResponse } from 'msw';

export const handlers = [
  http.get('https://api.example.com/v1/products', () => {
    return HttpResponse.json([
      { id: 'p1', title: 'Ergonomic Desk', price: 299 },
      { id: 'p2', title: '4K Monitor', price: 450 },
    ]);
  }),
];
```

---

## 3. Pure Domain Unit Test

```typescript
// src/domain/pricing/calculateTotal.test.ts
import { describe, it, expect } from 'vitest';
import { calculateCartTotal } from './calculateTotal';

describe('calculateCartTotal', () => {
  it('applies discount code correctly and calculates sales tax', () => {
    const items = [
      { price: 100, quantity: 2 },
      { price: 50, quantity: 1 },
    ];
    const discountPercent = 10;
    const taxRate = 0.08;

    const total = calculateCartTotal(items, discountPercent, taxRate);

    // subtotal = 250, discount = 25 -> 225, tax = 18 -> 243
    expect(total).toBe(243);
  });
});
```
