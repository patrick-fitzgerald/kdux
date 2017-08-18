package com.github.svenjacobs.kdux

import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.haveSize
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class ListenerTest : StringSpec() {

    init {

        val store = Kdux.createStore(reducer)
        val results = mutableListOf<String>()
        val listener1: () -> Unit = { results.add("listener1") }
        val listener2: () -> Unit = { results.add("listener2") }

        "listeners should be called in order" {
            store.subscribe(listener1)
            store.subscribe(listener2)

            results should beEmpty()

            store.dispatch(Action.Message("Hello world"))

            results should haveSize(2)
            results[0] shouldBe "listener1"
            results[1] shouldBe "listener2"
        }

        "listeners should unsubscribe" {
            results should beEmpty()

            val subscription = store.subscribe(listener1)

            subscription.unsubscribe() shouldBe true
            subscription.unsubscribe() shouldBe false

            store.dispatch(Action.Message("Hello world"))

            results should beEmpty()
        }
    }
}
