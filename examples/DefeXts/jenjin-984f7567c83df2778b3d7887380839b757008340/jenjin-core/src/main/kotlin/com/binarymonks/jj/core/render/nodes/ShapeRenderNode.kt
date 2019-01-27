package com.binarymonks.jj.core.render.nodes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Transform
import com.badlogic.gdx.utils.Array
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.pools.mat3
import com.binarymonks.jj.core.pools.recycle
import com.binarymonks.jj.core.pools.recycleItems
import com.binarymonks.jj.core.pools.vec2
import com.binarymonks.jj.core.properties.PropOverride
import com.binarymonks.jj.core.render.ShaderSpec
import com.binarymonks.jj.core.specs.render.RenderGraphType


abstract class ShapeRenderNode(
        priority: Int,
        color: PropOverride<Color>,
        renderGraphType: RenderGraphType,
        name: String?,
        shaderSpec: ShaderSpec?,
        var fill: Boolean = true
) : RenderNode(priority, color, renderGraphType, name, shaderSpec) {

    override fun render(camera: OrthographicCamera) {
        JJ.B.renderWorld.switchToShapes(fill)
        JJ.B.renderWorld.shapeRenderer.color = color.get()
        drawShape(camera)
    }

    override fun setShaderAndColor() {
        if (shaderSpec != null) {
            // TODO: Should centralize this and check if the current shader is the same or not
            JJ.B.renderWorld.shapeRenderer.end()
            JJ.B.renderWorld.shapeRenderer.begin(if (fill) ShapeRenderer.ShapeType.Filled else ShapeRenderer.ShapeType.Line)
            (JJ.B.renderWorld.shapeRenderer.renderer as ImmediateModeRenderer20).setShader(JJ.B.renderWorld.getShaderPipe(shaderSpec!!.shaderPipe))
        }
    }

    override fun restoreShaderAndColor() {
        if (shaderSpec != null) {
            // TODO: Should centralize this and check if the current shader is the same or not
            JJ.B.renderWorld.shapeRenderer.end()
            JJ.B.renderWorld.shapeRenderer.begin(if (fill) ShapeRenderer.ShapeType.Filled else ShapeRenderer.ShapeType.Line)
            (JJ.B.renderWorld.shapeRenderer.renderer as ImmediateModeRenderer20).setShader(JJ.B.renderWorld.defaultShapeShader)
        }
    }

    abstract fun drawShape(camera: OrthographicCamera)
}

class CircleRenderNode(
        priority: Int,
        color: PropOverride<Color>,
        renderGraphType: RenderGraphType,
        name: String?,
        shaderSpec: ShaderSpec?,
        fill: Boolean = true,
        val offsetX: Float,
        val offsetY: Float,
        val radius: Float,
        var segments: Int = 360
) : ShapeRenderNode(priority, color, renderGraphType, name, shaderSpec, fill) {

    private var positionCache: Vector2 = vec2()

    override fun drawShape(camera: OrthographicCamera) {
        val transform: Transform = me().physicsRoot.transform
        positionCache.set(offsetX, offsetY)
        transform.mul(positionCache)
        JJ.B.renderWorld.shapeRenderer.circle(positionCache.x, positionCache.y, radius, segments)
    }

    override fun dispose() {
    }
}

class LineChainRenderNode(
        priority: Int,
        color: PropOverride<Color>,
        renderGraphType: RenderGraphType,
        name: String?,
        shaderSpec: ShaderSpec?,
        fill: Boolean = false,
        internal var scaleX: Float = 1f,
        internal var scaleY: Float = 1f,
        internal var offsetX: Float = 0f,
        internal var offsetY: Float = 0f,
        internal var rotationD: Float = 0f,
        val vertices: Array<Vector2>
) : ShapeRenderNode(priority, color, renderGraphType, name, shaderSpec, fill) {

    private var vertCache: Array<Vector2> = Array()

    override fun drawShape(camera: OrthographicCamera) {
        val transform: Transform = me().physicsRoot.transform
        val localTransform = mat3()
        localTransform.scale(scaleX, scaleY)
        localTransform.translate(offsetX, offsetY)
        localTransform.rotate(rotationD)
        clearCache()
        vertices.forEach {
            val transformed = vec2().set(it)
            transform.mul(transformed)
            transformed.mul(localTransform)
            vertCache.add(transformed)
        }
        for (i in 1..vertCache.size - 1) {
            JJ.B.renderWorld.shapeRenderer.line(vertCache.get(i - 1), vertCache.get(i))
        }
        recycle(localTransform)
    }

    override fun dispose() {
    }

    private fun clearCache() {
        recycleItems(vertCache)
        vertCache.clear()
    }
}