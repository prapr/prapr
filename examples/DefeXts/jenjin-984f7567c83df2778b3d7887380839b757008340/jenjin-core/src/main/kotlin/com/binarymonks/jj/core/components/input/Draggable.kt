package com.binarymonks.jj.core.components.input

import com.binarymonks.jj.core.pools.recycle
import com.binarymonks.jj.core.pools.vec2


class Draggable : TouchHandler() {

    var velocityScale: Float = 50f
    private var touched = false

    override fun onTouchDown(touchX: Float, touchY: Float, button: Int): Boolean {
        if (!touched) {
            touched = true
            return true
        }
        return false
    }

    override fun onTouchMove(touchX: Float, touchY: Float, button: Int) {
        val currentPosition = me().physicsRoot.position()
        val direction = vec2(touchX, touchY).sub(currentPosition)
        me().physicsRoot.b2DBody.linearVelocity = direction.scl(velocityScale)
        recycle(direction)
    }

    override fun onTouchUp(touchX: Float, touchY: Float, button: Int) {
        me().physicsRoot.b2DBody.setLinearVelocity(0f, 0f)
    }

    override fun onAddToWorld() {
        touched = false
    }
}





