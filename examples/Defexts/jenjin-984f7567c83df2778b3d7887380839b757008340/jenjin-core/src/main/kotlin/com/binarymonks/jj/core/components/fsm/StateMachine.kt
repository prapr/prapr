package com.binarymonks.jj.core.components.fsm

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.copy
import com.binarymonks.jj.core.properties.PropOverride
import com.binarymonks.jj.core.scenes.Scene


open class StateMachine() : State() {

    var initialState = PropOverride<String?>(null)
    var states: ObjectMap<String, State> = ObjectMap()
    var transitions: ObjectMap<String, Array<TransitionEdge>> = ObjectMap()
    private var currentState: String? = null
    private var requestedTransition: String? = null

    override var scene: Scene?
        get() = super.scene
        set(value) {
            super.scene = value
            states.forEach {
                it.value.scene = value
            }
            transitions.forEach {
                it.value.forEach {
                    it.condition?.scene = value
                }
            }
        }

    constructor(construct: StateMachineBuilder.() -> Unit) : this() {
        val builder = StateMachineBuilder(this)
        builder.construct()
    }

    fun buildStates(construct: StateMachineBuilder.() -> Unit) {
        val builder = StateMachineBuilder(this)
        builder.construct()
    }


    override fun onAddToWorld() {
        states.forEach { it.value.onAddToWorld() }
        transitions.forEach { it.value.forEach { it.condition!!.onAddToWorld() } }
    }


    override fun onRemoveFromWorld() {
        currentState = null
        states.forEach { it.value.onRemoveFromWorldWrapper() }
        transitions.forEach { it.value.forEach { it.condition!!.onRemoveFromWorld() } }
    }

    override fun update() {
        if (currentState == null) {
            if (initialState.get() == null) {
                throw Exception("You need to set an initial state")
            }
            currentState = initialState.get()
            prepCurrentState()
        }
        if (requestedTransition != null) {
            executeTransitionTo(requestedTransition!!)
            requestedTransition = null
        } else {
            for (edge in transitions.get(currentState)) {
                if (edge.condition!!.met()) {
                    executeTransitionTo(edge.toEventName!!)
                    break
                }
            }
        }
        state().update()
        for (edge in transitions.get(currentState)) {
            edge.condition!!.update()
        }
    }

    private fun executeTransitionTo(state:String){
        closeCurrentState()
        currentState=state
        prepCurrentState()
    }

    private fun prepCurrentState() {
        state().enterWrapper(this)
        for (edge in transitions.get(currentState)) {
            edge.condition!!.enterWrapper(this)
        }
    }

    private fun closeCurrentState(){
        state().exitWrapper()
        for (edge in transitions.get(currentState)) {
            edge.condition!!.exitWrapper()
        }
    }

    fun state(): State {
        return states.get(currentState)
    }

    fun transitionTo(stateName: String) {
        requestedTransition = stateName
    }
}

class TransitionEdge() {
    var condition: TransitionCondition? = null
    var toEventName: String? = null

    constructor(
            condition: TransitionCondition,
            toEventName: String
    ) : this() {
        this.condition = condition
        this.toEventName = toEventName
    }

    fun clone(): TransitionEdge {
        return copy(this)
    }
}


class StateMachineBuilder(
        val stateMachine: StateMachine
) {

    /**
     * Set the default initial state.
     */
    fun initialState(name: String) {
        stateMachine.initialState.default = name
    }

    /**
     * Set the propertyKey to override default initial state.
     */
    fun initialStateProp(propertyKey: String) {
        stateMachine.initialState.setOverride(propertyKey)
    }

    fun <T : State> addState(name: String, component: T): TransitionBuilder {
        stateMachine.states.put(name, component)
        stateMachine.transitions.put(name, Array<TransitionEdge>())
        return TransitionBuilder(name, stateMachine)
    }

    fun addComposite(name: String, build: CompositeState.() -> Unit): TransitionBuilder {
        val composite = CompositeState()
        composite.build()
        stateMachine.transitions.put(name, Array<TransitionEdge>())
        stateMachine.states.put(name, composite)
        return TransitionBuilder(name, stateMachine)
    }

}

class TransitionBuilder(
        val from: String,
        val stateMachine: StateMachine
) {

    fun withTransitions(build: TransitionBuilder.() -> Unit) {
        this.build()
    }

    fun to(stateName: String): TransitionBuilderTo {

        return TransitionBuilderTo(from, stateName, stateMachine)
    }
}

class TransitionBuilderTo(
        val from: String,
        val to: String,
        val stateMachine: StateMachine
) {

    fun <T : TransitionCondition> whenJust(whenCondition: T) {
        stateMachine.transitions.get(from).add(TransitionEdge(whenCondition, to))
    }

    fun <T : TransitionCondition> whenNot(whenCondition: T) {
        stateMachine.transitions.get(from).add(TransitionEdge(not(whenCondition), to))
    }

    fun whenAll(build: AndTransitionCondition.() -> Unit) {
        val and = AndTransitionCondition()
        and.build()
        stateMachine.transitions.get(from).add(TransitionEdge(and, to))
    }

    fun whenAny(build: OrTransitionCondition.() -> Unit) {
        val or = OrTransitionCondition()
        or.build()
        stateMachine.transitions.get(from).add(TransitionEdge(or, to))
    }


}