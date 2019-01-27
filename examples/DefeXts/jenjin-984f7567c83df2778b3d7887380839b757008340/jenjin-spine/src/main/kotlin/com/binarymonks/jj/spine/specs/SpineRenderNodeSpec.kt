package com.binarymonks.jj.spine.specs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.assets.AssetReference
import com.binarymonks.jj.core.pools.vec2
import com.binarymonks.jj.core.render.ShaderSpec
import com.binarymonks.jj.core.render.nodes.RenderNode
import com.binarymonks.jj.core.specs.render.RenderNodeSpec
import com.binarymonks.jj.core.spine.RagDollBone
import com.binarymonks.jj.spine.components.SPINE_RENDER_NAME
import com.binarymonks.jj.spine.render.SpineRenderNode
import com.binarymonks.jj.core.workshop.ParamStack
import com.esotericsoftware.spine.Bone
import com.esotericsoftware.spine.BoneData
import com.esotericsoftware.spine.Skeleton
import com.esotericsoftware.spine.SkeletonJson
import com.esotericsoftware.spine.attachments.AtlasAttachmentLoader


internal class SpineRenderNodeSpec(
        var atlasPath: String? = null,
        var dataPath: String? = null,
        var originX: Float = 0f,
        var originY: Float = 0f,
        var scale: Float = 1f,
        var shader: ShaderSpec?
) : RenderNodeSpec() {

    /**
     * A builder constructor
     */
    constructor(
            atlasPath: String? = null,
            dataPath: String? = null,
            originX: Float = 0f,
            originY: Float = 0f,
            scale: Float = 1f,
            shader: ShaderSpec?,
            build: SpineRenderNodeSpec.() -> Unit
    ) : this(atlasPath, dataPath, originX, originY, scale, shader) {
        this.build()
    }

    init {
        shaderSpec =shader
    }

    override fun getAssets(): Array<AssetReference> {
        return Array.with(AssetReference(TextureAtlas::class, checkNotNull(atlasPath)))
    }

    override fun makeNode(paramsStack: ParamStack): RenderNode {
        val atlas = JJ.assets.getAsset(checkNotNull(atlasPath), TextureAtlas::class)
        val atlasLoader = AtlasAttachmentLoader(atlas)
        val json = SkeletonJson(atlasLoader)
        val actualScale = scale * paramsStack.scaleX
        json.scale = actualScale
        val skeletonData = json.readSkeletonData(Gdx.files.internal(dataPath))
        val skeleton = Skeleton(skeletonData, this::getRagDollBone)
        val positionOffset = vec2()
        positionOffset.set(originX, originY).scl(scale * paramsStack.scaleX)

        val spineNode = SpineRenderNode(
                priority,
                color,
                renderGraphType,
                SPINE_RENDER_NAME,
                shaderSpec,
                skeleton,
                skeletonData,
                positionOffset
        )
        return spineNode
    }


    internal fun getRagDollBone(boneData: BoneData, skeleton: Skeleton, parent: Bone?): RagDollBone {
        return RagDollBone(boneData, skeleton, parent)
    }

}