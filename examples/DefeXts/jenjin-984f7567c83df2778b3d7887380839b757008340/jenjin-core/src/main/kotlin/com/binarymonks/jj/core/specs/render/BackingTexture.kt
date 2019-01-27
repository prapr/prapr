package com.binarymonks.jj.core.specs.render

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.assets.AssetReference
import com.binarymonks.jj.core.render.nodes.TextureFrameProvider
import com.binarymonks.jj.core.render.nodes.FrameProvider

abstract class BackingTexture : Json.Serializable {
    var path: String? = null

    constructor()

    constructor(path: String) {
        this.path = path
    }

    abstract fun provider(): FrameProvider

    abstract fun assets(): Array<AssetReference>


    override fun toString(): String {
        return "BackingTexture{" +
                "path='" + path + '\'' +
                '}'
    }
}

class SimpleImage : BackingTexture {

    constructor() : super()

    constructor(path: String) : super(path)

    override fun provider(): FrameProvider {
        if (path == null) {
            throw Exception("No Path set in backing image")
        }
        return TextureFrameProvider(JJ.B.assets.getAsset(path!!, Texture::class))
    }

    override fun assets(): Array<AssetReference> {
        val assets: Array<AssetReference> = Array()
        if(path !=null){
            assets.add(AssetReference(Texture::class, path!!))
        }
        return assets
    }


    override fun write(json: Json) {
        json.writeValue("path", path)
    }

    override fun read(json: Json, jsonData: JsonValue) {
        this.path = jsonData.getString("path")
    }
}
