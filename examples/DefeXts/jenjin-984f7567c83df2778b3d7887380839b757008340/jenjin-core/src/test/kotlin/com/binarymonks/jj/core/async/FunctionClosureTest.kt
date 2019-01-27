package com.binarymonks.jj.core.async

import com.binarymonks.jj.core.mockoutGDXinJJ
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner


interface SomeThing {
    fun someFunctionWithoutArgs()

    fun someFunctionWithArgs(aString: String, aFloat: Float)
}

@RunWith(MockitoJUnitRunner::class)
class FunctionClosureTest {
    @Mock
    lateinit var someThing: SomeThing

    @Before
    fun setUp() {
        mockoutGDXinJJ()
    }

    @Test
    fun captureFunctionWithoutArgs() {
        val capture = capture(someThing::someFunctionWithoutArgs)
        capture.call()
        Mockito.verify(someThing).someFunctionWithoutArgs()
    }

    @Test
    fun captureFunctionWithArgs() {
        val arg1 = "hello"
        val arg2 = 20f
        val capture = capture(someThing::someFunctionWithArgs) {
            withArg("aString", arg1)
            withArg("aFloat", arg2)
        }
        capture.call()
        Mockito.verify(someThing).someFunctionWithArgs(arg1, arg2)
    }
}