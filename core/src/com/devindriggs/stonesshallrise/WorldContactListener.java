package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        Object object = null;

        if (a.getUserData() == "Adventurer") {
            if (b.getUserData() != null) {
                object = b.getUserData();
            }
        }
        if (b.getUserData() == "Adventurer") {
            if (b.getUserData() != null) {
                object = a.getUserData();
            }
        }
        if (object != null && BaseItem.class.isAssignableFrom(object.getClass())) {
            ((BaseItem)object).onContact();
        }
        if (object != null && PhysicsActor.class.isAssignableFrom(object.getClass())) {
            ((PhysicsActor)object).onContact();
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
