package com.binarymonks.jj.core.api

import kotlin.reflect.KClass


interface AssetsAPI {
    /**
     * Provides access to asset management. Mostly backed by a libGDX AssetManager.
     *
     * Generally though, if you have added SceneSpecs to [com.binarymonks.jj.core.JJ.scenes],
     * then if you try to instantiate the scene, the assets will be loaded, this is here to do pre loading of
     * assets that are not mentioned in your [com.binarymonks.jj.core.specs.SceneSpec]s.
     */

    /**
     * A synchronous load of an asset. When you want to use it right now.
     */
    fun loadNow(path: String, assetClass: KClass<*>)

    /**
     * An asynchronous load of an asset.
     */
    fun load(path: String, assetClass: KClass<*>)

    /**
     * Get your asset.
     */
    fun <T : Any> getAsset(path: String, assetClass: KClass<T>): T

}