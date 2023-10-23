# Conscious Compose optimization cheat sheet

## Type stability

**Stable**:

- All primitive types and String.
- Functional types (lambdas).
- Classes with all fields of stable type and declared as val. 
- Enum (even if you specify a var field).
- Types marked `@Immutable` or `@Stable`.

**Unstable**:

- Classes with at least one field of unstable type or declared as var.
- All classes from external modules and libraries that do not have a Compose compiler (`List`, `Set`, `Map` and other collections, `LocalDate`, `LocalTime`, `Flow`...).

**Generics** (`MyClass<T>`) — check first by the structure of the generic, and then by the specified type.

**Predefined stable external types and generics**: `Pair`, `Result`, `Comparator`, `ClosedRange`, collections from the `kotlinx.collections.immutable` library, `dagger.Lazy` and others.

**Types with computable stability** — check when receiving specific objects in runtime:

- Types that are declared in other modules with Compose compiler enabled.
- Interfaces (the check is based on the derived class, the object of which will be passed as argument).

**@Immutable and @Stable contract**

1. The result of `equals` will always return the same result for the same two instances.
2. When a public property of the type changes, composition will be notified.
3. All public property types are stable.

Pay special attention to equals: use the data class or write your own implementation.

Stability from annotations is inherited by child types.

## Skippability of the function

The function is skippable if it doesn't have unstable parameters.

Mark UI models `@Immutable` or `@Stable`.

Don't pass unnecessary data.

For unstable external types, use a value class wrapper with an annotation.

Lambda isn't wrapped in `remember { }` if it captures unstable and computable in runtime variables or var.

Always wrap calls to ViewModel (including method references) in `remember { }`.

Prefer arguments received inside lambda:
```kotlin
@Composable
fun MyComposableItem(items: List<MyClass>) {
    // Instead of this
    ItemWidget { items[5].doSomething() } 
 
    // Do this
    ItemWidget(item[5]) { item -> item.doSomething() }
}
```

Reading the state:

```kotlin
val LocalContentAlpha = compositionLocalOf { 1f }

@Composable
fun MyComposable1() {
    val counter1: MutableState<Int> = remember { mutableStateOf(0) }
    var counter2: Int by remember { mutableStateOf(0) }
    MyComposable2(counter1, { counter2 })
}

@Composable
fun MyComposable2(counter1: State<Int>, counterProvider2: () -> Int) {
    Text("Counter = ${counter1.value}") // Reading the state
    Text("Counter = ${counterProvider2()}") // Reading the state
    Text("Counter = ${LocalContentAlpha.current}") // Reading the state
}
```

A restartable function can be restarted, be a restart scope.

**When restartability and skippability are not needed**:

- composable function data rarely or never changes;

- composable function simply calls other skippable composable functions:

  - function without complex logic and without `State<T>`, calling a minimum of other composable functions;
  - wrapper around another function (parameter mapper).

**@NonRestartableComposable** removes restartability and skippability.

```kotlin
@Composable
@NonRestartableComposable
fun ColumnScope.SpacerHeight(height: Dp) {
    Spacer(modifier = Modifier.height(height))
}
```

## Optimization of frequently changing elements

You should optimize reading of `State<T>` only where the state changes frequently and affects a lot of content!

**Derived state** - if the derived state will change less frequently than the original ones.

```kotlin
val listState = rememberLazyListState() 
val showButton by remember { 
    derivedStateOf { listState.firstVisibleItemIndex > 0 } 
}
```

Deferred reading of states in composable functions:

```kotlin
@Composable
fun MyComposable1() {
    val scrollState = rememberScrollState()
    val counter = remember { mutableStateOf(0) }

    MyList(scrollState)
    MyComposable2(counter1, { scrollState.value })
}

@Composable
fun MyComposable2(counter: State<Int>, scrollProvider: () -> Int) {
    // Reading the state in MyComposable2
    Text(text = "My counter = ${counter.value}")
    Text(text = "My scroll = ${scrollProvider()}")
}
```

