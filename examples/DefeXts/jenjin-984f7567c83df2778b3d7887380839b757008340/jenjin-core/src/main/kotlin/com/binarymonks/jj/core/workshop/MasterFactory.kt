package com.binarymonks.jj.core.workshop

import box2dLight.Light
import box2dLight.PointLight
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.audio.SoundEffects
import com.binarymonks.jj.core.audio.SoundParams
import com.binarymonks.jj.core.extensions.copy
import com.binarymonks.jj.core.extensions.merge
import com.binarymonks.jj.core.physics.PhysicsNode
import com.binarymonks.jj.core.physics.PhysicsRoot
import com.binarymonks.jj.core.pools.mat3
import com.binarymonks.jj.core.pools.new
import com.binarymonks.jj.core.pools.recycle
import com.binarymonks.jj.core.pools.vec2
import com.binarymonks.jj.core.render.RenderRoot
import com.binarymonks.jj.core.render.nodes.RenderNode
import com.binarymonks.jj.core.scenes.Scene
import com.binarymonks.jj.core.specs.*
import com.binarymonks.jj.core.specs.physics.FixtureSpec
import com.binarymonks.jj.core.specs.physics.LightSpec
import com.binarymonks.jj.core.specs.physics.PhysicsSpec
import com.binarymonks.jj.core.specs.physics.PointLightSpec
import com.binarymonks.jj.core.specs.render.RenderSpec

class MasterFactory {

    private var paramsStackCache: Array<ParamStack> = Array()
    internal var scenePool: ScenePool = ScenePool()

    fun createScene(sceneSpec: SceneSpec, params: InstanceParams, rootScene: Scene?): Scene {
        var paramsStack = paramsStack()
        if (rootScene != null) {
            val rootParams = InstanceParams.from(rootScene)
            paramsStack.add(rootParams)
        }
        paramsStack.add(params)
        if (sceneSpec.container) {
            var aScene: Scene? = null
            sceneSpec.nodes.forEach {
                aScene = createContainerNode(it.value, paramsStack, rootScene)
            }
            if (aScene == null) {
                throw Exception("A container scene must have at least 1 node")
            }
            returnParamsStack(paramsStack)
            return aScene!!
        } else {
            val myScene: Scene = createSceneHelper(sceneSpec, paramsStack)
            returnParamsStack(paramsStack)
            rootScene?.add(myScene, sceneSpec.layer)
            return myScene
        }
    }

    private fun createContainerNode(sceneNode: SceneNode, paramsStack: ParamStack, rootScene: Scene?): Scene {
        paramsStack.add(sceneNode.instanceParams)
        val childSpec = sceneNode.sceneRef!!.resolve()
        val scene = createSceneHelper(childSpec, paramsStack)
        paramsStack.pop()
        rootScene?.add(scene, childSpec.layer)
        return scene
    }

    private fun createSceneHelper(
            sceneSpec: SceneSpec,
            paramsStack: ParamStack): Scene {

        val myScene = createSceneCore(sceneSpec, paramsStack)

        val scenes = ObjectMap<String, Scene>()
        for (entry in sceneSpec.nodes) {
            val nodeSceneRef = checkNotNull(entry.value.sceneRef).resolve()
            val nodeParams = entry.value.instanceParams
            paramsStack.add(nodeParams)
            val nodeScene = createSceneHelper(nodeSceneRef, paramsStack)
            scenes.put(entry.key, nodeScene)
            myScene.add(nodeScene, sceneSpec.layer)
            paramsStack.pop()
        }
        for (jointSpec in sceneSpec.joints) {
            val bodyA: Body = if (jointSpec.nameA == null) myScene.physicsRoot.b2DBody else scenes[jointSpec.nameA, myScene].physicsRoot.b2DBody
            val jointDef = jointSpec.toJointDef(
                    bodyA,
                    scenes[jointSpec.nameB].physicsRoot.b2DBody,
                    paramsStack.transformMatrix
            )
            JJ.B.physicsWorld.b2dworld.createJoint(jointDef)
        }

        return myScene
    }

