package com.binarymonks.jj.core.properties


interface HasProps {
    fun hasProp(key: String): Boolean
    fun getProp(key: String): Any?
}