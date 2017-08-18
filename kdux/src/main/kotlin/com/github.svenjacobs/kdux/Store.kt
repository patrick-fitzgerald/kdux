package com.github.svenjacobs.kdux

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Store<State, in Action> internal constructor(private val reducer: Reducer<State, Action>,
                                                   initialState: State? = null,
                                                   middlewares: Set<Middleware<State, Action>> = emptySet()) {

    inner class Subscription internal constructor(private val listener: Listener) {

        fun unsubscribe() = listeners.remove(listener)
    }

    private val reducerMiddleware = { _: State?, action: Action, _: NextMiddleware<Action> ->
        state = reducer(state, action)
        listeners.forEach(Listener::invoke)
    }
    private val lock = ReentrantLock()
    private val listeners = mutableSetOf<Listener>()
    private val middlewares = middlewares + reducerMiddleware

    @Volatile
    var state: State? = initialState
        private set

    fun dispatch(action: Action) {
        lock.withLock {
            val iterator = middlewares.iterator()

            fun next(nextAction: Action) {
                if (iterator.hasNext())
                    iterator.next()(state, nextAction, ::next)
            }

            iterator.next()(state, action, ::next)
        }
    }

    fun subscribe(listener: Listener) =
            Subscription(listener).also {
                listeners.add(listener)
            }
}
