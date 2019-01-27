package com.binarymonks.jj.spine.specs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.Copyable
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.assets.AssetReference
import com.binarymonks.jj.core.components.Component
import com.binarymonks.jj.core.extensions.copy
import com.binarymonks.jj.core.pools.vec2
import com.binarymonks.jj.core.render.ShaderSpec
import com.binarymonks.jj.core.scenes.ScenePath
import com.binarymonks.jj.core.specs.*
import com.binarymonks.jj.core.specs.physics.FixtureSpec
import com.binarymonks.jj.core.spine.SpineEventHandler
import com.binarymonks.jj.spine.components.SpineBoneComponent
import com.binarymonks.jj.spine.components.SpineComponent
import com.esotericsoftware.spine.Bone
import com.esotericsoftware.spine.Event
import com.esotericsoftware.spine.Skeleton
import com.esotericsoftware.spine.SkeletonJson
import com.esotericsoftware.spine.attachments.AtlasAttachmentLoader
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment
import kotlin.reflect.KClass
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2


class SpineSpec() : SceneSpecRef {

    constructor(build: SpineSpec.() -> Unit) : this() {
        this.build()
    }

    private val renderModel = RenderModel()

    private val spineAnimaitons = SpineAnimations()

    private val root = SceneSpec { physics { bodyType = BodyDef.BodyType.KinematicBody } }

    private var skel: SpineSkeletonSpec? = null

    fun rootScene(build: SceneSpec.() -> Unit) {
        root.build()
    }

    fun spineRender(build: RenderModel.() -> Unit) {
        renderModel.build()
    }

    fun skeleton(build: SpineSkeletonSpec.() -> Unit) {
        val skel = SpineSkeletonSpec()
        skel.build()
        this.skel = skel
    }

    fun animations(build: SpineAnimations.() -> Unit) {
        spineAnimaitons.build()
    }


    override fun resolve(): SceneSpec {
        val spineComponent = SpineComponent(spineAnimaitons)
        val renderSpec = SpineRenderNodeSpec(
                renderModel.atlasPath,
                renderModel.dataPath,
                renderModel.originX,
                renderModel.originY,
                renderModel.scale,
                renderModel.shader
        )
        renderSpec.priority = renderModel.priority
        root.render.renderNodes.add(renderSpec)
        root.component(spineComponent)
        if (skel != null) {
            val atlas = JJ.assets.getAsset(checkNotNull(renderModel.atlasPath), TextureAtlas::class)
            val atlasLoader = AtlasAttachmentLoader(atlas)
            val json = SkeletonJson(atlasLoader)
            val actualScale = renderModel.scale
            json.scale = actualScale
            val skeletonData = json.readSkeletonData(Gdx.files.internal(renderModel.dataPath))
            val skeleton = Skeleton(skeletonData)
            buildPhysicsSkeleton(root, skeleton, spineComponent, checkNotNull(skel))
        }
        return root
    }

    override fun getAssets(): Array<AssetReference> {
        val assets: Array<AssetReference> = root.getAssets()
        assets.add(AssetReference(TextureAtlas::class, checkNotNull(renderModel.atlasPath)))
        return assets
    }

    private fun buildPhysicsSkeleton(scene: SceneSpec, skeleton: Skeleton, spineComponent: SpineComponent, customSkeleton: SpineSkeletonSpec) {
        val bone = skeleton.rootBone
        val path: Array<String> = Array()
        path.add(bone.data.name)
        buildBoneRecurse(bone, customSkeleton.coreMass, customSkeleton.coreMotorTorque, path, scene, spineComponent, skeleton, customSkeleton)
    }

    private fun buildBoneRecurse(
            bone: Bone, mass: Float,
            motorTorque: Float,
            path: Array<String>,
            parentScene: SceneSpec,
            spineComponent: SpineComponent,
            skeleton: Skeleton,
            customSkeleton: SpineSkeletonSpec): String {
        val boneName = path.last()
        parentScene.addNode(
                SceneSpec {
                    physics {
                        bodyType = BodyDef.BodyType.DynamicBody
                        gravityScale = 0f
                        val fixture = buildFixture(bone, mass, skeleton, customSkeleton.customs.get(boneName), customSkeleton)
                        addFixture(fixture)
                        collisions.copyAppendFrom(customSkeleton.all.collisions)
                    }
                    component(SpineBoneComponent()) {
                        bonePath = path
                    }
                    bone.children.forEach {
                        val a = path.copy()
                        a.add(it.data.name)
                        val childName = buildBoneRecurse(it, mass * customSkeleton.massFalloff, motorTorque * customSkeleton.coreMotorTorqueFalloff, a, this@SceneSpec, spineComponent, skeleton, customSkeleton)
                        val custom = customSkeleton.customs.get(childName)
                        revJoint(null, childName, vec2(it.x, it.y), vec2()) {
                            collideConnected = false
                            enableLimit = custom?.boneOverride?.enableLimit ?: customSkeleton.all.enableLimit
                            lowerAngle = custom?.boneOverride?.lowerAngle ?: customSkeleton.all.lowerAngle
                            upperAngle = custom?.boneOverride?.upperAngle ?: customSkeleton.all.upperAngle

                            enableMotor = custom?.boneOverride?.enableMotor ?: customSkeleton.all.enableMotor
                            motorSpeed = custom?.boneOverride?.motorSpeed ?: 0f
                            maxMotorTorque = custom?.boneOverride?.maxMotorTorque ?: motorTorque
                        }
                    }
                    customSkeleton.all.properties.forEach {
                        prop(it.key, it.value)
                    }
                    for (component in customSkeleton.all.components) {
                        this.component(component)
                    }
                    if (customSkeleton.customs.containsKey(boneName)) {
                        for (component in customSkeleton.customs.get(boneName).components) {
                            this.component(component)
                        }
                    }
                },
                params { name = bone.data.name }
        )
        spineComponent.bonePaths.put(bone.data.name, ScenePath(path.copy()))
        return bone.data.name
    }

