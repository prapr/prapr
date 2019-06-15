package com.binarymonks.jj.core.render.nodes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonRegion
import com.badlogic.gdx.graphics.g2d.PolygonSprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.pools.recycle
import com.binarymonks.jj.core.pools.vec2
import com.binarymonks.jj.core.properties.PropOverride
import com.binarymonks.jj.core.render.ShaderSpec
import com.binarymonks.jj.core.specs.render.RenderGraphType
import com.binarymonks.jj.core.specs.render.RenderNodeSpec

class PolygonRenderNode constructor(
        priority: Int,
        color: PropOverride<Color>,
        renderGraphType: RenderGraphType,
        name:String?,
        renderPipe: ShaderSpec?,
        internal var poly: PolygonSprite,
        internal var scaleX: Float = 1f,
        internal var scaleY: Float = 1f,
        internal var offsetX: Float = 0f,
        internal var offsetY: Float = 0f,
        internal var rotationD: Float = 0f
) : BatchBasedRenderNode(priority, color, renderGraphType, name, renderPipe) {

    override fun render(camera: OrthographicCamera) {
        JJ.B.renderWorld.switchToBatch()
        val parentRotation = (me().physicsRoot.rotationR() * MathUtils.radDeg)
        val parentTransform = me().physicsRoot.transform
        val myPosition = vec2(offsetX, offsetY)
        parentTransform.mul(myPosition)
        poly.color = color.get()
        poly.setOrigin(0f, 0f)
        poly.setPosition(myPosition.x, myPosition.y)
        poly.rotation = parentRotation + rotationD
        poly.setScale(scaleX, scaleY)
        poly.draw(JJ.B.renderWorld.polyBatch)
        recycle(myPosition)
    }

    override fun dispose() {}

    companion object {

        private val spriteByNodeIDCache: ObjectMap<Int, PolygonSprite> = ObjectMap()
        internal var triangulator = EarClippingTriangulator()

        fun haveBuilt(renderNodeSpec: RenderNodeSpec): Boolean {
            return spriteByNodeIDCache.containsKey(renderNodeSpec.id)
        }

        fun polygonSprite(points: Array<Vector2>): PolygonSprite {
            val pix = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            pix.setColor(0xFFFFFFFF.toInt())
            pix.fill()
            val textureSolid = Texture(pix)
            val vertices = FloatArray(points.size * 2)
            val triangleIndices = triangulator.computeTriangles(vertices)
            for (i in 0..points.size - 1) {
                val point = points.get(i)
                val offset = i * 2
                vertices[offset] = point.x
                vertices[offset + 1] = point.y
            }
            val polyReg = PolygonRegion(TextureRegion(textureSolid),
                    vertices, triangleIndices.toArray())
            val poly = PolygonSprite(polyReg)
            return poly
        }

        fun getSprite(renderNodeSpec: RenderNodeSpec): PolygonSprite? {
            return spriteByNodeIDCache[renderNodeSpec.id]
        }


    }

}
