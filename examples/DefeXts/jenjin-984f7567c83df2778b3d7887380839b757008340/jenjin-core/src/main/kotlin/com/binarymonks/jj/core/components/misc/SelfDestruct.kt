package com.binarymonks.jj.core.components.misc

import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.components.Component


class SelfDestruct(
        var delaySeconds: Float = 1f
) : Component() {

    internal var scheduleID = -1
    override fun onAddToWorld() {
        scheduleID = JJ.clock.schedule(this::destroy, delaySeconds)
    }

    override fun onRemoveFromWorld() {
        JJ.clock.cancel(scheduleID)
    }

    fun destroy() {
        me().destroy()
    }
}