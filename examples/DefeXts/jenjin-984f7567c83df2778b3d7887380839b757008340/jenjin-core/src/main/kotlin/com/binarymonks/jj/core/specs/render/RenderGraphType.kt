package com.binarymonks.jj.core.specs.render


interface RenderGraphType {
    companion object{
        val DEFAULT: RenderGraphType = DefaultRenderGraph()
        val LIGHT : RenderGraphType = LightRenderGraph()
    }
}

internal class DefaultRenderGraph : RenderGraphType

internal class LightRenderGraph : RenderGraphType
