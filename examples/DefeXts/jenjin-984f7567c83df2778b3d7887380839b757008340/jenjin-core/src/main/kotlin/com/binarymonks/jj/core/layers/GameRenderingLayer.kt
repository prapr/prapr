package com.binarymonks.jj.core.layers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Sort
import com.binarymonks.jj.core.GameViewConfig
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.input.TouchManager
import com.binarymonks.jj.core.pools.new
import com.binarymonks.jj.core.pools.recycle
import com.binarymonks.jj.core.render.nodes.RenderNode
import com.binarymonks.jj.core.scenes.Scene
import com.binarymonks.jj.core.specs.render.RenderGraphType

class GameRenderingLayer(
        var worldBoxWidth: Float,
        var posX: Float,
        var posY: Float
) : LayerAbs() {

    var camera: OrthographicCamera = OrthographicCamera()

    internal var drenderer = Box2DDebugRenderer()
    internal var touchManager = TouchManager(camera)
    val sceneComparator = SceneComporator()

    init {
        // Constructs a new OrthographicCamera, using the given viewport width and height
        // Height is multiplied by aspect ratio.
        setView(worldBoxWidth, posX, posY)
        inputMultiplexer.addProcessor(touchManager)
    }

    constructor(viewConfig: GameViewConfig) : this(
            viewConfig.worldBoxWidth,
            viewConfig.cameraPosX,
            viewConfig.cameraPosY)


    override fun update() {
        camera.update()
        updateScreenToWorldScale()
        touchManager.update()
        renderGraph(RenderGraphType.DEFAULT)
        renderLights()
        renderGraph(RenderGraphType.LIGHT)
        if (JJ.B.config.b2d.debugRender) {
            drenderer.render(JJ.B.physicsWorld.b2dworld, JJ.B.renderWorld.polyBatch.getProjectionMatrix())
        }
    }

    private fun updateScreenToWorldScale() {
        val worldDistance = 1000f
        val worldLeft = new(Vector3::class).set(0f, 0f, 0f)
        val worldRight = new(Vector3::class).set(worldDistance, 0f, 0f)
        val screenLeft = camera.project(worldLeft)
        val screenRight = camera.project(worldRight)
        JJ.B.renderWorld.worldToScreenScale = (screenRight.x - screenLeft.x) / worldDistance
        recycle(worldLeft, worldRight)
    }

    private fun renderGraph(type: RenderGraphType) {
        if (JJ.B.sceneWorld.rootScene != null) {
            val layers = JJ.B.sceneWorld.rootScene!!.sceneLayers
            JJ.B.renderWorld.polyBatch.enableBlending()
            JJ.B.renderWorld.polyBatch.projectionMatrix = camera.combined
            JJ.B.renderWorld.shapeRenderer.projectionMatrix = camera.combined

            JJ.B.renderWorld.polyBatch.begin()
            var renderedCount = 0
            var layerIndex = 0
            while (renderedCount < layers.size) {
                if (layers.containsKey(layerIndex)) {
                    renderedCount++
                    val scenes = layers.get(layerIndex)
                    Sort.instance().sort(scenes, sceneComparator)
                    scenes.forEach {
                        renderScene(it, type)
                    }
                }
                layerIndex++
            }
            JJ.B.renderWorld.end()
        }
    }

    private fun renderScene(scene: Scene, type: RenderGraphType) {
        when (type) {
            RenderGraphType.DEFAULT -> updateSceneNodes(scene.renderRoot.defaultRenderNodes)
            RenderGraphType.LIGHT -> updateSceneNodes(scene.renderRoot.lightRenderNodes)
        }
        val layers = scene.sceneLayers
        var renderedCount = 0
        var layerIndex = 0
        while (renderedCount < layers.size) {
            if (layers.containsKey(layerIndex)) {
                renderedCount++
                val scenes = layers.get(layerIndex)
                Sort.instance().sort(scenes, sceneComparator)
                scenes.forEach {
                    renderScene(it, type)
                }
            }
            layerIndex++
        }
    }

    private fun renderLights() {
        JJ.B.renderWorld.rayHandler.setCombinedMatrix(camera.combined, camera.position.x, camera.position.y, camera.viewportWidth, camera.viewportHeight)
        JJ.B.renderWorld.rayHandler.updateAndRender()
    }

    private fun updateSceneNodes(renderNodes: Array<RenderNode>) {
        for (node in renderNodes) {
            node.renderShell(camera)
        }
    }

    fun setView(worldWidth: Float, cameraX: Float, cameraY: Float) {
        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()
        camera.viewportWidth = worldWidth
        camera.viewportHeight = worldWidth * (screenHeight / screenWidth)
        camera.position.set(cameraX, cameraY, 0f)
        camera.update()
    }

    override fun resize(width: Int, height: Int) {
        val newWidth = Gdx.graphics.width.toFloat()
        val newHeight = Gdx.graphics.height.toFloat()
        camera.viewportHeight = camera.viewportWidth * (newHeight / newWidth)
        camera.update()
    }
}

class SceneComporator : Comparator<Scene> {
    override fun compare(o1: Scene?, o2: Scene?): Int {
        return o1!!.specID - o2!!.specID
    }

}
