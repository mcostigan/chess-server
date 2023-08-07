package com.example.chessserver.observer

open class Observable<T> {
    private var observers: MutableCollection<(data: T) -> Unit> = mutableListOf()

    fun subscribe(observer: (data: T) -> Unit) {
        this.observers.add(observer)
    }

    protected fun notify(data: T) {
        this.observers.forEach {
            it.invoke(data)
        }
    }
}