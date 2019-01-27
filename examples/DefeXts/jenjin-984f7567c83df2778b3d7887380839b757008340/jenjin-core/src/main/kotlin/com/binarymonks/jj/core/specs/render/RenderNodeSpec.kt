package com.binarymonks.jj.core.specs.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.PolygonSprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.assets.AssetReference
import com.binarymonks.jj.core.extensions.copy
import com.binarymonks.jj.core.pools.recycleItems
import com.binarymonks.jj.core.properties.PropOverride
import com.binarymonks.jj.core.render.ShaderSpec
import com.binarymonks.jj.core.render.nodes.*
import com.binarymonks.jj.core.workshop.ParamStack
import kotlin.reflect.KClass

abstract class RenderNodeSpec {
    var id = JJ.B.nextID()
    var priority: Int = 0
    var color: PropOverride<Color> = PropOverride(Color.WHITE)
    var renderGraphType: RenderGraphType = RenderGraphType.DEFAULT
    var name: String? = null
    var shaderSpec: ShaderSpec? = null
    abstract fun getAssets(): Array<AssetReference>
    abstract fun makeNode(paramsStack: ParamStack): RenderNode
}


abstract class SpatialRenderNodeSpec : RenderNodeSpec() {
    var offsetX = 0f
    var offsetY = 0f
    var rotationD = 0f
}

class PolygonRenderNodeSpec : SpatialRenderNodeSpec() {
    var vertices: Array<Vector2> = Array()

    override fun makeNode(paramsStack: ParamStack): RenderNode {
        var sprite: PolygonSprite?
        if (PolygonRenderNode.haveBuilt(this)) {
            sprite = PolygonRenderNode.getSprite(this)
        } else {
            val vertCopy: Array<Vector2> = Array()
            vertices.forEach { vertCopy.add(it.copy()) }
            sprite = PolygonRenderNode.polygonSprite(vertices)
            recycleItems(vertCopy)
        }
        return PolygonRenderNode(
                priority,
                color.clone(),
                renderGraphType,
                name,
                shaderSpec,
                checkNotNull(sprite),
                paramsStack.scaleX,
                paramsStack.scaleY,
                offsetX * paramsStack.scaleX,
                offsetY * paramsStack.scaleY,
                rotationD
        )
    }

    override fun getAssets(): Array<AssetReference> {
        return Array()
    }
}

class LineChainRenderNodeSpec : SpatialRenderNodeSpec() {
    var vertices: Array<Vector2> = Array()
    var fill: Boolean = false

    override fun makeNode(paramsStack: ParamStack): RenderNode {
        val vertCopy: Array<Vector2> = Array()
        vertices.forEach { vertCopy.add(it.copy()) }
        return LineChainRenderNode(
                priority,
                color.clone(),
                renderGraphType,
                name,
                shaderSpec,
                fill,
                paramsStack.scaleX,
                paramsStack.scaleY,
                offsetX,
                offsetY,
                rotationD,
                vertCopy
        )
    }

    override fun getAssets(): Array<AssetReference> {
        return Array()
    }

}

class CircleRenderNodeSpec : SpatialRenderNodeSpec() {
    var radius: Float = 1f
    var fill: Boolean = true

    override fun makeNode(paramsStack: ParamStack): RenderNode {
        return CircleRenderNode(
                this.priority,
                this.color.clone(),
                renderGraphType,
                name,
                shaderSpec,
                fill = fill,
                offsetX = offsetX * paramsStack.scaleX,
                offsetY = offsetY * paramsStack.scaleY,
                radius = radius * paramsStack.scaleX
        )
    }

    override fun getAssets(): Array<AssetReference> {
        return Array()
    }
}

abstract class ImageNodeSpec<out K : KClass<*>> : SpatialRenderNodeSpec() {
    var width: Float = 1f
    var height: Float = 1f
    var assetPath: String? = null

    override fun getAssets(): Array<AssetReference> {
        val assets: Array<AssetReference> = Array()
        if (assetPath != null) {
            assets.add(AssetReference(assetClass(), assetPath!!))
        } else {
            throw Exception("Trying to fetch an asset that has no path set")
        }
        return assets
    }

    abstract fun assetClass(): K
}

class ParticleNodeSpec : SpatialRenderNodeSpec() {

    var effectPath: String? = null
    var imageDir: String? = null
    var atlatPath: String? = null
    var scale = 1f

    override fun getAssets(): Array<AssetReference> {
        if (imageDir == null && atlatPath == null) {
            throw Exception("Need to set one of imageDir or atlasPath")
        }
        //TODO: Make the spec point at an optional atlas to allow preloading particle images
        val assets: Array<AssetReference> = Array()
        return assets
    }

    override fun makeNode(paramsStack: ParamStack): RenderNode {
        val particleEffect = ParticleEffect()
        particleEffect.load(Gdx.files.internal(effectPath), Gdx.files.internal(imageDir))
        return ParticleRenderNode(
                priority = priority,
                color = color.clone(),
                renderGraphType = renderGraphType,
                name = name,
                shaderSpec = shaderSpec,
                particleEffect = particleEffect,
                offsetX = offsetX,
                offsetY = offsetY,
                rotationD = rotationD,
                scale = scale*paramsStack.scaleX
        )
    }

}

class TextureNodeSpec : ImageNodeSpec<KClass<Texture>>() {

    override fun assetClass() = Texture::class

    override fun makeNode(paramsStack: ParamStack): RenderNode {
        val frameProvider: FrameProvider = if (assetPath == null) {
            throw Exception("No Path set in backing image")
        } else {
            TextureFrameProvider(JJ.B.assets.getAsset(assetPath!!, Texture::class))
        }
        return FrameRenderNode(
                priority = priority,
                color = color.clone(),
                renderGraphType = renderGraphType,
                name = name,
                shaderSpec = shaderSpec,
                provider = frameProvider,
                offsetX = offsetX,
                offsetY = offsetY,
                rotationD = rotationD,
                width = width,
                height = height,
                scaleX = paramsStack.scaleX,
                scaleY = paramsStack.scaleY
        )

    }
}