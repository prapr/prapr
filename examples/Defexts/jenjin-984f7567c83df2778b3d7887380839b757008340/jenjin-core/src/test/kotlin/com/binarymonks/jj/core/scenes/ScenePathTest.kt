package com.binarymonks.jj.core.scenes

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.audio.SoundEffects
import com.binarymonks.jj.core.mockoutGDXinJJ
import com.binarymonks.jj.core.physics.PhysicsRoot
import com.binarymonks.jj.core.render.RenderRoot
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito


class ScenePathTest {

    lateinit var grandParent: Scene
    lateinit var parent: Scene
    lateinit var uncle: Scene
    lateinit var me: Scene
    lateinit var cousin: Scene
    lateinit var child: Scene


    @Before
    fun setup() {
        mockoutGDXinJJ()
        grandParent = scene("grandparent")
        parent = scene("scene")
        uncle = scene("uncle")
        me = scene("me")
        cousin = scene("cousin")
        child = scene("child1")

        grandParent.add(uncle)
        grandParent.add(parent)
        parent.add(me)
        uncle.add(cousin)
        me.add(child)
    }

    @Test
    fun getGrandParent() {
        Assert.assertEquals(grandParent, ScenePath(ScenePath.UP, ScenePath.UP).from(me))
    }

    @Test
    fun getCousin() {
        Assert.assertEquals(cousin, ScenePath(ScenePath.UP, ScenePath.UP, "uncle", "cousin").from(me))
    }

    @Test
    fun getChild() {
        Assert.assertEquals(child, ScenePath("child1").from(me))
    }

    @Test
    fun getMe() {
        Assert.assertEquals(me, ScenePath().from(me))
    }

    @Test(expected = Exception::class)
    fun getBadPath() {
        ScenePath("blah").from(me)
    }


    fun scene(name: String): Scene {
        return Scene(
                name,
                null,
                null,
                1,
                Vector2(),
                PhysicsRoot(Mockito.mock(Body::class.java)),
                RenderRoot(1),
                SoundEffects(),
                ObjectMap()
        )
    }
}