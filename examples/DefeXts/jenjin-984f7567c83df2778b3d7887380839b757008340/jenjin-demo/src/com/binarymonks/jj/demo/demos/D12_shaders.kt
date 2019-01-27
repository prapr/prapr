package com.binarymonks.jj.demo.demos

import com.badlogic.gdx.graphics.Color
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJConfig
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.render.ShaderSpec
import com.binarymonks.jj.core.specs.SceneSpec
import com.binarymonks.jj.core.specs.SceneSpecRef
import com.binarymonks.jj.core.specs.params


class D12_shaders : JJGame(JJConfig {
    gameView.worldBoxWidth = 20f
    gameView.cameraPosX = 0f
    gameView.cameraPosY = 0f
}) {
    override fun gameOn() {

        JJ.scenes.addSceneSpec("linearLightCube", linearLightCube())
        JJ.scenes.addSceneSpec("normalCube", normalCube())
        JJ.scenes.addSceneSpec("pixelPolyCube", pixelPoyCube())
        JJ.scenes.addSceneSpec("pixelCubeLarge", pixelCubeLarge())
        JJ.scenes.addSceneSpec("pixelCubeSmall", pixelCubeSmall())
        JJ.scenes.addSceneSpec("normalCircle", normalCircle())
        JJ.scenes.addSceneSpec("checkeredCircle", checkeredCircle())
        JJ.scenes.loadAssetsNow()

        JJ.render.registerShader(
                name = "linearLight",
                vertexPath = "shaders/default_vertex.glsl",
                fragmentPath = "shaders/linear_light_fragment.glsl"
        )

        JJ.render.registerShader(
                name = "checkeredBatch",
                vertexPath = "shaders/default_vertex.glsl",
                fragmentPath = "shaders/checkered_batch_fragment.glsl"
        )

        JJ.render.registerShader(
                name = "pixels",
                vertexPath = "shaders/default_vertex.glsl",
                fragmentPath = "shaders/pixel_fragment.glsl"
        )

        JJ.render.registerShader(
                name = "paramPixels",
                vertexPath = "shaders/default_vertex.glsl",
                fragmentPath = "shaders/pixel_params_fragment.glsl"
        )

        //Shape shaders are a bit different to texture based shaders ie, no texture
        JJ.render.registerShader(
                name = "checkeredShape",
                vertexPath = "shaders/checkered_shape_vertex.glsl",
                fragmentPath = "shaders/checkered_shape_fragment.glsl"
        )



        JJ.scenes.instantiate(SceneSpec {
            nodeRef(params { x = -5f; y = 5f;prop("color", Color.BLUE) }, "linearLightCube")
            nodeRef(params { x = 0f; y = 5f; rotationD = 45f; prop("color", Color.GREEN) }, "pixelPolyCube")
            nodeRef(params { x = 5f; y = 5f;prop("color", Color.ORANGE) }, "linearLightCube")

            nodeRef(params { x = -5f; y = 0f;prop("color", Color.BLUE) }, "pixelCubeLarge")
            nodeRef(params { x = 0f; y = 0f;prop("color", Color.GREEN) }, "checkeredCircle")
            nodeRef(params { x = 5f; y = 0f;prop("color", Color.ORANGE) }, "pixelCubeSmall")

            nodeRef(params { x = -5f; y = -5f;prop("color", Color.BLUE) }, "normalCube")
            nodeRef(params { x = 0f; y = -5f;prop("color", Color.GREEN) }, "normalCircle")
            nodeRef(params { x = 5f; y = -5f;prop("color", Color.ORANGE) }, "normalCube")
        })

    }

    private fun pixelPoyCube(): SceneSpecRef {
        return SceneSpec {
            render {
                rectangleRender(3f, 3f) {
                    shaderSpec = ShaderSpec("checkeredBatch")
                    color.setOverride("color")
                }
            }
        }
    }

    private fun checkeredCircle(): SceneSpecRef {
        return SceneSpec {
            render {
                circleRender(1.5f) {
                    color.setOverride("color")
                    shaderSpec = ShaderSpec("checkeredShape")
                }
            }
        }
    }

    private fun normalCircle(): SceneSpecRef {
        return SceneSpec {
            render {
                circleRender(1.5f) {
                    color.setOverride("color")
                }
            }
        }
    }

    private fun pixelCubeSmall(): SceneSpecRef {
        return SceneSpec {
            render {
                imageTexture("textures/linear_light_square.png") {
                    width = 3f
                    height = 3f
                    shaderSpec = ShaderSpec("paramPixels") {
                        floatBinding("dx", 64f * (1f / 1024f))
                        floatBinding("dy", 64f * (1f / 1024f))
                    }
                    color.setOverride("color")
                }
            }
        }
    }

    private fun pixelCubeLarge(): SceneSpecRef {
        return SceneSpec {
            render {
                imageTexture("textures/linear_light_square.png") {
                    width = 3f
                    height = 3f
                    shaderSpec = ShaderSpec("paramPixels") {
                        floatBinding("dx", 128f * (1f / 1024f))
                        floatBinding("dy", 128f * (1f / 1024f))
                    }
                    color.setOverride("color")
                }
            }
        }
    }

    private fun normalCube(): SceneSpecRef {
        return SceneSpec {
            render {
                imageTexture("textures/linear_light_square.png") {
                    width = 3f
                    height = 3f
                    color.setOverride("color")
                }
            }
        }
    }

    private fun linearLightCube(): SceneSpecRef {
        return SceneSpec {
            render {
                imageTexture("textures/linear_light_square.png") {
                    width = 3f
                    height = 3f
                    shaderSpec = ShaderSpec("linearLight")
                    color.setOverride("color")
                }
            }
        }
    }
}

