package com.binarymonks.jj.core.specs.render

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.pools.vec2


class RenderSpec {
    var id = JJ.B.nextID()
    var renderNodes: Array<RenderNodeSpec> = Array()

    fun addNode(renderNode: RenderNodeSpec) {
        this.renderNodes.add(renderNode)
    }

    fun imageTexture(assetpath: String, init: (TextureNodeSpec.() -> Unit)? = null) {
        val imageSpec: TextureNodeSpec = TextureNodeSpec()
        imageSpec.assetPath = assetpath
        if (init != null) {
            imageSpec.init()
        }
        addNode(imageSpec)
    }

    fun particles(assetpath: String, init: (ParticleNodeSpec.() -> Unit)? = null) {
        val particleNodeSpec: ParticleNodeSpec = ParticleNodeSpec()
        particleNodeSpec.effectPath = assetpath
        if (init != null) {
            particleNodeSpec.init()
        }
        addNode(particleNodeSpec)
    }

    fun polygonRender(vertices: Array<Vector2>, init: (PolygonRenderNodeSpec.() -> Unit)? = null) {
        val shapeSpec: PolygonRenderNodeSpec = PolygonRenderNodeSpec()
        vertices.forEach { shapeSpec.vertices.add(it) }
        if (init != null) {
            shapeSpec.init()
        }
        addNode(shapeSpec)
    }

    fun polygonRender(vararg vertices: Vector2, init: (PolygonRenderNodeSpec.() -> Unit)? = null) {
        val shapeSpec: PolygonRenderNodeSpec = PolygonRenderNodeSpec()
        vertices.forEach { shapeSpec.vertices.add(it) }
        if (init != null) {
            shapeSpec.init()
        }
        addNode(shapeSpec)
    }

    fun lineChainRender(vertices: Array<Vector2>, init: (LineChainRenderNodeSpec.() -> Unit)? = null) {
        val shapeSpec: LineChainRenderNodeSpec = LineChainRenderNodeSpec()
        vertices.forEach { shapeSpec.vertices.add(it) }
        if (init != null) {
            shapeSpec.init()
        }
        addNode(shapeSpec)
    }

    fun lineChainRender(vararg vertices: Vector2, init: (LineChainRenderNodeSpec.() -> Unit)? = null) {
        val shapeSpec: LineChainRenderNodeSpec = LineChainRenderNodeSpec()
        vertices.forEach { shapeSpec.vertices.add(it) }
        if (init != null) {
            shapeSpec.init()
        }
        addNode(shapeSpec)
    }

    fun rectangleRender(width: Float, height: Float, init: (PolygonRenderNodeSpec.() -> Unit)? = null) {
        val shapeSpec: PolygonRenderNodeSpec = PolygonRenderNodeSpec()
        shapeSpec.vertices.add(vec2(-width / 2, height / 2))
        shapeSpec.vertices.add(vec2(width / 2, height / 2))
        shapeSpec.vertices.add(vec2(width / 2, -height / 2))
        shapeSpec.vertices.add(vec2(-width / 2, -height / 2))
        if (init != null) {
            shapeSpec.init()
        }
        addNode(shapeSpec)
    }


    fun circleRender(radius: Float, init: (CircleRenderNodeSpec.() -> Unit)? = null) {
        val circleRender = CircleRenderNodeSpec()
        circleRender.radius = radius
        if (init != null) {
            circleRender.init()
        }
        addNode(circleRender)
    }
}