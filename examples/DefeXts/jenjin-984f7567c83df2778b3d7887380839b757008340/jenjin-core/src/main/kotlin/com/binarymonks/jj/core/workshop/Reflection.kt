package com.binarymonks.jj.core.workshop

import java.lang.reflect.Field

/**
 * Created by lwillmore on 07/02/17.
 */
object Reflection {

    fun getField(name: String, clazz: Class<*>): Field {
        try {
            return clazz.getField(name)
        } catch (e: NoSuchFieldException) {
            throw RuntimeException(String.format("Could not checkPools field name %s from class %s", name, clazz.canonicalName))
        }

    }


    fun <T> getFieldFromInstance(field: Field, parentObject: Any): T {
        try {
            @Suppress("UNCHECKED_CAST")
            return field.get(parentObject) as T
        } catch (e: IllegalAccessException) {
            throw RuntimeException(String.format("Could not checkPools the field value %s from class %s", field.name, parentObject.javaClass.canonicalName))
        }

    }
}
