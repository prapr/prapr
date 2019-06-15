package com.binarymonks.jj.core.physics

import com.binarymonks.jj.core.mockoutGDXinJJ
import com.binarymonks.jj.core.scenes.Scene
import com.binarymonks.jj.core.testScene
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CollisionHandlerLifeCycleTest {


    lateinit var mockBeginCollisionHandler: CollisionHandler
    lateinit var mockPreSolveHandler: PreSolveHandler
    lateinit var mockFinalBeginCollisionHandler: CollisionHandler
    lateinit var mockEndCollisionHandler: CollisionHandler
    lateinit var mockPostSolveHandler: PostSolveHandler

    lateinit var scene: Scene


    @Before
    fun setUp() {
        mockoutGDXinJJ()
        scene = testScene()
        mockBeginCollisionHandler = Mockito.mock(CollisionHandler::class.java)
        mockPreSolveHandler = Mockito.mock(PreSolveHandler::class.java)
        mockFinalBeginCollisionHandler = Mockito.mock(CollisionHandler::class.java)
        mockEndCollisionHandler = Mockito.mock(CollisionHandler::class.java)
        mockPostSolveHandler = Mockito.mock(PostSolveHandler::class.java)
        scene.physicsRoot.collisionResolver.collisions.begin(mockBeginCollisionHandler)
        scene.physicsRoot.collisionResolver.collisions.finalBegin(mockFinalBeginCollisionHandler)
        scene.physicsRoot.collisionResolver.collisions.preSolve(mockPreSolveHandler)
        scene.physicsRoot.collisionResolver.collisions.end(mockEndCollisionHandler)
        scene.physicsRoot.collisionResolver.collisions.postSolve(mockPostSolveHandler)
    }

    @Test
    fun collisionHandlersAreCalledWhenDestroyedAndAddedToWorld() {
        scene.onAddToWorld()

        Mockito.verify(mockBeginCollisionHandler).onAddToWorld()
        Mockito.verify(mockFinalBeginCollisionHandler).onAddToWorld()
        Mockito.verify(mockPreSolveHandler).onAddToWorld()
        Mockito.verify(mockEndCollisionHandler).onAddToWorld()
        Mockito.verify(mockPostSolveHandler).onAddToWorld()

        scene.executeDestruction()

        Mockito.verify(mockBeginCollisionHandler).onRemoveFromWorld()
        Mockito.verify(mockFinalBeginCollisionHandler).onRemoveFromWorld()
        Mockito.verify(mockPreSolveHandler).onRemoveFromWorld()
        Mockito.verify(mockEndCollisionHandler).onRemoveFromWorld()
        Mockito.verify(mockPostSolveHandler).onRemoveFromWorld()

    }

}