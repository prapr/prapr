package com.binarymonks.jj.demo.demos

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.physics.box2d.BodyDef
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJConfig
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.pools.vec2
import com.binarymonks.jj.core.specs.*

/**
 * A SceneNode's transforms (translation, rotation, scale) operate in the space of the me.
 * All the way up to the global b2d world space.
 */
class D01_nested_transforms : JJGame(JJConfig {
    b2d.debugRender = true

    gameView.worldBoxWidth = 50f
    gameView.cameraPosX = 0f
    gameView.cameraPosY = 0f
}) {

    public override fun gameOn() {
        JJ.scenes.addSceneSpec("nestedCircles", nestedCircles())
        JJ.scenes.addSceneSpec("nestedRectangles", nestedRectangles())
        JJ.scenes.addSceneSpec("nestedPolygons", nestedPolygons())
        JJ.scenes.addSceneSpec("nestedFixtureTransforms", nested())
        JJ.scenes.addSceneSpec("flatFixtureTransforms", flat())
        JJ.scenes.loadAssetsNow()

        val initialSceneSpec = SceneSpec {
            nodeRef(params { x = 10f;y = 6f }, "nestedCircles")
            nodeRef(params { x = 20f;y = 6f; scaleX = 0.5f; scaleY = 0.5f }, "nestedCircles")
            nodeRef(params { x = -8f; y = 6f }, "nestedRectangles")
            nodeRef(params { x = -18f; y = 6f; scaleX = 0.5f; scaleY = 0.5f }, "nestedRectangles")
            nodeRef(params { x = 10f; y = -15f }, "nestedPolygons")
            nodeRef(params { x = 20f; y = -15f; scaleX = 0.5f; scaleY = 0.5f }, "nestedPolygons")
            nodeRef(params { x = -12f; y = -8f }, "nestedFixtureTransforms")
            nodeRef(params { x = -12f; y = -15f }, "flatFixtureTransforms")
        }

        JJ.scenes.instantiate(initialSceneSpec)
    }

    private fun nestedCircles(): SceneSpec {
        val nestedParams = params { x = 4.1f; y = 4.1f; scaleX = 0.5f; scaleY = 0.5f; rotationD = 45f }
        val nestedParams2 = params { x = -4.1f; y = 4.1f; scaleX = 0.5f; scaleY = 0.5f; rotationD = -45f }
        return SceneSpec {
            physics {
                bodyType = BodyDef.BodyType.StaticBody
                fixture {
                    shape = Circle(4f)
                }
            }
            render {
                circleRender(4f) {
                    color.set(Color.PURPLE)
                }
            }
            node(nestedParams) {
                physics {
                    bodyType = BodyDef.BodyType.StaticBody
                    fixture {
                        shape = Circle(4f)
                    }
                }
                render {
                    circleRender(4f) {
                        color.set(Color.PURPLE)
                    }
                }
                node(nestedParams) {
                    physics {
                        bodyType = BodyDef.BodyType.StaticBody
                        fixture {
                            shape = Circle(4f)
                        }
                    }
                    render {
                        circleRender(4f) {
                            color.set(Color.PURPLE)
                        }
                    }
                    node(nestedParams) {
                        physics {
                            bodyType = BodyDef.BodyType.StaticBody
                            fixture {
                                shape = Circle(4f)
                            }
                        }
                        render {
                            circleRender(4f) {
                                color.set(Color.PURPLE)
                            }
                        }
                    }
                }
            }
            node(nestedParams2) {
                physics {
                    bodyType = BodyDef.BodyType.StaticBody
                    fixture {
                        shape = Circle(4f)
                    }
                }
                render {
                    circleRender(4f) {
                        color.set(Color.PURPLE)
                    }
                }
                node(nestedParams2) {
                    physics {
                        bodyType = BodyDef.BodyType.StaticBody
                        fixture {
                            shape = Circle(4f)
                        }
                    }
                    render {
                        circleRender(4f) {
                            color.set(Color.PURPLE)
                        }
                    }
                    node(nestedParams2) {
                        physics {
                            bodyType = BodyDef.BodyType.StaticBody
                            fixture {
                                shape = Circle(4f)
                            }
                        }
                        render {
                            circleRender(4f) {
                                color.set(Color.PURPLE)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun nestedRectangles(): SceneSpec {
        val nestedParams = params { x = 4.1f; y = 4.1f; scaleX = 0.5f; scaleY = 0.75f; rotationD = 45f }
        val imageWidth = 8f
        val imageheight = 8f
        val rectangle = Rectangle(imageWidth, imageheight)
        return SceneSpec {
            physics {
                bodyType = BodyDef.BodyType.StaticBody
                fixture {
                    shape = rectangle
                }
                render {
                    imageTexture("textures/binarymonk.png") { width = imageWidth; height = imageheight }
                }
            }
            node(nestedParams) {
                physics {
                    bodyType = BodyDef.BodyType.StaticBody
                    fixture {
                        shape = rectangle
                    }
                    render {
                        imageTexture("textures/binarymonk.png") { width = imageWidth; height = imageheight }
                    }
                }
                node(nestedParams) {
                    physics {
                        bodyType = BodyDef.BodyType.StaticBody
                        fixture {
                            shape = rectangle
                        }
                        render {
                            imageTexture("textures/binarymonk.png") { width = imageWidth; height = imageheight }
                        }
                    }
                    node(nestedParams) {
                        physics {
                            bodyType = BodyDef.BodyType.StaticBody
                            fixture {
                                shape = rectangle
                            }
                            render {
                                imageTexture("textures/binarymonk.png") { width = imageWidth; height = imageheight }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun nestedPolygons(): SceneSpec {
        val nestedParams = params { x = 4.1f; y = 4.1f; scaleX = 0.5f; scaleY = 0.75f; rotationD = 45f }
        val polygonTriangle = Polygon(
                vec2(0f, 8f),
                vec2(-4f, 0f),
                vec2(4f, 0f)
        )
        return SceneSpec {
            physics {
                bodyType = BodyDef.BodyType.StaticBody
                fixture {
                    shape = polygonTriangle
                }
            }
            render {
                polygonRender(polygonTriangle.vertices) {
                    color.set(Color.CYAN)
                }
            }
            node(nestedParams) {
                physics {
                    bodyType = BodyDef.BodyType.StaticBody
                    fixture {
                        shape = polygonTriangle
                    }
                }
                render {
                    polygonRender(polygonTriangle.vertices) {
                        color.set(Color.CYAN)
                    }
                }
                node(nestedParams) {
                    physics {
                        bodyType = BodyDef.BodyType.StaticBody
                        fixture {
                            shape = polygonTriangle
                        }
                    }
                    render {
                        polygonRender(polygonTriangle.vertices) {
                            color.set(Color.CYAN)
                        }
                    }
                    node(nestedParams) {
                        physics {
                            bodyType = BodyDef.BodyType.StaticBody
                            fixture {
                                shape = polygonTriangle
                            }
                        }
                        render {
                            polygonRender(polygonTriangle.vertices) {
                                color.set(Color.CYAN)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun flat(): SceneSpec {
        return SceneSpec {
            node {
                physics {
                    bodyType = BodyDef.BodyType.StaticBody
                    fixture {
                        shape = poly
                    }
                }
                render {
                    polygonRender(poly.vertices) {
                        color.set(Color.RED)
                    }
                }
            }
            node(params { x = 5f }) {
                physics {
                    bodyType = BodyDef.BodyType.StaticBody
                    fixture {
                        rotationD = 30f
                        shape = poly
                    }
                }
                render {
                    polygonRender(poly.vertices) {
                        rotationD = 30f
                        color.set(Color.RED)
                    }
                }
            }
        }
    }

    private fun nested(): SceneSpec {
        return SceneSpec {
            physics {
                bodyType = BodyDef.BodyType.StaticBody
                fixture {
                    shape = poly
                }
            }
            render {
                polygonRender(poly.vertices) {
                    color.set(Color.RED)
                }
            }
            node(params { scaleX = 0.5f; scaleY = 0.5f }) {
                physics {
                    bodyType = BodyDef.BodyType.StaticBody
                    fixture {
                        offsetX = 10f
                        rotationD = 30f
                        shape = poly2
                    }
                }
                render {
                    polygonRender(poly2.vertices) {
                        offsetX = 10f
                        rotationD = 30f
                        color.set(Color.RED)
                    }
                }
            }
        }
    }
}

val poly = Polygon(
        vec2(-2f, 0f),
        vec2(-1.5f, 1.5f),
        vec2(0f, 2f),
        vec2(1.5f, 1.5f),
        vec2(2f, 0f),
        vec2(1.5f, -1.5f),
        vec2(0f, -2f),
        vec2(-1.5f, -1.5f)
)
val poly2 = Polygon(
        vec2(-4f, 0f),
        vec2(-3f, 3f),
        vec2(0f, 4f),
        vec2(3f, 3f),
        vec2(4f, 0f),
        vec2(3f, -3f),
        vec2(0f, -4f),
        vec2(-3f, -3f)
)
