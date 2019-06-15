package com.binarymonks.jj.core.scenes

import com.binarymonks.jj.core.events.EventBus
import com.binarymonks.jj.core.events.ParamSubscriberMock
import com.binarymonks.jj.core.events.SubscriberMock
import com.binarymonks.jj.core.mockoutGDXinJJ
import com.binarymonks.jj.core.testScene
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class SceneEventBusTest {

    internal var message = "Arbitrary Message"
    lateinit var scene1: Scene
    lateinit var scene2: Scene
    lateinit var scene1SubscriberMock: SubscriberMock
    lateinit var scene2HandlerMock: ParamSubscriberMock

    @Before
    fun setup() {
        mockoutGDXinJJ()
        scene1 = testScene()
        scene2 = testScene()
        scene1.add(scene2)

        scene1SubscriberMock = SubscriberMock()
        scene2HandlerMock = ParamSubscriberMock()

        scene1.register(message, scene1SubscriberMock::call)
        scene2.register(message, scene2HandlerMock::call)
    }

    @Test
    fun broadcastNoPropagation() {
        scene1.broadcast(message, propagate = false)

        Assert.assertTrue(scene1SubscriberMock.called)
        Assert.assertNull(scene2HandlerMock.params)
    }

    @Test
    fun broadcastPropagation() {
        scene1.broadcast(message, propagate = true)

        Assert.assertTrue(scene1SubscriberMock.called)
        Assert.assertEquals(EventBus.NULLPARAMS, scene2HandlerMock.params)
    }

    @Test
    fun deregister(){
        val mockSubscriber = SubscriberMock()
        val id = scene1.register(message, mockSubscriber::call)
        scene1.deregister(message, id)

        scene1.broadcast(message, propagate = false)

        Assert.assertFalse(mockSubscriber.called)
    }

}