    private fun buildFixture(bone: Bone, mass: Float, skeleton: Skeleton, customBone: CustomBone?, customSkeleton: SpineSkeletonSpec): FixtureSpec {
        if (customSkeleton.boundingBoxes) {
            val boundingBox: Polygon? = findPolygon(bone, skeleton)
            if (boundingBox != null) {
                return FixtureSpec {
                    shape = customBone?.boneOverride?.shape ?: boundingBox
                    density = customBone?.boneOverride?.mass ?: mass
                    restitution = customBone?.boneOverride?.restitution ?: customSkeleton.all.restitution
                    friction = customBone?.boneOverride?.friction ?: customSkeleton.all.friction
                    val mat = customBone?.boneOverride?.material ?: customSkeleton.all.material
                    if (mat != null) {
                        material.set(mat)
                    }
                    collisionGroup = customBone?.boneOverride?.collisionGroup ?: customSkeleton.all.collisionGroup
                }
            }
        }
        val boneLength = bone.data.length
        if (boneLength > 0) {
            return FixtureSpec {
                shape = customBone?.boneOverride?.shape ?: Rectangle(boneLength, customSkeleton.boneWidth)
                offsetX = customBone?.boneOverride?.offsetX ?: boneLength / 2
                offsetY = customBone?.boneOverride?.offsetX ?: 0f
                density = customBone?.boneOverride?.mass ?: mass
                restitution = customBone?.boneOverride?.restitution ?: customSkeleton.all.restitution
                friction = customBone?.boneOverride?.friction ?: customSkeleton.all.friction
                val mat = customBone?.boneOverride?.material ?: customSkeleton.all.material
                if (mat != null) {
                    material.set(mat)
                }
                collisionGroup = customBone?.boneOverride?.collisionGroup ?: customSkeleton.all.collisionGroup
            }
        }
        return FixtureSpec {
            shape = Circle(customSkeleton.boneWidth)
            density = mass
            collisionGroup = customSkeleton.all.collisionGroup
        }
    }

    private fun findPolygon(bone: Bone, skeleton: Skeleton): Polygon? {
        val boneName = bone.data.name
        for (slot in skeleton.slots) {
            if (slot.bone.data.name == boneName) {
                if (slot.attachment is BoundingBoxAttachment) {
                    val bb: BoundingBoxAttachment = slot.attachment as BoundingBoxAttachment
                    val poly = Polygon()
                    for (i in 0..bb.vertices.size - 2 step 2) {
                        poly.vertices.add(vec2(bb.vertices[i], bb.vertices[i + 1]))
                    }
                    return poly
                }
            }
        }
        return null
    }
}

class RenderModel {
    var atlasPath: String? = null
    var dataPath: String? = null
    var originX: Float = 0f
    var originY: Float = 0f
    var scale: Float = 1f
    var shader: ShaderSpec? = null
    var priority = 0
}

internal class CrossFade(
        val fromName: String,
        val toName: String,
        val duration: Float
)

internal class EventToComponentCall(
        val componentType: KClass<Component>,
        val componentFunction: KFunction2<Component, @ParameterName(name = "event") Event, Unit>
)

internal class ComponentCall(
        val componentType: KClass<Component>,
        val componentFunction: KFunction1<Component, Unit>
)

class SpineAnimations : Copyable<SpineAnimations> {

    var startingAnimation: String? = null
    internal var crossFades: Array<CrossFade> = Array()
    internal var handlers: ObjectMap<String, SpineEventHandler> = ObjectMap()
    internal var functions: ObjectMap<String, () -> Unit> = ObjectMap()
    internal var componentHandlers: ObjectMap<String, EventToComponentCall> = ObjectMap()
    internal var componentFunctions: ObjectMap<String, ComponentCall> = ObjectMap()

    fun setMix(fromName: String, toName: String, duration: Float) {
        crossFades.add(CrossFade(fromName, toName, duration))
    }

    fun registerEventHandler(eventName: String, handler: SpineEventHandler) {
        handlers.put(eventName, handler)
    }

    fun registerEventFunction(eventName: String, function: () -> Unit) {
        functions.put(eventName, function)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Component> registerEventHandler(eventName: String, componentType: KClass<T>, componentFunction: KFunction2<T, @ParameterName(name = "event") Event, Unit>) {
        componentHandlers.put(eventName, EventToComponentCall(componentType as KClass<Component>, componentFunction as KFunction2<Component, @kotlin.ParameterName(name = "event") Event, Unit>))
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Component> registerEventFunction(eventName: String, componentType: KClass<T>, componentFunction: KFunction1<T, Unit>) {
        componentFunctions.put(eventName, ComponentCall(componentType as KClass<Component>, componentFunction as KFunction1<Component, Unit>))
    }

    override fun clone(): SpineAnimations {
        val copy = SpineAnimations()
        copy.startingAnimation = startingAnimation
        copy.crossFades = crossFades.copy()
        copy.handlers = handlers.copy()
        copy.componentHandlers = componentHandlers.copy()
        componentFunctions = componentFunctions.copy()
        return copy
    }
}







