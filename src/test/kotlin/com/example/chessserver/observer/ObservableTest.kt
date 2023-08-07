package com.example.chessserver.observer

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

internal class ObservableTest {
    private class TestObservable : Observable<Int>() {
        fun testNotify() {
            this.notify(1)
        }
    }

    private val observable: TestObservable = TestObservable()
    private val observer1: (data: Int) -> Unit = mock()
    val observer2: (data: Int) -> Unit = mock()


    @Test
    fun `should call observers on notify`() {
        observable.subscribe(observer1)
        observable.subscribe(observer2)
        observable.testNotify()
        verify(observer1).invoke(1)
        verify(observer2).invoke(1)
    }

}