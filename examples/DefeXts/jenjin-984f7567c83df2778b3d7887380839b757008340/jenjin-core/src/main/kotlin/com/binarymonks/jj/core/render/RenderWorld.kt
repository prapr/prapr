package com.binarymonks.jj.core.render

import box2dLight.RayHandler
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.api.RenderAPI

open class RenderWorld : RenderAPI {

    val defaultShapeShader: ShaderProgram = ImmediateModeRenderer20.createDefaultShader(false, true, 0)
    var shapeRenderer = ShapeRenderer(5000, defaultShapeShader)
    var polyBatch = PolygonSpriteBatch()
    var rayHandler: RayHandler = RayHandler(JJ.B.physicsWorld.b2dworld)
    var worldToScreenScale: Float = 0.toFloat()
    private var currentShapeFill = false
    private var batchStoredColor = Color.WHITE
    private var shaderPrograms: ObjectMap<String, ShaderProgram> = ObjectMap()

    init {
        rayHandler.setBlurNum(3)
        rayHandler.setAmbientLight(0.0f, 0.0f, 0.0f, 1.0f)
    }

    override fun setAmbientLight(r: Float, g: Float, b: Float, a: Float) {
        rayHandler.setAmbientLight(r, g, b, a)
    }

    override fun setClearColor(r: Float, g: Float, b: Float, a: Float) {
        JJ.B.layers.clearColor.set(r, g, b, a)
    }

    override fun registerShader(name: String, vertexPath: String, fragmentPath: String) {
        val vertex = Gdx.files.internal(vertexPath).readString()
        val fragment = Gdx.files.internal(fragmentPath).readString()
        val program = ShaderProgram(vertex, fragment)
        if (!program.isCompiled()) {
            throw Exception(program.getLog())
        }
        shaderPrograms.put(name, program)
    }

    override fun getShaderPipe(shaderPipeName: String): ShaderProgram {
        return shaderPrograms.get(shaderPipeName)
    }

    fun switchToShapes(fill: Boolean) {
        if (!shapeRenderer.isDrawing) {
            polyBatch.end()
            shapeRenderer.begin(if (fill) ShapeRenderer.ShapeType.Filled else ShapeRenderer.ShapeType.Line)
            currentShapeFill = fill
        } else if (fill != currentShapeFill) {
            currentShapeFill = fill
            shapeRenderer.end()
            shapeRenderer.begin(if (fill) ShapeRenderer.ShapeType.Filled else ShapeRenderer.ShapeType.Line)
        }
    }

    fun switchToBatch() {
        if (!polyBatch.isDrawing) {
            shapeRenderer.end()
            polyBatch.begin()
        }
    }

    fun switchBatchColorTo(color: Color) {
        batchStoredColor = polyBatch.color
        polyBatch.color = color
    }

    fun switchBatchColorBack() {
        polyBatch.color = batchStoredColor
    }

    fun end() {
        if (polyBatch.isDrawing) {
            polyBatch.end()
        } else {
            shapeRenderer.end()
        }
    }

}