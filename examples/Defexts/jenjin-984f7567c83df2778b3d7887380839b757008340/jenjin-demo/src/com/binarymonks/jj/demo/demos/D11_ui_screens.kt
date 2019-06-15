package com.binarymonks.jj.demo.demos

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.layers.Layer
import com.binarymonks.jj.core.ui.JJChangeListener
import com.binarymonks.jj.core.ui.UIBuilder


class D11_ui_screens : JJGame() {


    override fun gameOn() {

        JJ.assets.loadNow("ui/uiskin.json", Skin::class)

        JJ.layers.registerLayer("screen1", screen1())
        JJ.layers.registerLayer("screen2", screen2())

        JJ.layers.push("screen1")

    }

    private fun screen1(): Layer {
        val width = Gdx.graphics.width.toFloat()
        val height = Gdx.graphics.height.toFloat()
        val skin = JJ.assets.getAsset("ui/uiskin.json", Skin::class)

        //UIBuilder lets you build a UI Layer. Duh.
        return UIBuilder {

            actor(Label("SCREEN 1", skin)) {
                setPosition(width / 2, height * 2 / 3)
            }

            //You can add actors with a name reference so that you can retrieve them
            actor("beepButton", TextButton("Beep", skin)) {
                setPosition(width / 2, height / 2)
            }

            //There is a second tier builder for adding listeners to your actors
            actor(TextButton("Hide", skin)) {
                setPosition(width * 2 / 3, height / 2)
            }.withListener(object : JJChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    uiLayer?.getActor("beepButton")?.isVisible = false
                }
            })

            //You can add UILayer aware Listeners, so you can get a reference to the stage and other named actors.
            actor(TextButton("show", skin)) {
                setPosition(width * 1 / 3, height / 2)
            }.withListener(object : JJChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    //Here we retrieve one of the other actors
                    uiLayer?.getActor("beepButton")?.isVisible = true
                }
            })

            actor(TextButton("Swap Screen", skin)) {
                setPosition(width / 2, height / 3)
            }.withListener(object : JJChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    JJ.layers.pop()
                    JJ.layers.push("screen2")
                }
            })

        }.build()
    }

    private fun screen2(): Layer {
        val width = Gdx.graphics.width.toFloat()
        val height = Gdx.graphics.height.toFloat()
        val skin = JJ.assets.getAsset("ui/uiskin.json", Skin::class)

        return UIBuilder {

            actor(Label("SCREEN 2", skin)) {
                setPosition(width / 2, height * 2 / 3)
            }

            actor(TextButton("Swap Screen", skin)) {
                setPosition(width / 2, height / 3)
            }.withListener(object : JJChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    JJ.layers.pop()
                    JJ.layers.push("screen1")
                }
            })

        }.build()
    }

}