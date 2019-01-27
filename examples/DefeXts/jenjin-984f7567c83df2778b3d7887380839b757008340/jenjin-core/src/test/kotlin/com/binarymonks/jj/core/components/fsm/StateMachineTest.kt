package com.binarymonks.jj.core.components.fsm

import com.binarymonks.jj.core.mockoutGDXinJJ
import com.binarymonks.jj.core.properties.PropOverride
import com.binarymonks.jj.core.scenes.Scene
import com.binarymonks.jj.core.testScene
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito


class StateMachineTest {

    @Before
    fun setUp(){
        mockoutGDXinJJ()
    }

    @Test
    fun setScene_injectsSceneIntoPropOverrides(){

        val testScene = testScene()

        val myStateMachine = MyStateMachine()

        myStateMachine.scene=testScene

        Assert.assertSame(testScene,myStateMachine.myPropOverrideVal.hasProps)
        Assert.assertSame(testScene,myStateMachine.myPropOverrideVar.hasProps)
        Assert.assertSame(testScene,myStateMachine.initialState.hasProps)
    }

    @Test
    fun stateMachineInitializationAndLifecycle() {

        val scene = Mockito.mock(Scene::class.java)
        val initialStateMock = Mockito.mock(State::class.java)
        val anotherStateMock = Mockito.mock(State::class.java)
        val combinationStateMock = Mockito.mock(State::class.java)
        val transitionMock = Mockito.mock(TransitionCondition::class.java)
        val mocks = listOf<State>(initialStateMock, anotherStateMock, combinationStateMock)

        val stateMachine = StateMachine {
            initialState("initial")
            addState("initial", initialStateMock).withTransitions {
                to("another").whenJust(transitionMock)
            }
            addState("another", anotherStateMock)
            addComposite("composite") {
                part(combinationStateMock)
            }
        }
        stateMachine.onAddToWorld()
        mocks.forEach {
            Mockito.verify(it).onAddToWorld()
        }
        Mockito.verify(transitionMock).onAddToWorld()
        stateMachine.scene = scene
        mocks.forEach {
            Mockito.verify(it).scene = scene
        }
        Mockito.verify(transitionMock).scene = scene
        stateMachine.onRemoveFromWorld()
        mocks.forEach {
            Mockito.verify(it).onRemoveFromWorldWrapper()
        }
        Mockito.verify(transitionMock).onRemoveFromWorld()
    }

    @Test
    fun transitionsStatesInitial() {
        val initialStateMock = Mockito.mock(State::class.java)
        val transitionMock = Mockito.mock(TransitionCondition::class.java)

        val stateMachine = StateMachine {
            addState("initial", initialStateMock).withTransitions {
                to("anotherState").whenJust(transitionMock)
            }
            initialState("initial")
        }

        stateMachine.update()
        Mockito.verify(initialStateMock).enterWrapper(stateMachine)
        Mockito.verify(transitionMock).enterWrapper(stateMachine)
    }

    @Test
    fun transitionsStatesInitialFromProperty() {
        val initialStateMock = Mockito.mock(State::class.java)

        val sceneWithProperties = testScene()
        sceneWithProperties.properties.put("initialStateName","initial")

        val stateMachine = StateMachine {
            addState("initial", initialStateMock)
            initialStateProp("initialStateName")
        }
        sceneWithProperties.addComponent(stateMachine)

        stateMachine.update()
        Mockito.verify(initialStateMock).enterWrapper(stateMachine)
    }

    @Test
    fun transitionsStatesWhenConditionMet() {
        val initialStateMock = Mockito.mock(State::class.java)
        val anotherStateMock = Mockito.mock(State::class.java)
        val aThirdStateMock = Mockito.mock(State::class.java)
        val transitionToAnotherMock = Mockito.mock(TransitionCondition::class.java)
        Mockito.`when`(transitionToAnotherMock.met()).thenReturn(true)

        val transitionToThirdMock = Mockito.mock(TransitionCondition::class.java)
        Mockito.`when`(transitionToThirdMock.met()).thenReturn(true)


        val stateMachine = StateMachine {
            initialState("initial")
            addState("initial", initialStateMock).withTransitions {
                to("another").whenJust(transitionToAnotherMock)
            }
            addState("another", anotherStateMock).withTransitions {
                to("aThird").whenJust(transitionToThirdMock)
            }
            addState("aThird", aThirdStateMock)
        }

        stateMachine.update()
        Mockito.verify(initialStateMock).exitWrapper()
        Mockito.verify(anotherStateMock).enterWrapper(stateMachine)
        Mockito.verify(transitionToThirdMock).enterWrapper(stateMachine)
        Mockito.verify(anotherStateMock).update()
        Mockito.verify(transitionToThirdMock).update()
        stateMachine.update()
        Mockito.verify(anotherStateMock).exitWrapper()
        Mockito.verify(transitionToThirdMock).exitWrapper()
        Mockito.verify(aThirdStateMock).enterWrapper(stateMachine)
        Mockito.verify(aThirdStateMock).update()
    }

    @Test
    fun doesNotTransitionsStatesWhenConditionNotMet() {
        val initialStateMock = Mockito.mock(State::class.java)
        val anotherStateMock = Mockito.mock(State::class.java)
        val transitionMock = Mockito.mock(TransitionCondition::class.java)
        Mockito.`when`(transitionMock.met()).thenReturn(false)

        val stateMachine = StateMachine {
            initialState("initial")
            addState("initial", initialStateMock).withTransitions {
                to("another").whenJust(transitionMock)
            }
            addState("another", anotherStateMock)
        }

        stateMachine.update()
        Mockito.verify(initialStateMock).update()
        Mockito.verify(initialStateMock, Mockito.never()).exit()
        Mockito.verify(anotherStateMock, Mockito.never()).enter()
        Mockito.verify(anotherStateMock, Mockito.never()).update()
    }

    @Test
    fun forceTransitionOverridesTransitionConditions() {

        val initialStateMock = Mockito.mock(State::class.java)
        val anotherStateMock = Mockito.mock(State::class.java)
        val forceToMeStateMock = Mockito.mock(State::class.java)
        val transitionMock = Mockito.mock(TransitionCondition::class.java)
        Mockito.`when`(transitionMock.met()).thenReturn(true)

        val stateMachine = StateMachine {
            initialState("initial")
            addState("initial", initialStateMock).withTransitions {
                to("another").whenJust(transitionMock)
            }
            addState("another", anotherStateMock)
            addState("forceToMe", forceToMeStateMock)
        }

        stateMachine.update()
        stateMachine.transitionTo("forceToMe")
        stateMachine.update()
        Mockito.verify(anotherStateMock).exitWrapper()
        Mockito.verify(forceToMeStateMock).enterWrapper(stateMachine)
        Mockito.verify(forceToMeStateMock, Mockito.never()).exitWrapper()

    }
}

class MyStateMachine: StateMachine(){

    val myPropOverrideVal = PropOverride("originalVal")
    var myPropOverrideVar = PropOverride("originalVar")
}