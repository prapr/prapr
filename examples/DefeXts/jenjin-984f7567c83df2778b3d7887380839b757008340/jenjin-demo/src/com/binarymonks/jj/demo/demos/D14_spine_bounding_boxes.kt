package com.binarymonks.jj.demo.demos

import com.badlogic.gdx.graphics.Color
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJConfig
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.specs.SceneSpec
import com.binarymonks.jj.core.specs.SceneSpecRef
import com.binarymonks.jj.spine.specs.SpineSpec


class D14_spine_bounding_boxes : JJGame(JJConfig {
    b2d.debugRender = true
    val width = 6f
    gameView.worldBoxWidth = width
    gameView.cameraPosX = 0f
    gameView.cameraPosY = 2f
    gameView.clearColor = Color(0.5f, 0.7f, 1f, 1f)
}) {

    override fun gameOn() {
        JJ.scenes.addSceneSpec("spineDummy", spine_with_bounding_boxes())
        JJ.scenes.loadAssetsNow()

        JJ.scenes.instantiate(SceneSpec {
            nodeRef ( "spineDummy" )
        })

    }

    fun spine_with_bounding_boxes(): SceneSpecRef {
        return SpineSpec {
            spineRender {
                atlasPath = "spine/male_base.atlas"
                dataPath = "spine/male_base.json"
                scale = 1.5f / 103f
            }
            skeleton {
                boundingBoxes = true
            }
        }
    }
}