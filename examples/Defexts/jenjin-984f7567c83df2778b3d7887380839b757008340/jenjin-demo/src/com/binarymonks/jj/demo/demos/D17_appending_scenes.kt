package com.binarymonks.jj.demo.demos

import com.badlogic.gdx.graphics.Color
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJConfig
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.components.Component
import com.binarymonks.jj.core.specs.SceneSpec
import com.binarymonks.jj.core.specs.params


class D17_appending_scenes : JJGame(JJConfig {
    b2d.debugRender = true
    val width = 6f
    gameView.worldBoxWidth = width
    gameView.cameraPosX = 0f
    gameView.cameraPosY = 0f
    gameView.clearColor = Color(0.2f, 0.2f, 0.2f, 1f)
}) {
    override fun gameOn() {
        JJ.scenes.addSceneSpec("parent", parentScene())
        JJ.scenes.addSceneSpec("child", childScene())

        JJ.scenes.instantiate(params { x = -1f;y = 1.5f }, "parent")
        JJ.scenes.instantiate(params { x = 1f;y = -1f;rotationD = 45f }, "parent")
    }


    fun parentScene(): SceneSpec {
        return SceneSpec {
            render {
                rectangleRender(2f, 2f) {
                    color.set(Color.BLUE)
                }
            }
            component(AddSceneToSceneComponent())
        }
    }

    fun childScene(): SceneSpec {
        return SceneSpec {
            render {
                rectangleRender(1f, 1f) {
                    color.set(Color.GREEN)
                }
            }
        }
    }
}

class AddSceneToSceneComponent : Component() {

    override fun onAddToWorld() {
        JJ.clock.schedule(this::addSceneToSelf, 1f, 1)
    }


    fun addSceneToSelf() {
        me().append(params {}, "child")
        me().append(params { x = 1f;y = 1f }, "child")
        me().append(params { x = -1f;y = -1f;rotationD = 45f }, "child")
    }


}