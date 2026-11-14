# 🎯 Fact-First Examples (Do's & Don'ts)

## Example 1: Reviewing a Compose Clickable Modifier

### ❌ DON'T (Sycophantic & Vague)
> Great job setting up the Column! Your code looks really clean and well-structured! You might maybe want to double-check the modifier order just in case, because sometimes padding and clickable can behave a bit differently. Keep up the great work!

### ✅ DO (Direct, Honest & Fact-Backed)
> **Bug in `CartItemRow.kt:L24-L27`**: `Modifier.padding(16.dp).clickable { ... }` shrinks the touch target and clips the ripple inside the padding.
> - **Why**: Compose applies modifiers sequentially from outer to inner ([Official Modifier Order Docs](https://developer.android.com/develop/ui/compose/modifiers-order)).
> - **Fix**: Apply `clip` ➔ `clickable` ➔ `padding`:
>   ```kotlin
>   Column(
>       modifier = Modifier
>           .fillMaxWidth()
>           .clip(RoundedCornerShape(12.dp))
>           .clickable(onClick = onClick)
>           .padding(16.dp),
>   ) { ... }
>   ```

---

## Example 2: Challenging a Flawed Technical Assumption

### ❌ DON'T (Blindly Supportive)
> Yes, using `GlobalScope.launch` in your ViewModel is a totally valid way to get started quickly! You can always refactor it later if needed.

### ✅ DO (Honest Technical Pushback with Data)
> **No — `GlobalScope.launch` leaks work past `ViewModel.onCleared()` and violates structured concurrency.**
> - **Impact**: Network/DB coroutines continue executing after the user leaves the screen and cannot be cancelled by `viewModelScope` ([Android Coroutines Best Practices](https://developer.android.com/kotlin/coroutines/coroutines-best-practices)).
> - **Fix**: Use `viewModelScope.launch { ... }` (or `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ...)` for `StateFlow`).
