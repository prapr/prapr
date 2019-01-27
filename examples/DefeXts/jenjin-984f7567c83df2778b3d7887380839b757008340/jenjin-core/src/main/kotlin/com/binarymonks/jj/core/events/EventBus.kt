package com.binarymonks.jj.core.events

import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.api.EventsAPI

typealias Subscriber = () -> Unit
typealias ParamSubscriber = (params: ObjectMap<String, Any>) -> Unit


class EventBus : EventsAPI {

    private var listeners = ObjectMap<String, Listeners>()
    private var idCounter = 0

    private fun nextID(): Int {
        idCounter++
        return idCounter
    }

    override fun register(channel: String, function: Subscriber): Int {
        if (!listeners.containsKey(channel)) {
            listeners.put(channel, Listeners())
        }
        val id = nextID()
        listeners.get(channel).funcListeners.put(id, function)
        return id
    }

    override fun register(channel: String, eventHandler: ParamSubscriber): Int {
        if (!listeners.containsKey(channel)) {
            listeners.put(channel, Listeners())
        }
        val id = nextID()
        listeners.get(channel).handlers.put(id, eventHandler)
        return id
    }

    override fun send(channel: String, eventParams: ObjectMap<String, Any>) {
        if (listeners.containsKey(channel)) {
            for (func in listeners.get(channel).funcListeners.values()) {
                func()
            }
            for (handler in listeners.get(channel).handlers.values()) {
                handler(eventParams)
            }
        }
    }

    override fun deregister(channel: String, registerID: Int) {
        if (listeners.containsKey(channel)) {
            listeners[channel].funcListeners.remove(registerID)
            listeners[channel].handlers.remove(registerID)
        }
    }

    fun clear() {
        listeners.forEach { it.value.funcListeners.clear(); it.value.handlers.clear() }
    }

    class Listeners {
        internal var funcListeners = ObjectMap<Int, Subscriber>()
        internal var handlers = ObjectMap<Int, ParamSubscriber>()
    }

    companion object {

        internal var NULLPARAMS = ObjectMap<String, Any>()
    }

}
