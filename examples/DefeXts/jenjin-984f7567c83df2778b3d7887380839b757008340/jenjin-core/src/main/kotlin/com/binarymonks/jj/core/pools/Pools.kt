package com.binarymonks.jj.core.pools


import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.api.PoolsAPI
import com.binarymonks.jj.core.pools.managers.Matrix3PoolManager
import com.binarymonks.jj.core.pools.managers.Vector2PoolManager
import com.binarymonks.jj.core.pools.managers.Vector3PoolManager
import com.binarymonks.jj.core.specs.InstanceParams
import com.binarymonks.jj.core.specs.ParamsPoolManager
import kotlin.reflect.KClass

/**
 * Get a new instance of a pooled class
 *
 * @param pooledClass the class of the object that is pooled
 * *
 * @return an instance of the pooled class
 **/
fun <T> new(clazz: Class<T>): T {
    return JJ.B.pools.nuw(clazz)
}

/**
 * Get a new instance of a pooled class
 *
 * @param pooledClass the class of the object that is pooled
 * *
 * @return an instance of the pooled class
 **/
fun <T : Any> new(pooledClass: KClass<T>): T {
    return new(pooledClass.java)
}

/**
 * Get a new instance of an ObjectMap from a pool
 */
fun <K, V> newObjectMap(): ObjectMap<K, V> {
    return JJ.B.pools.nuwObjectMap()
}


/**
 * Recycle a pooled object
 */
fun recycle(vararg objects: Any) {
    for (o in objects) {
        JJ.B.pools.recycle(o)
    }
}


/**
 * Recycle a collection of pooled objects
 */
fun recycleItems(collectionOfObjects: Iterable<*>) {
    for (o in collectionOfObjects) {
        if (o != null) recycle(o)
    }
}

@Suppress("UNCHECKED_CAST")
/**
 * A place to manage your pools and pooled objects.
 *
 * If your scene is [Poolable] then you can just get new ones and recycle old ones here.
 * If not - A [PoolManager] must be registered.
 *
 * Once you have that you can use [new], [recycle] and [recycleItems] to deal with your pooled objects
 */
class Pools : PoolsAPI {

    internal var pools = ObjectMap<Class<*>, Pool<*>>()
    internal var poolablePools = ObjectMap<Class<*>, Array<Poolable>>()
    internal var objectMapPool = Array<ObjectMap<*, *>>()


    init {
        registerManager(Vector2PoolManager(), Vector2::class.java)
        registerManager(Vector3PoolManager(), Vector3::class.java)
        registerManager(Matrix3PoolManager(), Matrix3::class.java)
        registerManager(ParamsPoolManager(), InstanceParams::class.java)
    }

    /**
     * Get something from the pool or make a new one.
     *
     *
     * If your scene is [Poolable] then all is done for you.
     * If not - A [PoolManager] must be registered.
     *
     *
     * There is a nice little convenience function with much less to type [new]
     * Be sure to [recycle] it when you are done.

     * @param pooledClass the class of the object that is pooled
     * *
     * @return an instance of the pooled class
     **/
    fun <T> nuw(pooledClass: Class<T>): T {

        if (Poolable::class.java.isAssignableFrom(pooledClass)) {
            return nuwPoolable(pooledClass)
        }
        if (!pools.containsKey(pooledClass)) {
            throw NoPoolManagerException(pooledClass)
        } else {
            return pools.get(pooledClass).getPooled() as T
        }
    }

    /**
     * Get something from the pool or make a new one.
     *
     *
     * If your scene is [Poolable] then all is done for you.
     * If not - A [PoolManager] must be registered.
     *
     *
     * There is a nice little convenience function with much less to type [new]
     * Be sure to [recycle] it when you are done.

     * @param pooledClass the class of the object that is pooled
     * *
     * @return an instance of the pooled class
     **/
    fun <T : Any> nuw(pooledClass: KClass<T>): T {
        return nuw(pooledClass.java)
    }

    private fun <T> nuwPoolable(pooledClass: Class<T>): T {
        if (poolablePools.containsKey(pooledClass) && poolablePools.get(pooledClass).size > 0) {
            return poolablePools.get(pooledClass).pop() as T
        } else {
            try {
                return pooledClass.newInstance()
            } catch (e: InstantiationException) {
                throw CouldNotCreateOneException(pooledClass)
            } catch (e: IllegalAccessException) {
                throw CouldNotCreateOneException(pooledClass)
            }

        }
    }

    /**
     * Recycle the used pooled object. A [PoolManager] must be registered.
     * There is a nice little convenience function with much less to type [recycle]

     * @param pooled
     */
    fun recycle(pooled: Any) {
        if (pooled is ObjectMap<*, *>) {
            pooled.clear()
            objectMapPool.add(pooled)
            return
        }
        if (pooled is Poolable) {
            recyclePoolable(pooled)
            return
        }
        if (!pools.containsKey(pooled.javaClass)) {
            throw NoPoolManagerException(pooled.javaClass)
        } else {
            pools.get(pooled.javaClass).add(pooled)
        }
    }

    private fun recyclePoolable(pooled: Poolable) {
        if (!poolablePools.containsKey(pooled.javaClass)) {
            poolablePools.put(pooled.javaClass, Array<Poolable>())
        }
        poolablePools.get(pooled.javaClass).add(pooled)
        pooled.reset()
    }

    /**
     * Use this if you want to dump the pooled objects.
     * They will be disposed by the PoolManager before being removed from the pool.

     * @param clazzToClear
     */
    override fun clearPool(clazzToClear: Class<*>) {
        pools.get(clazzToClear).clear()
    }

    /**
     * Use this if you want to dump the pooled objects.
     * They will be disposed by the PoolManager before being removed from the pool.

     * @param clazzToClear
     */
    override fun clearPool(clazzToClear: KClass<*>) {
        pools.get(clazzToClear.java).clear()
    }

    override fun <P, T : PoolManager<P>> registerManager(poolManager: T, sceneToPoolClass: Class<P>) {
        if (!pools.containsKey(sceneToPoolClass)) {
            pools.put(sceneToPoolClass, Pool(poolManager))
        }
    }

    fun <K, V> nuwObjectMap(): ObjectMap<K, V> {
        if (objectMapPool.size > 0) {
            val map = objectMapPool.pop()
            map.clear()
            return map as ObjectMap<K, V>
        }
        return ObjectMap()
    }

    class NoPoolManagerException(classWithMissingPool: Class<*>) : RuntimeException(String.format("No PoolManager for %s. Register a pool manager.", classWithMissingPool.canonicalName))

    class CouldNotCreateOneException(noDefault: Class<*>) : RuntimeException(String.format("Could not access a default constructor for %s", noDefault.canonicalName))
}
