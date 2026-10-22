# 🎨 React UI Components Examples

Production-grade code patterns illustrating modular file decomposition, accessible interactive cards, and the strict prop ordering rule.

---

## 1. Modular Child Component with Prop Ordering Rule

```tsx
// src/features/products/components/ProductCard.tsx
import React from 'react';

export interface ProductItem {
  readonly id: string;
  readonly title: string;
  readonly priceFormatted: string;
  readonly inStock: boolean;
}

/** Props for [ProductCard]. Notice mandatory data/callbacks first, className at bottom. */
export interface ProductCardProps {
  // 1. Mandatory Data Props
  product: ProductItem;
  // 2. Mandatory Callback Props
  onAddToCart: (productId: string) => void;
  // 3. Optional Configuration Props
  isFavorite?: boolean;
  onToggleFavorite?: (productId: string) => void;
  // 4. Optional Styling & Slots (Bottom)
  className?: string;
  children?: React.ReactNode;
}

/**
 * Accessible product card component conforming to WCAG 2.2 AA.
 */
export function ProductCard({
  product,
  onAddToCart,
  isFavorite = false,
  onToggleFavorite,
  className = '',
  children,
}: ProductCardProps) {
  return (
    <article
      aria-label={`Product card: ${product.title}`}
      className={`rounded-2xl border border-neutral-200 bg-white p-5 shadow-sm transition hover:shadow-md dark:border-neutral-800 dark:bg-neutral-900 ${className}`}
    >
      <div className="flex items-start justify-between">
        <h3 className="text-lg font-semibold text-neutral-900 dark:text-neutral-100">
          {product.title}
        </h3>
        {onToggleFavorite && (
          <button
            type="button"
            onClick={() => onToggleFavorite(product.id)}
            aria-label={isFavorite ? 'Remove from favorites' : 'Add to favorites'}
            aria-pressed={isFavorite}
            className="rounded-full p-2 text-neutral-500 hover:bg-neutral-100 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 dark:hover:bg-neutral-800"
          >
            ♥
          </button>
        )}
      </div>

      <p className="mt-2 text-xl font-bold text-neutral-800 dark:text-neutral-200">
        {product.priceFormatted}
      </p>

      <div className="mt-4 flex items-center justify-between">
        <button
          type="button"
          disabled={!product.inStock}
          onClick={() => onAddToCart(product.id)}
          className="rounded-lg bg-blue-600 px-4 py-2 font-medium text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:bg-neutral-300 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2"
        >
          {product.inStock ? 'Add to Cart' : 'Out of Stock'}
        </button>
        {children}
      </div>
    </article>
  );
}
```

---

## 2. Component Consumption (Call Site Rule)

```tsx
// src/features/products/ProductListContainer.tsx
import React from 'react';
import { ProductCard } from './components/ProductCard';

export function ProductListContainer() {
  const sampleProduct = {
    id: 'prod_99',
    title: 'Mechanical Ergonomic Keyboard',
    priceFormatted: '$149.00',
    inStock: true,
  };

  return (
    <section className="p-6">
      {/* Notice: Data and callbacks at top, styling at the VERY BOTTOM */}
      <ProductCard
        product={sampleProduct}
        onAddToCart={(id) => console.log('Added:', id)}
        isFavorite={true}
        onToggleFavorite={(id) => console.log('Toggled fav:', id)}
        className="max-w-sm hover:border-blue-400"
      />
    </section>
  );
}
```
