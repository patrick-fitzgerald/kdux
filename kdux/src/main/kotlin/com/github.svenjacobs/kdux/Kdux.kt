package com.github.svenjacobs.kdux

object Kdux {

    @JvmOverloads
    @JvmStatic
    fun <State, Action> createStore(reducer: Reducer<State, Action>,
                                    initialState: State? = null,
                                    middlewares: Set<Middleware<State, Action>> = emptySet()) =
            Store(reducer = reducer,
                  initialState = initialState,
                  middlewares = middlewares)
}
