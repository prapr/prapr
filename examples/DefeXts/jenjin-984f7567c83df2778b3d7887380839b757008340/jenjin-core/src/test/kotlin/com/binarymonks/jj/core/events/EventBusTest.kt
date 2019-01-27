package com.binarymonks.jj.core.events

import com.badlogic.gdx.utils.ObjectMap
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class SubscriberMock {
    var called = false

    fun call() {
        called = true
    }
}

class ParamSubscriberMock {
    var params: ObjectMap<String, Any>? = null

    fun call(params: ObjectMap<String, Any>) {
        this.params = params
    }
}


class EventBusTest {

    internal var message = "Arbitrary Message"
    internal var emptyParams = ObjectMap<String, Any>()
    lateinit var testObj: EventBus

    @Before
    fun setup() {
        this.testObj = EventBus()
    }

    @Test
    fun sendNoParams() {

        val subscriberMock = SubscriberMock()
        val paramSubscriberMock = ParamSubscriberMock()

        testObj.register(message, subscriberMock::call)
        testObj.register(message, paramSubscriberMock::call)

        testObj.send(message)

        Assert.assertTrue(subscriberMock.called)
        Assert.assertEquals(emptyParams, paramSubscriberMock.params)
    }

    @Test
    fun sendParams() {
        val params = ObjectMap<String, Any>()
        params.put("Arbitrary", "Value")
        val subscriberMock = SubscriberMock()
        val paramSubscriberMock = ParamSubscriberMock()

        testObj.register(message, subscriberMock::call)
        testObj.register(message, paramSubscriberMock::call)

        testObj.send(message, params)

        Assert.assertTrue(subscriberMock.called)
        Assert.assertEquals(params, paramSubscriberMock.params)
    }

    @Test
    fun deregister(){

        val subscriberMock = SubscriberMock()
        val paramSubscriberMock = ParamSubscriberMock()

        val funcID = testObj.register(message, subscriberMock::call)
        val handlerID = testObj.register(message, paramSubscriberMock::call)

        testObj.deregister(message, funcID)
        testObj.deregister(message, handlerID)

        testObj.send(message)

        Assert.assertFalse(subscriberMock.called)
        Assert.assertNull(paramSubscriberMock.params)

    }

    @Test
    fun clear(){
        val subscriberMock = SubscriberMock()
        val paramSubscriberMock = ParamSubscriberMock()

        testObj.register(message, subscriberMock::call)
        testObj.register(message, paramSubscriberMock::call)

        testObj.clear()

        testObj.send(message)

        Assert.assertFalse(subscriberMock.called)
        Assert.assertNull(paramSubscriberMock.params)
    }

}