package com.binarymonks.jj.core.properties


class StubHasProperties : HasProps {

    public val props = mutableMapOf<String, Any>()

    override fun hasProp(key: String): Boolean {
        return props.containsKey(key)
    }

    override fun getProp(key: String): Any? {
        return props.get(key)
    }
}