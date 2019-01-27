package com.binarymonks.jj.core.components


interface ComponentLifecycle {
    /**
     * This will be called on every game loop. Override this for ongoing tasks
     */
    fun update()

    /**
     * Called when the Component's parent Scene is added to the world,
     * or when the Component is added to a Scene that is already in the world.
     * The whole scene graph for that loop of the game cycle will be complete.
     */
    fun onAddToWorld()

    /**
     * Called when the Component is removed from the scene,
     * or when the Scene is removed from the world.
     * This will also be called before being added to a pool (If the Scene is pooled)
     */
    fun onRemoveFromWorld()
}