package com.binarymonks.jj.core.components.fsm

import org.junit.Assert
import org.junit.Test


class AndTransitionConditionTest {


    @Test
    fun trueWhenAllTrue() {
        Assert.assertTrue(AndTransitionCondition {
            and(AlwaysTrueCondition())
            and(AlwaysTrueCondition())
            and(AlwaysTrueCondition())
        }.met())
    }

    @Test
    fun falseWhenAnyFalse() {
        Assert.assertFalse(AndTransitionCondition {
            and(AlwaysTrueCondition())
            and(AlwaysTrueCondition())
            and(AlwaysFalseCondition())
        }.met())
    }

    @Test
    fun falseWhenNone() {
        Assert.assertFalse(AndTransitionCondition().met())
    }

}

class OrTransitionConditionTest {


    @Test
    fun trueWhenAnyTrue() {
        Assert.assertTrue(OrTransitionCondition {
            or(AlwaysFalseCondition())
            or(AlwaysFalseCondition())
            or(AlwaysTrueCondition())
        }.met())
    }

    @Test
    fun falseWhenNoneFalse() {
        Assert.assertFalse(OrTransitionCondition {
            or(AlwaysFalseCondition())
            or(AlwaysFalseCondition())
            or(AlwaysFalseCondition())
        }.met())
    }

    @Test
    fun falseWhenNone() {
        Assert.assertFalse(OrTransitionCondition().met())
    }

}

class NotConditionTest {

    @Test
    fun trueWhenFalse() {
        Assert.assertTrue(NotTransitionCondition(AlwaysFalseCondition()).met())
    }

    @Test
    fun falseWhenTrue() {
        Assert.assertFalse(NotTransitionCondition(AlwaysTrueCondition()).met())
    }

    @Test
    fun falseWhenNone() {
        Assert.assertFalse(NotTransitionCondition().met())
    }
}