    private fun createSceneCore(sceneSpec: SceneSpec, paramsStack: ParamStack): Scene {
        if (sceneSpec.pooled) {
            val scene: Scene? = scenePool.get(paramsStack.scaleX, paramsStack.scaleY, sceneSpec.id)
            if (scene != null) {
                var worldPosition = vec2().mul(paramsStack.transformMatrix)
                scene.resetFromPool(worldPosition.x, worldPosition.y, paramsStack.rotationD)
                scene.uniqueName = paramsStack.peek().uniqueInstanceName
                scene.name = paramsStack.peek().name
                scene.properties.clear()
                scene.properties.merge(sceneSpec.properties).merge(paramsStack.peek().properties)
                return scene
            }
        }
        val scene = Scene(
                name = paramsStack.peek().name,
                specName = sceneSpec.name,
                uniqueName = paramsStack.peek().uniqueInstanceName,
                specID = sceneSpec.id,
                scale = vec2(paramsStack.scaleX, paramsStack.scaleY),
                physicsRoot = buildPhysicsRoot(sceneSpec.physics, paramsStack),
                renderRoot = buildRenderRoot(sceneSpec.render, paramsStack),
                soundEffects = buildSoundEffects(sceneSpec.sounds.params),
                properties = sceneSpec.properties.copy().merge(paramsStack.peek().properties),
                pooled = sceneSpec.pooled
        )
        addBehaviour(scene, sceneSpec)

        return scene
    }

    private fun addBehaviour(scene: Scene, sceneSpec: SceneSpec) {
        for (component in sceneSpec.components) {
            scene.addComponent(component.clone())
        }
    }

    private fun buildSoundEffects(sounds: Array<SoundParams>): SoundEffects {
        val soundEffects = SoundEffects()
        soundEffects.addSoundEffects(sounds)
        return soundEffects
    }

    private fun buildRenderRoot(renderSpec: RenderSpec, paramsStack: ParamStack): RenderRoot {
        val renderRoot: RenderRoot = RenderRoot(renderSpec.id)
        for (nodeSpec in renderSpec.renderNodes) {
            val node: RenderNode = nodeSpec.makeNode(paramsStack)
            renderRoot.addNode(node)
        }
        return renderRoot
    }

    private fun buildPhysicsRoot(physicsSpec: PhysicsSpec, paramsStack: ParamStack): PhysicsRoot {
        val def = BodyDef()

        var worldPosition = vec2().mul(paramsStack.transformMatrix)

        def.position.set(worldPosition.x, worldPosition.y)
        def.angle = paramsStack.rotationD * MathUtils.degreesToRadians
        def.type = physicsSpec.bodyType
        def.fixedRotation = physicsSpec.fixedRotation
        def.linearDamping = physicsSpec.linearDamping
        def.angularDamping = physicsSpec.angularDamping
        def.bullet = physicsSpec.bullet
        def.allowSleep = physicsSpec.allowSleep
        def.gravityScale = physicsSpec.gravityScale
        val body = JJ.B.physicsWorld.b2dworld.createBody(def)

        val physicsRoot = PhysicsRoot(body)

        physicsRoot.collisionResolver.collisions.copyAppendFrom(physicsSpec.collisions)

        for (fixtureSpec in physicsSpec.fixtures) {
            buildFixture(physicsRoot, fixtureSpec, body, paramsStack)
        }

        for (lightSpec in physicsSpec.lights) {
            val light = buildLight(lightSpec, body, paramsStack.peek())
            val colData = lightSpec.collisionGroup.toCollisionData(paramsStack.peek().properties)
            light.setContactFilter(colData.category, 0, colData.mask)
            if (lightSpec.name != null) {
                physicsRoot.lights.add(checkNotNull(lightSpec.name), light)
            } else {
                physicsRoot.lights.add(light)
            }
        }

        return physicsRoot

    }

    private fun buildLight(lightSpec: LightSpec, body: Body, instanceParams: InstanceParams): Light {
        when (lightSpec) {
            is PointLightSpec -> return buildPointLight(lightSpec, body, instanceParams)
            else -> throw Exception("Don't know that light")
        }
    }

    private fun buildPointLight(pointSpec: PointLightSpec, body: Body, instanceParams: InstanceParams): Light {
        val pl = PointLight(
                JJ.B.renderWorld.rayHandler,
                pointSpec.rays,
                pointSpec.color.clone().get(instanceParams),
                pointSpec.reach,
                0f,
                0f
        )
        pl.attachToBody(body, pointSpec.offsetX, pointSpec.offsetY)
        return pl
    }

