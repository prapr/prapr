package com.binarymonks.jj.core.render.nodes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.binarymonks.jj.core.properties.PropOverride
import com.binarymonks.jj.core.render.ShaderSpec
import com.binarymonks.jj.core.specs.render.RenderGraphType
import com.binarymonks.jj.core.scenes.Scene


abstract class RenderNode(
        var priority: Int = 0,
        var color: PropOverride<Color>,
        var renderGraphType: RenderGraphType,
        var name: String?,
        var shaderSpec: ShaderSpec?
) {
    var parent: Scene? = null
        set(value) {
            field = value
            color.hasProps = value
        }
    var active = true

    fun me(): Scene {
        if (parent == null) {
            throw Exception("scene has not been set")
        }
        return parent!!
    }

    fun renderShell(camera: OrthographicCamera) {
        if (active) {
            setShaderAndColor()
            render(camera)
            restoreShaderAndColor()
        }
    }


    abstract fun setShaderAndColor()

    abstract fun restoreShaderAndColor()


    abstract fun render(camera: OrthographicCamera)

    abstract fun dispose()
}