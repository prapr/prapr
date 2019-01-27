package com.binarymonks.jj.core.api

import com.binarymonks.jj.core.pools.PoolManager
import kotlin.reflect.KClass

/**
 * Manage your pools here
 *
 * Use [com.binarymonks.jj.core.pools.new], [com.binarymonks.jj.core.pools.recycle] and
 * [com.binarymonks.jj.core.pools.recycleItems] to create and return your pooled objects.
 */
interface PoolsAPI {

    /**
     * Register [PoolManager]s for objects that are not [com.binarymonks.jj.core.pools.Poolable]
     */
    fun <P, T : PoolManager<P>> registerManager(poolManager: T, sceneToPoolClass: Class<P>)

    /**
     * Use this if you want to dump the pooled objects.
     * They will be disposed by the PoolManager before being removed from the pool.

     * @param clazzToClear
     */
    fun clearPool(clazzToClear: Class<*>)

    /**
     * Use this if you want to dump the pooled objects.
     * They will be disposed by the PoolManager before being removed from the pool.

     * @param clazzToClear
     */
    fun clearPool(clazzToClear: KClass<*>)
}