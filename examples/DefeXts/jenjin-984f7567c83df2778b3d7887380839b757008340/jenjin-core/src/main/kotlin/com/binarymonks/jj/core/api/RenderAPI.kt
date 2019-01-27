package com.binarymonks.jj.core.api

import com.badlogic.gdx.graphics.glutils.ShaderProgram


interface RenderAPI {

    //TODO: This needs to hook into the game rendering layer, not the Renderworld. Other layers might not want the same ambient light
    fun setAmbientLight(r: Float, g: Float, b: Float, a: Float)

    fun setClearColor(r: Float, g: Float, b: Float, a: Float)

    fun registerShader(name: String, vertexPath: String, fragmentPath: String)
    fun getShaderPipe(shaderPipeName: String): ShaderProgram

}