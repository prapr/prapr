package com.binarymonks.jj.core.components

import com.binarymonks.jj.core.mockoutGDXinJJ
import com.binarymonks.jj.core.scenes.Scene
import com.binarymonks.jj.core.scenes.ScenePath
import com.binarymonks.jj.core.testScene
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ComponentLifeCycleTest {


    lateinit var mockComponent: Component

    lateinit var scene: Scene


    @Before
    fun setUp() {
        mockoutGDXinJJ()
        scene = testScene()
        mockComponent = Mockito.mock(Component::class.java)
        Mockito.`when`(mockComponent.type()).thenReturn(Component::class)
    }


    @Test
    fun addToScene_whenSceneIsNotInitiallyAddedToWorld_removed_and_added_again() {

        scene.addComponent(mockComponent)

        Mockito.verify(mockComponent, Mockito.never()).onAddToWorld()

        scene.onAddToWorld()

        Mockito.verify(mockComponent, Mockito.atMost(1)).onAddToWorld()

        scene.executeDestruction()

        Mockito.verify(mockComponent).onRemoveFromWorld()

        scene.onAddToWorld()

        Mockito.verify(mockComponent, Mockito.atLeast(2)).onAddToWorld()

    }

    @Test
    fun addToScene_whenSceneIsInitiallyAddedToWorld() {
        Mockito.`when`(mockComponent.isDone()).thenReturn(true)

        scene.onAddToWorld()
        scene.addComponent(mockComponent)

        Mockito.verify(mockComponent).onAddToWorld()
    }

    @Test
    fun addToSceneAfterBeingRemovedFromScene() {
        Mockito.`when`(mockComponent.isDone()).thenReturn(true)

        scene.onAddToWorld()
        scene.addComponent(mockComponent)
        scene.update()
        scene.update()
        Mockito.verify(mockComponent, Mockito.atLeast(1)).onRemoveFromWorld()


        scene.addComponent(mockComponent)
        Mockito.verify(mockComponent, Mockito.atLeast(2)).onAddToWorld()

    }

    @Test
    fun onAddToWorld_graphIsComplete_whenAddedToWorld() {
        val parentScene = testScene()
        parentScene.addComponent(GetMyChildSceneMock())
        val childScene = testScene(name = "child")
        childScene.addComponent(GetMyParentSceneMock())
        childScene.addComponent(GetMyChildSceneMock())
        parentScene.add(childScene)
        val grandChildScene = testScene(name = "child")
        grandChildScene.addComponent(GetMyParentSceneMock())
        childScene.add(grandChildScene)

        parentScene.onAddToWorld()
    }

    @Test
    fun addAddToWorld_graphIsComplete_whenAddedToSceneAlreadyInWorld() {
        val parentScene = testScene()
        parentScene.onAddToWorld()

        val childScene = testScene(name = "child")
        childScene.addComponent(GetMyParentSceneMock())
        childScene.addComponent(GetMyChildSceneMock())
        val grandChildScene = testScene(name = "child")
        grandChildScene.addComponent(GetMyParentSceneMock())
        childScene.add(grandChildScene)

        parentScene.add(childScene)
    }

}

class GetMyParentSceneMock : Component() {
    override fun onAddToWorld() {
        me().getNode(ScenePath().up()).name
    }
}

class GetMyChildSceneMock : Component() {
    override fun onAddToWorld() {
        me().getChild("child")!!.name
    }
}