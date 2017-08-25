[![Release](https://jitpack.io/v/svenjacobs/kdux.svg)](https://jitpack.io/#svenjacobs/kdux)

Lightweight, predictable state container for Kotlin, Java and Android inspired by [Redux](https://github.com/reactjs/redux).

# Installation

Kdux is distributed via [JitPack](https://jitpack.io/). Please see the JitPack [releases](https://jitpack.io/#svenjacobs/kdux)
page for instructions on how to add the library to your Gradle or Maven project.

# Getting started

The [core concepts](http://redux.js.org/docs/introduction/CoreConcepts.html) of Kdux are basically the same as Redux.
Please make yourself familiar with actions, reducers, state and store.

First of all define your application state. The application state should be an immutable data class.

```kotlin
data class User(val firstName: String,
                val lastName: String)
                
data class State(val users: List<User> = emptyList(),
                 val message: String = "")
```

Now define actions for modifying your state. It's good practice to leverage sealed classes for actions as they will
simplify the reducer code later on.

```kotlin
sealed class Action {

    data class SetUsers(val users: List<User>) : Action()

    data class AddUser(val user: User) : Action()

    object ClearUsers : Action()

    data class SetMessage(val message: String) : Action()
}
```

Now write the reducer.

```kotlin
val reducer = { state: State?, action: Action ->
    (state ?: State()).let { newState ->
        when (action) {
            is Action.SetUsers -> newState.copy(users = action.users)
            is Action.AddUsers -> newState.copy(users = newState.users + action.user)
            is Action.ClearUsers -> newState.copy(users = emptyList())
            is Action.SetMessage -> newState.copy(message = action.message)
        }
    }
}
```

As you can see the reduce does **not** modify the original state but will always create and return a copy of the state
with the desired modifications applied. This is an **essential** concept of Kdux! Also you can see that since we've used
sealed classes for actions we don't need an `else` branch since all action types have been handled by `when`.

Finally create the store. The store instance should be a singleton object within your application and ideally be managed
by a dependency injection framework like [Dagger](https://google.github.io/dagger/) for instance.

```kotlin
class AppStore(private val store: Store<State, Action>) : Store<State, Action> by store

val store = Kdux.createStore(reducer)
val appStore = AppStore(store)
```

We wrap the `Store` class in an instance of `AppStore` utilizing Kotlin's [class delegation](https://kotlinlang.org/docs/reference/delegation.html)
since Dagger cannot inject classes with generic arguments due to JVM's type erasure.

Now that you have a singleton instance of store you can access your state anytime via `store.state`. The `state` property 
is nullable since `null` is considered a legal value for the state. `null` is the initial default value of your state 
unless an initial state has been passed to `createStore()`. 

Again, do **not** modify your state directly via `store.state`! The state class should be immutable. Dispatch actions 
to modify the state.

```kotlin
appStore.dispatch(Action.AddUser(firstName = "Walter", lastName = "White"))
appStore.dispatch(Action.SetMessage("Hello world"))
```

Subscribe to your store to be notified when the state has been modified (through the reducer, of course).

```kotlin
val subscription = appStore.subscribe {
    // State was modified
    appStore.state?.users.forEach(::println)
}

// Later when the listener isn't required anymore
subscription.unsubscribe()
```
