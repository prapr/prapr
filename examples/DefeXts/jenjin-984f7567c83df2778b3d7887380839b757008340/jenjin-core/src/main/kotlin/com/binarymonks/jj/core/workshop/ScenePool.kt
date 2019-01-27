package com.binarymonks.jj.core.workshop

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.scenes.Scene


class ScenePool {

    private var sceneByXandYandID: ObjectMap<String, Array<Scene>> = ObjectMap()

    fun get(scaleX: Float, scaleY: Float, sceneSpecID: Int): Scene? {
        val key = key(scaleX, scaleY, sceneSpecID)
        if (sceneByXandYandID.containsKey(key))
            if (sceneByXandYandID.get(key).size > 0)
                return sceneByXandYandID.get(key).pop()
        return null
    }

    fun put(scaleX: Float, scaleY: Float, sceneSpecID: Int, scene: Scene) {
        val key: String = key(scaleX, scaleY, sceneSpecID)
        if (!sceneByXandYandID.containsKey(key)) {
            sceneByXandYandID.put(key, Array())
        }
        sceneByXandYandID.get(key).add(scene)
    }

    private fun key(scaleX: Float, scaleY: Float, sceneSpecID: Int): String {
        return "$scaleX|$scaleY|$sceneSpecID"
    }

}