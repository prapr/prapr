package com.binarymonks.jj.core.render

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.render.nodes.RenderNode
import com.binarymonks.jj.core.specs.render.DefaultRenderGraph
import com.binarymonks.jj.core.specs.render.LightRenderGraph
import com.binarymonks.jj.core.scenes.Scene

private var nodeCompare: Comparator<RenderNode> = Comparator { renderNode1: RenderNode, renderNode2: RenderNode -> renderNode1.priority - renderNode2.priority }

class RenderRoot(var specID: Int) {
    internal var parent: Scene? = null
        set(value) {
            field = value
            defaultRenderNodes.forEach { it.parent = value }
            lightRenderNodes.forEach { it.parent = value }
        }
    internal var defaultRenderNodes: Array<RenderNode> = Array()
    internal var lightRenderNodes: Array<RenderNode> = Array()
    internal var namedNodes: ObjectMap<String, RenderNode> = ObjectMap()

    fun addNode(node: RenderNode) {
        when (node.renderGraphType) {
            is DefaultRenderGraph -> {
                defaultRenderNodes.add(node);defaultRenderNodes.sort(nodeCompare)
            }
            is LightRenderGraph -> {
                lightRenderNodes.add(node);lightRenderNodes.sort(nodeCompare)
            }
            else -> throw Exception("unknown graph id")
        }
        if (node.name != null) {
            namedNodes.put(node.name, node)
        }
        node.parent = parent
    }

    fun getNode(name: String): RenderNode? {
        return namedNodes.get(name)
    }

    internal fun destroy(pooled: Boolean) {
        if (!pooled) {
            defaultRenderNodes.forEach { it.dispose() }
            lightRenderNodes.forEach { it.dispose() }
        }
    }

}