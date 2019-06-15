package com.binarymonks.jj.core.render

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Transform
import com.badlogic.gdx.utils.ObjectMap


class ShaderSpec {
    var shaderPipe: String = ""
    internal var floatBindings: ObjectMap<String, Float> = ObjectMap()

    constructor(shaderPipe: String, build: (ShaderSpec.() -> Unit)? = null) {
        if (build != null)
            build()
        this.shaderPipe = shaderPipe
    }

    fun floatBinding(name: String, value: Float) {
        floatBindings.put(name, value)
    }

    internal fun bind(shader: ShaderProgram) {
        for (entry in floatBindings) {
            shader.setUniformf(entry.key, entry.value)
        }
    }
}