package com.github.svenjacobs.kdux

import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.contain
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.WordSpec

class ReducerTest : WordSpec() {

    init {

        val store = Kdux.createStore(reducer, State())
        val user = User(firstName = "Sven", lastName = "Jacobs")

        "Message" should {

            "update message" {
                store.state!!.message shouldBe null
                store.dispatch(Action.Message("Hello world!"))
                store.state!!.message shouldBe "Hello world!"
            }
        }

        "AddUser" should {

            "add user to state" {
                store.state!!.users should beEmpty()
                store.dispatch(Action.AddUser(user))
                store.state!!.users should contain(user)
            }
        }

        "RemoveUser" should {

            "remove user from state" {
                val removeStore = Kdux.createStore(reducer, State(users = listOf(user)))

                removeStore.state!!.users should contain(user)
                removeStore.dispatch(Action.RemoveUser(user))
                removeStore.state!!.users should beEmpty()
            }
        }
    }
}
