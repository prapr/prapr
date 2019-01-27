package com.binarymonks.jj.core.components.fsm


class AlwaysTrueCondition : TransitionCondition() {

    override fun met(): Boolean {
        return true
    }
}

class AlwaysFalseCondition : TransitionCondition() {
    override fun met(): Boolean {
        return false
    }
}