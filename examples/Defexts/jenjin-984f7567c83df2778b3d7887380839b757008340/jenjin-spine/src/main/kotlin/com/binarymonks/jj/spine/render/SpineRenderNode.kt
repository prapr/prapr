package com.binarymonks.jj.spine.render

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.properties.PropOverride
import com.binarymonks.jj.core.render.ShaderSpec
import com.binarymonks.jj.core.render.nodes.BatchBasedRenderNode
import com.binarymonks.jj.core.specs.render.RenderGraphType
import com.binarymonks.jj.spine.JJSpine
import com.esotericsoftware.spine.Skeleton
import com.esotericsoftware.spine.SkeletonData

class SpineRenderNode(
        priority: Int,
        color: PropOverride<Color>,
        renderGraphType: RenderGraphType,
        name: String? = null,
        shaderSpec: ShaderSpec?,
        internal var skeleton: Skeleton,
        internal var skeletonData: SkeletonData,
        internal var positionOffset: Vector2
) : BatchBasedRenderNode(priority, color, renderGraphType, name, shaderSpec) {

    override fun render(camera: OrthographicCamera) {
        val position = parent!!.physicsRoot.position().sub(positionOffset)
        skeleton.setPosition(position.x, position.y)
        skeleton.updateWorldTransform()
        if (JJ.B.config.spine.render) {
            JJ.B.renderWorld.switchToBatch()
            JJSpine.skeletonRenderer.draw(JJ.B.renderWorld.polyBatch, skeleton)
        }
    }

    override fun dispose() {}
}
