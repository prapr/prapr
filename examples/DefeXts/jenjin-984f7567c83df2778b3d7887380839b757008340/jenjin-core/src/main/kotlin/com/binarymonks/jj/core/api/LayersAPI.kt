package com.binarymonks.jj.core.api


import com.binarymonks.jj.core.layers.Layer

interface LayersAPI {

    fun push(layer: Layer)
    fun push(layerName: String)
    fun pop()

    fun registerLayer(layerName: String, layer: Layer)
    fun <T : Layer> getLayer(layerName: String): T
}