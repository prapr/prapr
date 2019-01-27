package com.binarymonks.jj.demo.demos

import com.badlogic.gdx.graphics.Color
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJConfig
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.audio.SoundMode
import com.binarymonks.jj.core.components.misc.Emitter
import com.binarymonks.jj.core.components.misc.SelfDestruct
import com.binarymonks.jj.core.specs.SceneSpec
import com.binarymonks.jj.core.specs.SceneSpecRef
import com.binarymonks.jj.core.specs.params
import com.binarymonks.jj.spine.specs.SpineSpec


class D16_spine_pooling : JJGame(JJConfig {
    b2d.debugRender = true
    val width = 6f
    gameView.worldBoxWidth = width
    gameView.cameraPosX = 0f
    gameView.cameraPosY = 2f
    gameView.clearColor = Color(0.2f, 0.2f, 0.2f, 1f)
}) {

    override fun gameOn() {
        JJ.scenes.addSceneSpec("spineBounding", spine_with_bounding_boxes())
        JJ.scenes.addSceneSpec("spineAnimation", spine_with_animations())
        JJ.scenes.addSceneSpec("spawnBounding", spawnerBounding())
        JJ.scenes.addSceneSpec("spawnAnimation", spawnerAnimated())
        JJ.scenes.loadAssetsNow()

        JJ.scenes.instantiate(SceneSpec {
            container = true
            nodeRef(params { x = -2f }, "spawnBounding")
            nodeRef(params { x = 2f }, "spawnAnimation")
        })

    }

    fun spawnerBounding(): SceneSpecRef {
        return SceneSpec {
            component(Emitter()) {
                setSpec("spineBounding")
                delayMinSeconds = 3f
                delayMaxSeconds = 3f
                repeat = 0
            }
        }
    }

    fun spawnerAnimated(): SceneSpecRef {
        return SceneSpec {
            component(Emitter()) {
                setSpec("spineAnimation")
                delayMinSeconds = 3f
                delayMaxSeconds = 3f
                repeat = 0
            }
        }
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
            rootScene {
                component(SelfDestruct()) {
                    delaySeconds = 2f
                }
            }
        }
    }

    fun spine_with_animations(): SceneSpecRef {
        return SpineSpec {
            spineRender {
                atlasPath = "spine/spineboy/spineboy-pma.atlas"
                dataPath = "spine/spineboy/spineboy.json"
                scale = 1 / 247f
                originY = 247f
            }
            animations {
                startingAnimation = "run"
                registerEventHandler("footstep", { component, _ ->
                    component.me().soundEffects.triggerSound("footstep", SoundMode.NORMAL)
                })
                registerEventHandler("footstep", SpineBoyComponent::class, SpineBoyComponent::onEvent)
                registerEventFunction("footstep", SpineBoyComponent::class, SpineBoyComponent::step)
            }
            rootScene {
                component(SpineBoyComponent())
                component(SelfDestruct()) {
                    delaySeconds = 2f
                }
                sounds.sound("footstep", "sounds/step.mp3")
            }
        }
    }
}