    private fun buildFixture(physicsRoot: PhysicsRoot, fixtureSpec: FixtureSpec, body: Body, params: ParamStack) {
        val shape = buildShape(fixtureSpec, params.scaleX, params.scaleY, fixtureSpec.offsetX, fixtureSpec.offsetY)
        val fDef = FixtureDef()
        fDef.shape = shape
        val material = JJ.physics.materials.getMaterial(fixtureSpec.material.get(params.peek()))
        fDef.density = material?.density ?: fixtureSpec.density
        fDef.friction = material?.friction ?: fixtureSpec.friction
        fDef.restitution = material?.restitution ?: fixtureSpec.restitution
        fDef.isSensor = fixtureSpec.isSensor
        val cd = fixtureSpec.collisionGroup.toCollisionData(checkNotNull(params.peek()).properties)
        fDef.filter.categoryBits = cd.category
        fDef.filter.maskBits = cd.mask
        val fixture = body.createFixture(fDef)
        val physicsNode = PhysicsNode(
                fixtureSpec.name,
                fixture, physicsRoot,
                fixtureSpec.properties.copy(),
                material?.name
        )
        physicsNode.collisionResolver.collisions.copyAppendFrom(fixtureSpec.collisions)
        fixture.userData = physicsNode
        shape!!.dispose()
    }

    private fun buildShape(nodeSpec: FixtureSpec, scaleX: Float, scaleY: Float, offsetX: Float = 0f, offsetY: Float = 0f): Shape? {
        if (nodeSpec.shape is Rectangle) {
            val polygonRectangle = nodeSpec.shape as Rectangle
            val boxshape = PolygonShape()
            boxshape.setAsBox(polygonRectangle.width * scaleX / 2.0f, polygonRectangle.height * scaleY / 2.0f, new(Vector2::class.java).set(nodeSpec.offsetX * scaleX, nodeSpec.offsetY * scaleY), nodeSpec.rotationD * MathUtils.degreesToRadians)
            return boxshape
        } else if (nodeSpec.shape is Circle) {
            val circle = nodeSpec.shape as Circle
            val circleShape = CircleShape()
            circleShape.radius = circle.radius * scaleX
            circleShape.position = vec2().set(nodeSpec.offsetX * scaleX, nodeSpec.offsetY * scaleY)
            return circleShape
        } else if (nodeSpec.shape is Polygon) {
            val trans = mat3()
            trans.scale(scaleX, scaleY)
            trans.translate(offsetX, offsetY)
            trans.rotate(nodeSpec.rotationD)
            val polygonSpec = nodeSpec.shape as Polygon
            val polygonShape = PolygonShape()
            val vertices = arrayOfNulls<Vector2>(polygonSpec.vertices.size)
            for (i in 0..polygonSpec.vertices.size - 1) {
                vertices[i] = polygonSpec.vertices.get(i).copy().mul(trans)
            }
            polygonShape.set(vertices)
            recycle(trans)
            return polygonShape
        } else if (nodeSpec.shape is Chain) {
            val trans = mat3()
            trans.scale(scaleX, scaleY)
            trans.translate(offsetX, offsetY)
            trans.rotate(nodeSpec.rotationD)
            val chainSpec = nodeSpec.shape as Chain
            val chainShape = ChainShape()
            val vertices = arrayOfNulls<Vector2>(chainSpec.vertices.size)
            for (i in 0..chainSpec.vertices.size - 1) {
                vertices[i] = chainSpec.vertices.get(i).copy().mul(trans)
            }
            chainShape.createChain(vertices)
            recycle(trans)
            return chainShape
        }
        return null
    }

    private fun paramsStack(): ParamStack {
        if (paramsStackCache.size > 0) {
            return paramsStackCache.pop()
        }
        return ParamStack()
    }

    private fun returnParamsStack(stack: ParamStack) {
        stack.clear()
        stack.rotationD = 0f
        stack.x = 0f
        stack.y = 0f
        stack.scaleX = 1f
        stack.scaleY = 1f
        stack.transformMatrix.idt()
        paramsStackCache.add(stack)
    }


}


class ParamStack : Array<InstanceParams>() {

    var rotationD = 0f
    var x = 0f
    var y = 0f
    var scaleX = 1f
    var scaleY = 1f
    var transformMatrix: Matrix3 = mat3()

    override fun add(params: InstanceParams) {
        super.add(params)
        rotationD += params.rotationD
        x += params.x
        y += params.y
        scaleX *= params.scaleX
        scaleY *= params.scaleY
        transformMatrix.mul(params.getTransformMatrix())
    }

    override fun pop(): InstanceParams? {
        var removing = super.pop()
        if (removing != null) {
            rotationD -= removing.rotationD
            x -= removing.x
            y -= removing.y
            scaleX /= removing.scaleX
            scaleY /= removing.scaleY
            transformMatrix.mul(removing.getTransformMatrix().inv())
        }
        return removing
    }
}