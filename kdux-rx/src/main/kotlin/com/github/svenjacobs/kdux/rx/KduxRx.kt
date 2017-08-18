package com.github.svenjacobs.kdux.rx

import com.github.svenjacobs.kdux.Store
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable

object KduxRx {

    /**
     * Subscribes to given [Store] and wraps the subscription in a [Flowable] reactive stream.
     *
     * Make sure to dispose the subscription via [Disposable.dispose] when not needed anymore.
     */
    @JvmStatic
    fun <State, Action> flowable(store: Store<State, Action>): Flowable<Store<State, Action>> =
            Flowable.create<Store<State, Action>>({ subscriber ->
                                                      val subscription = store.subscribe {
                                                          subscriber.onNext(store)
                                                      }

                                                      var isDisposed = false
                                                      subscriber.setDisposable(object : Disposable {
                                                          override fun isDisposed() = isDisposed

                                                          override fun dispose() {
                                                              if (isDisposed) return
                                                              isDisposed = true
                                                              subscription.unsubscribe()
                                                          }
                                                      })
                                                  }, BackpressureStrategy.MISSING)
}
