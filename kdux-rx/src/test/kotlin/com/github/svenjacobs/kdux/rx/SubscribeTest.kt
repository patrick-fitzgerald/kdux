package com.github.svenjacobs.kdux.rx

import com.github.svenjacobs.kdux.Kdux
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.WordSpec
import io.reactivex.Completable
import java.util.concurrent.TimeUnit

class SubscribeTest : WordSpec() {

    data class State(val value: String = "")

    data class Action(val value: String)

    init {

        val reducer = { state: State?, (value): Action ->
            (state ?: State()).copy(value = value)
        }

        val store = Kdux.createStore(reducer)

        "reactive streams" should {

            "receive events" {
                var count1 = 0
                var count2 = 0

                KduxRx.flowable(store)
                        .subscribe {
                            count1++
                        }

                KduxRx.flowable(store)
                        .subscribe {
                            count2++
                        }

                store.dispatch(Action("Hello"))
                store.dispatch(Action("world"))
                store.dispatch(Action("!"))

                count1 shouldBe 3
                count2 shouldBe 3
            }

            "unsubscribe on dispose" {
                var count1 = 0
                var count2 = 0

                val subscription1 = KduxRx.flowable(store)
                        .subscribe {
                            count1++
                        }

                val subscription2 = KduxRx.flowable(store)
                        .subscribe {
                            count2++
                        }

                store.dispatch(Action("Hello"))

                subscription1.dispose()

                store.dispatch(Action("world"))

                subscription2.dispose()

                store.dispatch(Action("!"))

                count1 shouldBe 1
                count2 shouldBe 2
            }

            "receive current state" {
                Completable.complete()
                        .delay(1, TimeUnit.SECONDS)
                        .subscribe {
                            store.dispatch(Action("Hello world"))
                        }

                val resultStore = KduxRx.flowable(store)
                        .blockingFirst()

                resultStore.state?.value shouldBe "Hello world"
            }
        }
    }
}
