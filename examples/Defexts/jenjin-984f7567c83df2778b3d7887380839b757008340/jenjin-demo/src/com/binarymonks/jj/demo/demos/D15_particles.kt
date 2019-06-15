package com.binarymonks.jj.demo.demos

import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJConfig
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.specs.SceneSpec


class D15_particles : JJGame(JJConfig {
    b2d.debugRender = true

    gameView.worldBoxWidth = 30f
    gameView.cameraPosX = 0f
    gameView.cameraPosY = 0f
}) {
    override fun gameOn() {

        JJ.scenes.addSceneSpec("nestedParticles", nestedParticles())

        JJ.scenes.instantiate("nestedParticles")

    }


    fun nestedParticles(): SceneSpec {
        return SceneSpec {
            render {
                particles("particles/jet_plume.p") {
                    imageDir = "particles"
                    scale = 0.5f
                    rotationD = 90f
                }
//                circleRender(0.5f)
            }
        }
    }
}