package com.github.svenjacobs.kdux

import com.svenjacobs.kdux.Action
import com.svenjacobs.kdux.State
import com.svenjacobs.kdux.reducer
import io.kotlintest.matchers.*
import io.kotlintest.specs.StringSpec

class MiddlewareTest : StringSpec() {

    init {

        val results = mutableListOf<String>()
        val middleware1 = { _: State?,
                            action: Action,
                            next: NextMiddleware<Action> ->

            results.add("middleware1")

            val newAction = when (action) {
                is Action.Message -> Action.Message(message = action.message + "1")
                else -> action
            }

            next(newAction)
        }
        val middleware2 = { _: State?,
                            action: Action,
                            next: NextMiddleware<Action> ->

            results.add("middleware2")

            val newAction = when (action) {
                is Action.Message -> Action.Message(message = action.message + "2")
                else -> action
            }

            next(newAction)
        }

        val store = Kdux.createStore(
                reducer = reducer,
                middlewares = setOf(middleware1, middleware2))

        "middlewares should be called in order" {
            results should beEmpty()

            store.dispatch(Action.Message("Hello world"))

            results should haveSize(2)

            results[0] shouldBe "middleware1"
            results[1] shouldBe "middleware2"
        }

        "middlewares should transform action" {
            store.state shouldBe null

            store.dispatch(Action.Message("Hello world"))

            store.state shouldNotBe null
            store.state!!.message shouldBe "Hello world12"
        }
    }
}
