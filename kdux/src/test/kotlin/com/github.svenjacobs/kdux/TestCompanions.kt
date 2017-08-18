package com.svenjacobs.kdux

data class User(val firstName: String,
                val lastName: String)

data class State(val message: String? = null,
                 val users: List<User> = emptyList())

sealed class Action {
    data class Message(val message: String) : Action()
    data class AddUser(val user: User) : Action()
    data class RemoveUser(val user: User) : Action()
}

val reducer = { state: State?, action: Action ->
    (state ?: State()).let { newState ->
        when (action) {
            is Action.Message -> newState.copy(message = action.message)
            is Action.AddUser -> newState.copy(users = newState.users.plus(action.user))
            is Action.RemoveUser -> newState.copy(users = newState.users.minus(action.user))
        }
    }
}
