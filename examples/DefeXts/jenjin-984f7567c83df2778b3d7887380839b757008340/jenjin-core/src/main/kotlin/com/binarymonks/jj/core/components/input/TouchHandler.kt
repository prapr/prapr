package com.binarymonks.jj.core.components.input

import com.binarymonks.jj.core.components.Component
import kotlin.reflect.KClass


/**
 * Extend this to add touch handling to your [com.binarymonks.jj.core.scenes.Scene]
 */
abstract class TouchHandler : Component() {

    /**
     * If true - the touch coordinates will be passed with the offset of the touch
     * in local space. ie. if you move the body to this position the body will retain its
     * position relative to the local body coordinates of the touch
     */
    var relativeToTouch = true

    /**
     * Return true if the touch event is handled and should not be propagated further,
     * false otherwise.
     *
     * If false [onTouchMove] and [onTouchUp] will not be called.
     */
    abstract fun onTouchDown(touchX: Float, touchY: Float, button: Int): Boolean

    abstract fun onTouchMove(touchX: Float, touchY: Float, button: Int)

    abstract fun onTouchUp(touchX: Float, touchY: Float, button: Int)

    override fun type(): KClass<Component> {
        @Suppress("unchecked_cast")
        return TouchHandler::class as KClass<Component>
    }
}