Deferred reading of states in Compose phases:

```kotlin
@Composable 
fun Example() {
    var state by remember { mutableStateOf(0) }

    Text(
        // Reading the state in Composition phase
        "My state = $state",
        Modifier
            .layout { measurable, constraints ->
                // Reading the state in Layout phase
                val size = IntSize(state, state)
            }
            .drawWithCache {
                // Reading the state in Drawing phase
                val color = state
            }
    )
}
```

Reducing the recomposition area:

```kotlin
@Composable
fun Promo(timer: State<Int>) {
    Text("Sample text")
    Image()

    // Old timer code right in the Promo function
    // Text("${timer.value} seconds left")

    // New timer code
    Timer(timer)
}

@Composable
fun Timer(timer: State<Int>) {
    // Timer code and reading the timer state (timer.value) inside
}
```

key and contentType:

```kotlin
LazyColumn {
    items(
        items = messages,
        key = { message -> message.id },
        contentType = { it.type }
    ) { message ->
        MessageRow(message)
    }
}
```
```kotlin
Column {
    widgets.forEach { widget ->
        key(widget.id) {
            MyWidget(widget)
        }
    }
}
```

## Layout

Write custom layouts.

Don't unnecessarily change the size and position of the list items.

Set a fixed image size.

Avoid `onGloballyPositioned()` and `onSizeChanged()`.

`SubcomposeLayout` defers composition until Measurement in the Layout phase, which is why it is slow. The main implementations are `BoxWithConstraint`, `LazyRow` and `LazyColumn`.

For small lists, use `Row` and `Column`.

**Intrinsic measurements** perform pre-computation of children and have almost no impact on performance.

## Modifiers

### **Custom modifiers**

- If it's stateless, just use functions.
- If it's stateful, use `Modifier.Node` (`ModifierNodeElement`).
- If a composable function is called in the modifier, use `Modifier.composed`.


### **Reusing modifiers**

With frequent recomposition:

```kotlin
val reusableModifier = Modifier
            .padding(12.dp)
            .background(Color.Gray),

@Composable 
fun LoadingWheelAnimation() { 
    val animatedState = animateFloatAsState(...) 

    LoadingWheel( 
        modifier = reusableModifier, 
        // Reading a frequently changing state
        animatedState = animatedState.value
    ) 
}
```

In lists:

```kotlin
val reusableItemModifier = Modifier
            .padding(bottom = 12.dp)
            .size(216.dp)
            .clip(CircleShape) 

@Composable 
private fun AuthorList(authors: List) { 
    LazyColumn { 
        items(authors) { 
            AsyncImage(modifier = reusableItemModifier) 
        } 
    } 
}
```

## Long calculations during recomposition

Long calculations only in `ViewModel` or in `remember { }`.

Without long calculations in the UI State getters.

When to use `remember { }`:

- For any long or memory-consuming operations that can be executed more than once but should not be executed before changing the keys (if needed) passed to `remember()`, especially for frequent recomposition.
- For wrapping lambdas with unstable external variables.
- For classes without overridden `equals`.

For objects that need to be preserved between recompositions (e.g. `State<T>`).

## Other tips

`staticCompositionLocalOf` — if the value is unlikely to change. When it is used by a huge number of composable functions. Examples: `LocalContext`, `LocalLifecycleOwner`, `LocalDensity`, `LocalFocusManager`.

`compositionLocalOf` — if the value will change frequently. There are subscription costs. Examples: `LocalConfiguration`, `LocalAlpha`.

`@ReadOnlyComposable `— If a composable function performs only read operations. Example: reading only `CompositionLocal` (color from the theme). 

Use less ComposeView.

## Debugging and monitoring

- Composable metrics: stability and skippability testing.
- Layout Inspector: debugging recomposition.
- Rebugger.
- Compose State in the debugger.
- Composition tracing.
- Benchmarking.