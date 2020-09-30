package ar.gob.coronavirus.utils.observables

class Event<T>(private val content: T) {
    private var wasConsumed = false

    fun getOrNull(): T? {
        return if (wasConsumed) {
            null
        } else {
            wasConsumed = true
            content
        }
    }

    fun get(): T {
        return content
    }
}