package com.binarymonks.jj.core.render.nodes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.properties.PropOverride
import com.binarymonks.jj.core.render.ShaderSpec
import com.binarymonks.jj.core.specs.render.RenderGraphType


class ParticleRenderNode(
        priority: Int,
        color: PropOverride<Color>,
        renderGraphType: RenderGraphType,
        name: String?,
        shaderSpec: ShaderSpec?,
        internal var particleEffect: ParticleEffect,
        internal var offsetX: Float,
        internal var offsetY: Float,
        internal var scale: Float,
        internal var rotationD: Float
) : BatchBasedRenderNode(priority, color, renderGraphType, name, shaderSpec) {

    private var started = false
    private var emitterSnapshots: ObjectMap<String, EmitterAngles> = ObjectMap()


    init {
        particleEffect.emitters.forEach {
            val angle = it.angle
            emitterSnapshots.put(it.name, EmitterAngles(
                    angle.highMin,
                    angle.highMax,
                    angle.lowMin,
                    angle.lowMax
            ))
        }
    }


    override fun render(camera: OrthographicCamera) {
        JJ.B.renderWorld.switchToBatch()
        updateParticle()
        particleEffect.draw(JJ.B.renderWorld.polyBatch)
    }

    private fun updateParticle() {
        if (!started) {
            started = true
            particleEffect.start()
            particleEffect.scaleEffect(scale)
        }
        val bodyRotation=me().physicsRoot.rotationR() * MathUtils.radDeg
        val combinedRotation = bodyRotation+rotationD
        emitterSnapshots.forEach {
            val emitter = particleEffect.findEmitter(it.key)
            val snapshot = it.value
            emitter.angle.setHigh(snapshot.highMin+combinedRotation, snapshot.highMax+combinedRotation)
            emitter.angle.setLow(snapshot.lowMin+combinedRotation, snapshot.lowMax+combinedRotation)
        }
        val position = me().physicsRoot.position().add(offsetX, offsetY)
        particleEffect.setPosition(position.x, position.y)

        particleEffect.update(JJ.clock.deltaFloat)
    }

    override fun dispose() {
        particleEffect.dispose()
    }
}

class EmitterAngles(
        val highMin: Float,
        val highMax: Float,
        val lowMin: Float,
        val lowMax: Float
)