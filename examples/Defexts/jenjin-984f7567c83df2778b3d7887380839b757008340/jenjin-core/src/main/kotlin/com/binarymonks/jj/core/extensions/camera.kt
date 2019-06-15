package com.binarymonks.jj.core.extensions

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.binarymonks.jj.core.pools.recycle
import com.binarymonks.jj.core.pools.vec3

fun OrthographicCamera.unproject(vector2: Vector2) {
    val v3 = vec3(vector2.x, vector2.y, 0f)
    unproject(v3)
    vector2.set(v3.x, v3.y)
    recycle(v3)
}


