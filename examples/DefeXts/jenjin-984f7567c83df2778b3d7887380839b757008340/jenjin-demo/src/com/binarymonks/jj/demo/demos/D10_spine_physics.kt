package com.binarymonks.jj.demo.demos

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.physics.box2d.BodyDef
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJConfig
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.pools.vec2
import com.binarymonks.jj.core.specs.*
import com.binarymonks.jj.core.specs.physics.CollisionGroupSpecName
import com.binarymonks.jj.spine.collisions.TriggerRagDollCollision
import com.binarymonks.jj.spine.specs.SpineSpec


class D10_spine_physics : JJGame(JJConfig{
    b2d.debugRender = true

    gameView.worldBoxWidth = 15f
    gameView.cameraPosX = 0f
    gameView.cameraPosY = 0f
}) {

    override fun gameOn() {
        JJ.physics.collisionGroups.buildGroups {
            group("character").collidesWith("hammer", "terrain")
            group("hammer").collidesWith("character")
            group("terrain").collidesWith("character")
        }

        JJ.scenes.addSceneSpec("spineBoy", spineBoy())
        JJ.scenes.addSceneSpec("hammer", swingHammer())
        JJ.scenes.addSceneSpec("terrain", terrain())
        JJ.scenes.loadAssetsNow()

        JJ.scenes.instantiate(SceneSpec {
            nodeRef(params { x = 4f; y = -0.4f }, "spineBoy" )
            nodeRef(params { x = 7f; y = 4f; rotationD = 90f }, "hammer" )
            nodeRef(params { y = -2f; scaleX = 13f; name = "floor" }, "terrain" )
        })
    }

    fun spineBoy(): SceneSpecRef {
        return SpineSpec {
            spineRender {
                atlasPath = "spine/spineboy/spineboy-pma.atlas"
                dataPath = "spine/spineboy/spineboy.json"
                scale = 1f / 247f
                originY = 247f
            }
            animations {
                startingAnimation = "walk"
            }
            skeleton {
                all.collisionGroup = CollisionGroupSpecName("character")
                all.collisions.begin(TriggerRagDollCollision())
                customize("head") {
                    override {
                        shape = Circle(0.4f)
                    }
                }
            }
        }
    }

    private fun swingHammer(): SceneSpec {
        return SceneSpec {
            node(params { name = "hammerAnchor" }) {
                physics { bodyType = BodyDef.BodyType.StaticBody }
            }
            node(params { name = "hammer" }) {
                physics {
                    bodyType = BodyDef.BodyType.DynamicBody
                    fixture { shape = Rectangle(0.5f, 3f); offsetY = -1.5f; collsionGroup("hammer") }
                    fixture { shape = Rectangle(2f, 2f); offsetY = -4f; collsionGroup("hammer") }
                    fixture { shape = Circle(1f); offsetY = -4f; offsetX = -1f; collsionGroup("hammer") }
                    fixture { shape = Circle(1f); offsetY = -4f; offsetX = 1f; collsionGroup("hammer") }
                }
                render {
                    rectangleRender(0.5f, 3f) { color.set(Color.BROWN); offsetY = -1.5f }
                    rectangleRender(2f, 2f) { color.set(Color.GRAY); offsetY = -4f }
                    circleRender(1f) { color.set(Color.GRAY); offsetY = -4f; offsetX = -1f }
                    circleRender(1f) { color.set(Color.GRAY); offsetY = -4f; offsetX = 1f }
                    circleRender(0.25f) { color.set(Color.GREEN); }
                }
            }
            revJoint("hammer", "hammerAnchor", vec2()) {}
        }
    }

    fun terrain(): SceneSpec {
        return SceneSpec {
            physics {
                bodyType = BodyDef.BodyType.StaticBody
                fixture {
                    shape = Rectangle(1f, 1f)
                    friction = 0.5f
                    collsionGroup("terrain")
                }
            }
            render {
                rectangleRender(1f, 1f) { color.set(Color.GREEN) }
            }
        }
    }

}

object MyConfig10 {
    var jjConfig: JJConfig = JJConfig()

    init {

    }
}


