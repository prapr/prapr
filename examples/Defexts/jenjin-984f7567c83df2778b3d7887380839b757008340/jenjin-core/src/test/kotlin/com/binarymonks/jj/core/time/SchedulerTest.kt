package com.binarymonks.jj.core.time

import com.binarymonks.jj.core.mockoutGDXinJJ
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class TimeFunctionMock(var time: Float) {

    fun returnTime(): Float {
        return time
    }
}

class FunctionMock() {
    var counter = 0

    fun call() {
        counter++
    }

    fun assertCalls(calls: Int) {
        Assert.assertEquals(calls, counter)
    }
}

class SelfCancelingFunctionMock() {
    var schedular: Scheduler? = null
    var scheduleId = -1
    var counter = 0

    fun call() {
        counter++
        schedular!!.cancel(scheduleId)
    }

    fun assertCalls(calls: Int) {
        Assert.assertEquals(calls, counter)
    }

}

class SchedulerTest {

    val timeMock = TimeFunctionMock(0f)
    var testObj = Scheduler(timeMock::returnTime)

    @Before
    fun setup() {
        timeMock.time = 0f
        testObj = Scheduler(timeMock::returnTime)
        mockoutGDXinJJ()
    }


    @Test
    fun schedule_No_Repeat() {
        val functionMock = FunctionMock()
        val delay = 1f

        testObj.schedule(functionMock::call, delay)

        tick(0.5f)

        functionMock.assertCalls(0)

        tick(0.51f)

        functionMock.assertCalls(1)

        tick(1.5f)

        functionMock.assertCalls(1)

    }

    @Test
    fun schedule_Repeat() {
        val functionMock = FunctionMock()
        val delay = 1f

        testObj.schedule(functionMock::call, delay, 2)

        tick(0.5f)

        functionMock.assertCalls(0)

        tick(0.5f)

        functionMock.assertCalls(1)

        tick(1f)

        functionMock.assertCalls(2)

        tick(1f)

        functionMock.assertCalls(2)
    }

    @Test
    fun schedule_Infinite_Zero() {
        val functionMock = FunctionMock()
        val delay = 1f

        testObj.schedule(functionMock::call, delay, 0)

        tick(0.5f)

        functionMock.assertCalls(0)

        tick(0.5f)

        functionMock.assertCalls(1)

        tick(1f)

        functionMock.assertCalls(2)

        tick(1f)

        functionMock.assertCalls(3)
    }

    @Test
    fun schedule_Cancel() {
        val functionMock = FunctionMock()
        val delay = 1f

        val id = testObj.schedule(functionMock::call, delay)

        tick(0.5f)

        functionMock.assertCalls(0)

        testObj.cancel(id)

        tick(0.5f)

        functionMock.assertCalls(0)

        tick(1.5f)

        functionMock.assertCalls(0)

    }

    @Test
    fun schedule_canceled_from_inside_scheduled_function() {
        val functionMock = SelfCancelingFunctionMock()
        functionMock.schedular = testObj
        val delay = 1f

        functionMock.scheduleId = testObj.schedule(functionMock::call, delay)

        tick(1f)
        testObj.update()

        functionMock.assertCalls(1)


        tick(1f)
        testObj.update()

        functionMock.assertCalls(1)
    }


    private fun tick(delta: Float) {
        timeMock.time += delta
        testObj.update()
        testObj.update()
    }
}


