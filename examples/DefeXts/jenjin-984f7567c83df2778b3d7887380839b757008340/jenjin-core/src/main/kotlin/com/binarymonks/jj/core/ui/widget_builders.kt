package com.binarymonks.jj.core.ui

import com.badlogic.gdx.scenes.scene2d.Actor


fun <T : LayerAwareListener> Actor.addJJListener(listener: T, build: (T.() -> Unit)? = null) {
    if(build!=null){
        listener.build()
    }

}

