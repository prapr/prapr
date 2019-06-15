package com.binarymonks.jj.core.components.fsm

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CompositeStateTest {

    @Mock
    lateinit var stateMock: State

    @Mock
    lateinit var machineMock: StateMachine

    @Test
    fun activeStatusUpdatedOnEnterAndExit() {
        val composite = CompositeState()
        composite.part(stateMock)

        composite.enterWrapper(machineMock)

        Mockito.verify(stateMock).enterWrapper(machineMock)

        composite.exitWrapper()

        Mockito.verify(stateMock).exitWrapper()
    }
}