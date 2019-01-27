package com.binarymonks.jj.core.render.nodes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.properties.PropOverride
import com.binarymonks.jj.core.render.ShaderSpec
import com.binarymonks.jj.core.specs.render.RenderGraphType


abstract class BatchBasedRenderNode(
        priority: Int,
        color: PropOverride<Color>,
        renderGraphType: RenderGraphType,
        name: String?,
        shaderSpec: ShaderSpec?
) : RenderNode(
        priority,
        color,
        renderGraphType,
        name,
        shaderSpec) {

    internal var batchColor: Color? = null

    override fun setShaderAndColor() {
        if (shaderSpec != null) {
            val shader = JJ.render.getShaderPipe(shaderSpec!!.shaderPipe)
            JJ.B.renderWorld.polyBatch.shader = shader
            shaderSpec!!.bind(shader)
        }
        batchColor = JJ.B.renderWorld.polyBatch.color
        JJ.B.renderWorld.polyBatch.color = color.get()
    }

    override fun restoreShaderAndColor() {
        if (shaderSpec != null) {
            JJ.B.renderWorld.polyBatch.shader = null
        }
        JJ.B.renderWorld.polyBatch.color = batchColor
    }

}