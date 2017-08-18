package com.github.svenjacobs.kdux

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

interface Subscription {

    fun unsubscribe(): Boolean
}

interface Store<out State, in Action> {

    val state: State?

    fun dispatch(action: Action)

    fun subscribe(listener: Listener): Subscription
}

internal class StoreImpl<State, in Action>(private val reducer: Reducer<State, Action>,
                                           initialState: State? = null,
                                           middlewares: Set<Middleware<State, Action>> = emptySet()) : Store<State, Action> {

    internal inner class SubscriptionImpl(private val listener: Listener) : Subscription {

        override fun unsubscribe() = listeners.remove(listener)
    }

    private val reducerMiddleware = { _: State?, action: Action, _: NextMiddleware<Action> ->
        state = reducer(state, action)
        listeners.forEach(Listener::invoke)
    }
    private val lock = ReentrantLock()
    private val listeners = mutableSetOf<Listener>()
    private val middlewares = middlewares + reducerMiddleware

    @Volatile
    override var state: State? = initialState
        private set

    override fun dispatch(action: Action) {
        lock.withLock {
            val iterator = middlewares.iterator()

            fun next(nextAction: Action) {
                if (iterator.hasNext())
                    iterator.next()(state, nextAction, ::next)
            }

            iterator.next()(state, action, ::next)
        }
    }

    override fun subscribe(listener: Listener) =
            SubscriptionImpl(listener).also {
                listeners.add(listener)
            }
}
