package com.binarymonks.jj.core.time

import com.badlogic.gdx.Gdx
import com.binarymonks.jj.core.api.ClockAPI

class ClockControls : ClockAPI {

    companion object {
        private var fixedDelta = (1f / 60).toDouble()
        private var fixedDeltaFloat = 1f / 60
        private var realToGameRatio = 1.0f
        private var DELTA = (1f / 60).toDouble()
        private var DELTA_FLOAT = 1f / 60
        private var TIME = 0.0
        private var timeFunction = TimeFunction.RATIO_TIME
    }

    val scheduler: Scheduler = Scheduler(this::timeFloat)

    override fun schedule(function: () -> Unit, delaySeconds: Float, repeat: Int, name: String?): Int {
        return scheduler.schedule(function, delaySeconds, repeat, name)
    }

    override fun schedule(function: () -> Unit, delayMinSeconds: Float, delayMaxSeconds: Float, repeat: Int, name: String?): Int {
        return scheduler.schedule(function, delayMinSeconds, delayMaxSeconds, repeat, name)
    }

    override fun cancel(id: Int) {
        scheduler.cancel(id)
    }

    override val delta: Double
        get() = DELTA

    override val deltaFloat: Float
        get() = DELTA_FLOAT

    override val time: Double
        get() = TIME

    override val timeFloat: Float
        get() = TIME.toFloat()

    fun update() {
        timeFunction.update(Gdx.graphics.deltaTime)
        TIME += DELTA
        scheduler.update()
    }

    fun setTimeFunction(function: TimeFunction) {
        timeFunction = function
    }

    fun getFixedDelta(): Double {
        return fixedDelta
    }

    val ratioFixedDelta: Double
        get() = fixedDelta * realToGameRatio

    fun setFixedDelta(fixedTime: Double) {
        ClockControls.fixedDelta = fixedTime
        ClockControls.fixedDeltaFloat = fixedTime.toFloat()
    }

    fun getRealToGameRatioDouble(): Double {
        return realToGameRatio.toDouble()
    }

    fun getRealToGameRatio(): Float {
        return realToGameRatio
    }

    fun setRealToGameRatio(realToGameRatio: Float) {
        ClockControls.realToGameRatio = realToGameRatio
    }

    fun getTimeFunction(): TimeFunction {
        return timeFunction
    }

    enum class TimeFunction {
        REAL_TIME {
            override fun update(realDelta: Float) {
                DELTA = realDelta.toDouble()
                DELTA_FLOAT = realDelta
            }
        },
        FIXED_TIME {
            override fun update(realDelta: Float) {
                DELTA = fixedDelta
                DELTA_FLOAT = fixedDeltaFloat
            }

        },
        RATIO_TIME {
            override fun update(realDelta: Float) {
                DELTA = (realToGameRatio * realDelta).toDouble()
                DELTA_FLOAT = realToGameRatio * realDelta
            }

        };

        abstract fun update(realDelta: Float)
    }
}
