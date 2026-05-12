# FitForge — Interview Q&A

---

## Architecture

**Q: Walk me through your app's architecture.**
A: I used Clean Architecture with three layers. The **data layer** owns Room entities, DAOs, and repositories — it's the only layer that talks to the database. The **domain layer** has pure Kotlin models and use cases like `RecommendWorkoutPlanUseCase` and `CalculateNutritionUseCase` — no Android imports, so they're easily testable. The **presentation layer** has ViewModels (using Hilt + `@HiltViewModel`) that collect state via `StateFlow` and Compose screens that observe that state. The UI is purely a function of state — no logic lives in the screens.

**Q: Why did you use MVVM and not MVI or MVP?**
A: MVVM with unidirectional data flow is the pattern Google recommends for Compose. State flows down from ViewModel to UI, events flow up via lambdas or functions. MVI would be a good next step for larger teams since it enforces stricter intent/state separation, but for a project this size MVVM is less boilerplate without sacrificing clarity.

**Q: Why Hilt over manual DI or Koin?**
A: Hilt is compile-time verified — if a dependency is missing it fails at build time, not runtime. It integrates natively with `@AndroidEntryPoint`, `@HiltViewModel`, and the Android lifecycle. Koin is runtime reflection-based which makes errors harder to catch early. For a production app, compile-time safety wins.

---

## Data

**Q: Why Room over a remote database or SharedPreferences?**
A: Fitness data is personal, sensitive, and needs to work offline. Room gives us structured queries, type-safety through DAOs, and reactive `Flow` streams so the UI updates automatically when data changes. SharedPreferences is only for simple key-value data — it can't handle relational data like workout sessions with exercise logs.

**Q: How do you handle the mapping between database entities and domain models?**
A: I separated them intentionally. `UserProfileEntity` is what Room knows about — it stores enums as strings because Room can't serialize Kotlin enums directly. `UserProfile` is the clean domain model ViewModels and use cases work with. Extension functions `toDomain()` and `toEntity()` handle conversion at the repository boundary, so the domain layer never has a Room import.

**Q: Why store `LocalDate` as a String in Room?**
A: Room doesn't natively serialize Java's `LocalDate`. The options are a custom TypeConverter or storing ISO-8601 strings. I chose strings because they're human-readable in the DB for debugging, sort correctly as strings, and don't require a TypeConverter class. The tradeoff is that queries filtering by date range need string comparison, which works fine since ISO-8601 is lexicographically ordered.

---

## Features

**Q: How does the recommendation engine work?**
A: `RecommendWorkoutPlanUseCase` maps the user's `TrainingGoal` to a pre-built 8-week plan template. Each plan encodes progressive overload through week-over-week focus changes. Then `adjustIntensity()` takes last night's sleep and today's check-in and computes a modifier between 0 and 1.1. It combines the sleep recovery impact (Optimal/Good/Moderate/Poor each map to a modifier) with a readiness score derived from mental state, physical state, energy, and inverted stress. A combined modifier below 0.3 means rest day; above 1.0 means you're peaking and can push harder.

**Q: How are nutrition targets calculated?**
A: `CalculateNutritionUseCase` uses the Mifflin-St Jeor equation for BMR (Basal Metabolic Rate), which is one of the most validated formulas. BMR × activity level multiplier gives TDEE. Then goal-specific adjustments apply: 20% caloric deficit for fat loss, 12% surplus for muscle gain. Protein is set at 2.2g/kg for muscle gain (research supports 1.6–2.2g/kg for hypertrophy), scaling down for other goals. Fat is 25% of calories, and remaining calories fill carbs.

**Q: Why hardcode the exercise library instead of fetching from an API?**
A: For an interview project this demonstrates the data modeling clearly and works offline. In production I'd either store this in the database (seeded on first launch) or fetch from a fitness API like ExerciseDB. The design is already set up for this — `ExerciseLibrary.all` is a list I could trivially replace with a repository call.

**Q: How does sleep affect the workout recommendation?**
A: `SleepLog` calculates a `RecoveryImpact` based on both duration and quality. Under 6 hours or poor quality → `POOR` (0.5x intensity modifier). 7–8h good quality → `GOOD` (0.9x). 8h+ excellent quality → `OPTIMAL` (1.0x). This combines with the check-in readiness score via averaging. So even with great sleep, a 1/5 physical state still recommends a rest day.

---

## Android / Compose

**Q: Why Jetpack Compose over XML layouts?**
A: Compose is declarative — the UI is a function of state. There's no need to manually call `setText()` or worry about view state synchronization. It also eliminates an entire class of bugs around view recycling in RecyclerViews. Google has committed to Compose as the future of Android UI, and for a new project there's no reason to start with XML.

**Q: How do you handle navigation?**
A: Navigation Compose with a `NavController`. Screens are composables mapped to string routes in a sealed class hierarchy to avoid magic strings. The bottom navigation bar tracks the current destination via `currentBackStackEntryAsState()` and `NavDestination.hierarchy` to handle nested navigation correctly. The onboarding flow is handled at startup by checking if the user profile exists, then popping the onboarding route from the backstack so back doesn't return to it.

**Q: How does state flow from ViewModel to UI?**
A: ViewModels expose `StateFlow` (not `LiveData`) which is lifecycle-aware and integrates naturally with Compose's `collectAsState()`. I use a single `UiState` data class per ViewModel where possible (like `DashboardUiState`) so the screen gets one coherent snapshot rather than multiple independent flows that could race.

**Q: What would you do differently with more time?**
A: A few things: (1) Add a progress screen with charts using Vico or a Canvas-based solution to visualize weight, sleep, and workouts over time. (2) Unit test the use cases — they're pure Kotlin so they test trivially with JUnit. (3) Add a food database (Nutritionix or Open Food Facts API) so users don't have to enter macros manually. (4) Push notifications for workout reminders. (5) Export to Google Fit or Apple Health via the Health Connect API.

---

## General Engineering

**Q: How would you scale this if it had 1 million users?**
A: The app itself is fully offline-first so client-side scale isn't an issue. If I added a backend: user profiles and workout history would go into a service behind an API, with local Room as a write-through cache for offline support. I'd use WorkManager for background sync. The recommendation engine could stay client-side (it's cheap computation) or move server-side if we wanted ML-based personalization.

**Q: How do you prevent the UI from showing stale data?**
A: Room DAOs return `Flow<T>`, which means any database write automatically triggers a new emission. The ViewModel collects that flow and the UI recomposes. For the dashboard's one-time loads I use `collectOnce` via a `launch` in `init` — the tradeoff is the dashboard doesn't auto-refresh, but a `pull-to-refresh` or navigation re-entry would trigger a reload.

**Q: How would you add offline-first sync with a backend?**
A: The repository layer is the right abstraction point. Each repository currently reads from Room only. To add sync, I'd add a remote data source alongside the local one, have the repository serve Room data immediately (for instant UI), then fetch from the network in the background and update Room — which automatically pushes the update to the UI via Flow. WorkManager handles periodic background sync. This is the standard "offline-first" pattern from the Now in Android sample.
