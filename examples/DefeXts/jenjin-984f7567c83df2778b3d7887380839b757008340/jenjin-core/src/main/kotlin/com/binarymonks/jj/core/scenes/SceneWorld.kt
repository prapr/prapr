package com.binarymonks.jj.core.scenes

import com.badlogic.gdx.utils.ObjectMap

class SceneWorld {

    internal var rootScene: Scene? = null
    internal var namedScenes = ObjectMap<String, Scene>(10)
    internal var inUpdate = false

    fun getSceneByUniqueName(uniqueName: String): Scene {
        return namedScenes.get(uniqueName)
    }

    fun update() {
        inUpdate = true
        rootScene?.update()
        inUpdate = false
    }
}
