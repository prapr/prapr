package com.binarymonks.jj.core

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.ObjectSet
import com.binarymonks.jj.core.extensions.addVar
import com.binarymonks.jj.core.extensions.build
import com.binarymonks.jj.core.properties.PropOverride
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class CopyTest {

    @Before
    fun setUp(){
        mockoutGDXinJJ()
    }

    @Test
    fun clone() {

        val original = ThingToCopy(
                name = "name1",
                number = 2,
                arrayOfCloneable = Array<SomeNestedCopyable>().addVar(SomeNestedCopyable("InArray")),
                map = ObjectMap<String, SomeNestedCopyable>().build { put("key", SomeNestedCopyable("InMap")) },
                set = ObjectSet<SomeNestedCopyable>().addVar(SomeNestedCopyable("InSet"))
        )
        original.cloneableProp.set("blue")
        original.hiddenName = "altered"
        original.cloneableValProp.set("green")

        val copy: ThingToCopy = copy(original)

        Assert.assertNotSame(original, copy)
        Assert.assertEquals(original.name, copy.name)
        Assert.assertEquals(original.number, copy.number)
        Assert.assertNotEquals(original.hiddenName, copy.hiddenName)
        Assert.assertNotSame(original.cloneableProp, copy.cloneableProp)
        Assert.assertEquals(original.cloneableProp, copy.cloneableProp)
        Assert.assertEquals(original.readableButNotCopyable, copy.readableButNotCopyable)
        Assert.assertEquals(original.cloneableValProp,copy.cloneableValProp)

        Assert.assertEquals(original.cloneable, copy.cloneable)
        Assert.assertNotSame(original.cloneable, copy.cloneable)

        Assert.assertEquals(original.arrayOfCloneable, copy.arrayOfCloneable)
        Assert.assertNotSame(original.arrayOfCloneable, copy.arrayOfCloneable)

        Assert.assertEquals(original.map, copy.map)
        Assert.assertNotSame(original.map, copy.map)

        Assert.assertEquals(original.set, copy.set)
        Assert.assertNotSame(original.set, copy.set)


    }
}


class ThingToCopy(
        var name: String? = null,
        var number: Int = 0,
        var cloneableProp: PropOverride<String> = PropOverride("nothing"),
        var cloneable: SomeNestedCopyable = SomeNestedCopyable("TopLevel"),
        var arrayOfCloneable: Array<SomeNestedCopyable> = Array(),
        var map: ObjectMap<String, SomeNestedCopyable> = ObjectMap(),
        var set: ObjectSet<SomeNestedCopyable> = ObjectSet()
) {
    var readableButNotCopyable: String = "Original"
        private set
    internal var hiddenName: String = "hidden"
    val fixed = "Cannot change"
    val cloneableValProp:PropOverride<String> = PropOverride<String>("constantPropOverrideOriginal")

}

class SomeNestedCopyable() : Copyable<SomeNestedCopyable> {
    var name = ""

    constructor(name: String) : this() {
        this.name = name
    }

    override fun clone(): SomeNestedCopyable {
        return copy(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as SomeNestedCopyable

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }


}