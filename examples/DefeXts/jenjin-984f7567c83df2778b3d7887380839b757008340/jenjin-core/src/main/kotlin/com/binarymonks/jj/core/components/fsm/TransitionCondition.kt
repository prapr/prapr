package com.binarymonks.jj.core.components.fsm

import com.badlogic.gdx.utils.Array
import com.binarymonks.jj.core.Copyable
import com.binarymonks.jj.core.components.ComponentLifecycle
import com.binarymonks.jj.core.copy
import com.binarymonks.jj.core.scenes.Scene


abstract class TransitionCondition : Copyable<TransitionCondition>, ComponentLifecycle {

    internal open var scene: Scene? = null
    private var machine: StateMachine? = null
    var active: Boolean = false
        private set

    fun me(): Scene {
        return scene!!
    }

    fun myMachine(): StateMachine {
        if (machine == null) {
            throw Exception("I cannot see my machine yet. I can only see it after I have been activated")
        }
        return machine!!
    }

    internal open fun enterWrapper(machine: StateMachine) {
        this.machine = machine
        active = true
        enter()
    }

    internal open fun exitWrapper() {
        active = false
        exit()
    }

    abstract fun met(): Boolean


    /**
     * Called when the condition's state is entered
     */
    open fun enter() {}

    /**
     * Called when the condition's state is exited
     */
    open fun exit() {}

    override fun update() {
    }

    override fun onAddToWorld() {
    }

    override fun onRemoveFromWorld() {
    }

    override fun clone(): TransitionCondition {
        return copy(this)
    }

}


class AndTransitionCondition() : TransitionCondition() {

    var conditions: Array<TransitionCondition> = Array()


    constructor(construc: AndTransitionCondition.() -> Unit) : this() {
        this.construc()
    }

    fun <T : TransitionCondition> and(condition: T) {
        conditions.add(condition)
    }

    override fun met(): Boolean {
        if (conditions.size == 0) {
            return false
        }
        conditions.forEach {
            if (it.met() == false) {
                return false
            }
        }
        return true
    }

    override fun enterWrapper(machine: StateMachine) {
        super.enterWrapper(machine)
        conditions.forEach { it.enterWrapper(machine) }
    }

    override fun exitWrapper() {
        super.exitWrapper()
        conditions.forEach { it.exitWrapper() }
    }

    override fun update() {
        conditions.forEach { it.update() }
    }

    override fun onAddToWorld() {
        conditions.forEach { it.onAddToWorld() }
    }

    override fun onRemoveFromWorld() {
        conditions.forEach { it.onRemoveFromWorld() }
    }

}

class OrTransitionCondition() : TransitionCondition() {

    var conditions: Array<TransitionCondition> = Array()

    override var scene: Scene?
        get() = super.scene
        set(value) {
            super.scene = value
            conditions.forEach { it.scene = value }
        }

    constructor(construc: OrTransitionCondition.() -> Unit) : this() {
        this.construc()
    }

    fun <T : TransitionCondition> or(condition: T) {
        conditions.add(condition)
    }

    override fun met(): Boolean {
        if (conditions.size == 0) {
            return false
        }
        conditions.forEach {
            if (it.met() == true) {
                return true
            }
        }
        return false
    }

    override fun enterWrapper(machine: StateMachine) {
        super.enterWrapper(machine)
        conditions.forEach { it.enterWrapper(machine) }
    }

    override fun exitWrapper() {
        super.exitWrapper()
        conditions.forEach { it.exitWrapper() }
    }

    override fun update() {
        conditions.forEach { it.update() }
    }

    override fun onAddToWorld() {
        conditions.forEach { it.onAddToWorld() }
    }

    override fun onRemoveFromWorld() {
        conditions.forEach { it.onRemoveFromWorld() }
    }

}

class NotTransitionCondition() : TransitionCondition() {

    var condition: TransitionCondition? = null

    override var scene: Scene?
        get() = super.scene
        set(value) {
            super.scene = value
            condition?.scene = value
        }

    constructor(condition: TransitionCondition) : this() {
        this.condition = condition
    }

    override fun met(): Boolean {
        if (condition == null) {
            return false
        }
        return !condition!!.met()
    }

    override fun update() {
        condition!!.update()
    }

    override fun onAddToWorld() {
        condition!!.onAddToWorld()
    }

    override fun onRemoveFromWorld() {
        condition!!.onRemoveFromWorld()
    }
}

fun not(condition: TransitionCondition): TransitionCondition {
    return NotTransitionCondition(condition)
}