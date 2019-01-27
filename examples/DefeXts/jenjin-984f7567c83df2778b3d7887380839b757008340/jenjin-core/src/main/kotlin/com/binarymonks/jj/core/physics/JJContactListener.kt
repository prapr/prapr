package com.binarymonks.jj.core.physics

import com.badlogic.gdx.physics.box2d.*
import com.binarymonks.jj.core.scenes.Scene

class JJContactListener : ContactListener {

    /**
     * beginContact is the only hook that will guarantee that the objects have not been disposed.
     */
    override fun beginContact(contact: Contact) {
        val fixtureA = contact.fixtureA
        val sceneA = getScene(fixtureA)
        val resolverA = getResolver(fixtureA)

        val fixtureB = contact.fixtureB
        val sceneB = getScene(fixtureB)
        val resolverB = getResolver(fixtureB)

        resolverA.beginContact(sceneB, fixtureB, contact, fixtureA)
        resolverB.beginContact(sceneA, fixtureA, contact, fixtureB)


        resolverA.finalBeginContact(sceneB, fixtureB, contact, fixtureA)
        resolverB.finalBeginContact(sceneA, fixtureA, contact, fixtureB)
    }

    private fun getResolver(fixture: Fixture): CollisionResolver {
        return (fixture.userData as PhysicsNode).collisionResolver
    }

    private fun getScene(fixture: Fixture): Scene {
        val node = fixture.userData as PhysicsNode
        return checkNotNull(node.physicsRoot.parent)
    }


    /**
     * There may be several game loops before a contact ends. Objects may have been disposed by the beginContact.
     */
    override fun endContact(contact: com.badlogic.gdx.physics.box2d.Contact) {
        val fixtureA = contact.fixtureA
        val objectA = getScene(fixtureA)
        val resolverA = getResolver(fixtureA)

        val fixtureB = contact.fixtureB
        val objectB = getScene(fixtureB)
        val resolverB = getResolver(fixtureB)

        resolverA.endContact(objectB, fixtureB, contact, fixtureA)
        resolverB.endContact(objectA, fixtureA, contact, fixtureB)
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        val fixtureA = contact.fixtureA
        val sceneA = getScene(fixtureA)
        val resolverA = getResolver(fixtureA)

        val fixtureB = contact.fixtureB
        val sceneB = getScene(fixtureB)
        val resolverB = getResolver(fixtureB)

        resolverA.preSolveContact(sceneB, fixtureB, contact, fixtureA, oldManifold)
        resolverB.preSolveContact(sceneA, fixtureA, contact, fixtureB, oldManifold)
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
        val fixtureA = contact.fixtureA
        val sceneA = getScene(fixtureA)
        val resolverA = getResolver(fixtureA)

        val fixtureB = contact.fixtureB
        val sceneB = getScene(fixtureB)
        val resolverB = getResolver(fixtureB)

        resolverA.postSolveContact(sceneB, fixtureB, contact, fixtureA, impulse)
        resolverB.postSolveContact(sceneA, fixtureA, contact, fixtureB, impulse)
    }

}
