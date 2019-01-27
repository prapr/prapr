package com.binarymonks.jj.core.components.fsm

import com.badlogic.gdx.utils.Array
import com.binarymonks.jj.core.components.Component
import com.binarymonks.jj.core.scenes.Scene


abstract class State : Component() {

    private var machine: StateMachine? = null

    var active = false
        internal set

    internal open fun enterWrapper(machine: StateMachine) {
        this.machine=machine
        active = true
        enter()
    }

    internal open fun exitWrapper() {
        active = false
        exit()
    }

    fun myMachine(): StateMachine{
        if(machine == null){
            throw Exception("I cannot see my machine yet. I can only see it after I have been activated")
        }
        return machine!!
    }


    /**
     * Called when the state is entered
     */
    open fun enter() {}

    /**
     * Called when the state is exited
     */
    open fun exit() {}

}


class CompositeState : State() {

    var states: Array<State> = Array()

    override fun enterWrapper(machine: StateMachine) {
        super.enterWrapper(machine)
        states.forEach { it.enterWrapper(machine) }
    }

    override fun exitWrapper() {
        super.exitWrapper()
        states.forEach { it.exitWrapper() }
    }

    override var scene: Scene?
        get() = super.scene
        set(value) {
            super.scene = value
            states.forEach { it.scene = value }
        }


    override fun onAddToWorld() {
        states.forEach { it.onAddToWorldWrapper() }
    }


    override fun onRemoveFromWorld() {
        states.forEach { it.onRemoveFromWorldWrapper() }
    }

    fun <T : State> part(component: T, build: (T.() -> Unit)? = null) {
        if (build != null)
            component.build()
        states.add(component)
    }

}