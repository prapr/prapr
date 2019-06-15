package com.binarymonks.jj.demo.demos

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJConfig
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.components.Component
import com.binarymonks.jj.core.input.mapping.Actions
import com.binarymonks.jj.core.layers.Layer
import com.binarymonks.jj.core.physics.CollisionHandler
import com.binarymonks.jj.core.physics.collisions.DestroyCollision
import com.binarymonks.jj.core.physics.collisions.EmitEventCollision
import com.binarymonks.jj.core.physics.collisions.SoundCollision
import com.binarymonks.jj.core.scenes.Scene
import com.binarymonks.jj.core.specs.Rectangle
import com.binarymonks.jj.core.specs.SceneSpec
import com.binarymonks.jj.core.specs.SceneSpecRef
import com.binarymonks.jj.core.specs.params
import com.binarymonks.jj.core.ui.JJClickListener
import com.binarymonks.jj.core.ui.UIBuilder
import com.binarymonks.jj.core.ui.UILayer

val COURT_LENGTH = 12f
val BAT_LENGTH = 1.5f

val PLAYER_A_SCORED = "PLAYER_A_SCORED"
val PLAYER_B_SCORED = "PLAYER_B_SCORED"

val WIN_SCORE = 3

object PongConfig {
    var jjConfig: JJConfig = JJConfig()

    init {
        jjConfig.b2d.gravity = Vector2.Zero

        jjConfig.gameView.worldBoxWidth = COURT_LENGTH
        jjConfig.gameView.cameraPosX = 6f
        jjConfig.gameView.cameraPosY = 6f
    }
}

/**
 * A full and tiny implementation of pong
 */
class Pong : JJGame(PongConfig.jjConfig) {

    override fun gameOn() {

        // Load assets
        JJ.assets.loadNow("ui/uiskin.json", Skin::class)

        // Layers/Menus/UIs
        JJ.layers.registerLayer("mainMenu", mainMenu())
        JJ.layers.registerLayer("gameHUD", gameHUD())


        // Scenes
        JJ.scenes.addSceneSpec("ball", ball())
        JJ.scenes.addSceneSpec("player", player())
        JJ.scenes.addSceneSpec("court", court())
        JJ.scenes.loadAssetsNow()

        // Wiring
        JJ.events.register(PLAYER_A_SCORED, this::playerAScored)
        JJ.events.register(PLAYER_B_SCORED, this::playerBScored)

        // Instantiate the court and players
        JJ.scenes.instantiate("court")
        JJ.scenes.instantiate(params { x = 1f; y = COURT_LENGTH / 2 }, "player").then {
            val player: Player = it.getComponent(Player::class).first()
            mapPlayer(player, Input.Keys.W, Input.Keys.S)
        }
        JJ.scenes.instantiate(params { x = COURT_LENGTH - 1f; y = COURT_LENGTH / 2 }, "player").then {
            val player: Player = it.getComponent(Player::class).first()
            mapPlayer(player, Input.Keys.UP, Input.Keys.DOWN)
        }

        //Show the main menu
        JJ.layers.push("mainMenu")
    }

    fun playerAScored() {
        GameState.playerAScore++
        if (GameState.playerAScore > WIN_SCORE) {
            playerWins("Player A Wins")
        } else {
            refreshScores()
            newBall()
        }
    }


    fun playerBScored() {
        GameState.playerBScore++
        if (GameState.playerBScore > WIN_SCORE) {
            playerWins("Player B Wins")
        } else {
            refreshScores()
            newBall(-1)
        }
    }

    private fun playerWins(winnerMessage: String) {
        JJ.layers.pop()
        (JJ.layers.getLayer<UILayer>("mainMenu").getActor("message") as Label).setText(winnerMessage)
        JJ.layers.push("mainMenu")
    }

}

fun refreshScores() {
    (JJ.layers.getLayer<UILayer>("gameHUD").getActor("playerBScore") as Label).setText("${GameState.playerBScore}")
    (JJ.layers.getLayer<UILayer>("gameHUD").getActor("playerAScore") as Label).setText("${GameState.playerAScore}")
}

object GameState {
    var playerAScore = 0
    var playerBScore = 0
}

fun court(): SceneSpecRef {
    return SceneSpec {
        //LeftWall
        node(params { x = 0f; y = COURT_LENGTH / 2 }) {
            physics {
                bodyType = BodyDef.BodyType.StaticBody
                fixture { shape = Rectangle(1f, COURT_LENGTH) }
                collisions.begin(DestroyCollision()) { destroyMe = false; destroyOther = true }
                collisions.begin(EmitEventCollision()) { messege.set(PLAYER_B_SCORED) }
            }
            render { rectangleRender(1f, COURT_LENGTH) }
        }
        //RightWall
        node(params { x = COURT_LENGTH; y = COURT_LENGTH / 2 }) {
            physics {
                bodyType = BodyDef.BodyType.StaticBody
                fixture { shape = Rectangle(1f, COURT_LENGTH) }
                collisions.begin(DestroyCollision()) { destroyMe = false; destroyOther = true }
                collisions.begin(EmitEventCollision()) { messege.set(PLAYER_A_SCORED) }
            }
            render { rectangleRender(1f, COURT_LENGTH) }
        }
        //BottomWall
        node(params { x = COURT_LENGTH / 2; y = 0f; rotationD = 90f }) {
            physics {
                bodyType = BodyDef.BodyType.StaticBody
                fixture { shape = Rectangle(1f, COURT_LENGTH) }
            }
            render { rectangleRender(1f, COURT_LENGTH) }
        }
        //TopWall
        node(params { x = COURT_LENGTH / 2; y = COURT_LENGTH; rotationD = 90f }) {
            physics {
                bodyType = BodyDef.BodyType.StaticBody
                fixture { shape = Rectangle(1f, COURT_LENGTH) }
            }
            render { rectangleRender(1f, COURT_LENGTH) }
        }
    }
}


fun player(): SceneSpecRef {

    return SceneSpec {
        physics {
            bodyType = BodyDef.BodyType.KinematicBody
            fixture {
                shape = Rectangle(0.3f, BAT_LENGTH)
            }
            collisions.begin(BatCollision())
        }
        render { rectangleRender(0.3f, BAT_LENGTH) }
        component(Player())
    }
}

class BatCollision : CollisionHandler() {
    //Puts some redirection on the ball
    override fun collision(me: Scene, myFixture: Fixture, other: Scene, otherFixture: Fixture, contact: Contact): Boolean {
        val myPosition = me.physicsRoot.position()
        val collisionPosition = contact.worldManifold.points[0]

        val collisionOffset = (collisionPosition.y - myPosition.y) / (BAT_LENGTH / 2)

        other.physicsRoot.b2DBody.applyLinearImpulse(0f, collisionOffset * 0.2f, 0f, 0f, true)
        return false
    }
}

class Player : Component() {
    internal var velocity = 10f

    internal var up = false
    internal var down = false

    override fun update() {
        var direction = 0f
        val y = me().physicsRoot.position().y
        if (up && y < COURT_LENGTH - BAT_LENGTH / 2) {
            direction += 1f
        }
        if (down && y > BAT_LENGTH / 2) {
            direction -= 1f
        }
        me().physicsRoot.b2DBody.setLinearVelocity(0f, velocity * direction)
    }

    fun goUp(): Boolean {
        up = true
        return true
    }

    fun stopUp(): Boolean {
        up = false
        return true
    }

    fun goDown(): Boolean {
        down = true
        return true
    }

    fun stopDown(): Boolean {
        down = false
        return true
    }
}

fun ball(): SceneSpecRef {
    val ballRadius = 0.15f
    return SceneSpec {
        sounds.sound("bounce", "sounds/pong.mp3", volume = 0.6f)
        physics {
            bodyType = BodyDef.BodyType.DynamicBody
            fixedRotation = true
            fixture {
                shape = Rectangle(ballRadius * 2, ballRadius * 2)
                restitution = 1f
            }
            collisions.begin(SoundCollision(soundName = "bounce"))
        }
        render {
            circleRender(ballRadius)
        }
    }
}

private fun newGame() {
    GameState.playerAScore = 0
    GameState.playerBScore = 0
    refreshScores()
    newBall()
}


private fun mapPlayer(player: Player, upKeyCode: Int, downKeyCode: Int) {
    JJ.input.map(upKeyCode, Actions.Key.PRESSED, player::goUp)
    JJ.input.map(upKeyCode, Actions.Key.RELEASED, player::stopUp)
    JJ.input.map(downKeyCode, Actions.Key.PRESSED, player::goDown)
    JJ.input.map(downKeyCode, Actions.Key.RELEASED, player::stopDown)
}

private fun newBall(direction: Int = 1) {
    JJ.scenes.instantiate(params { x = COURT_LENGTH / 2; y = COURT_LENGTH / 2 }, "ball").then {
        it.physicsRoot.b2DBody.setLinearVelocity(5f * direction, 0f)
    }
}

fun mainMenu(): Layer {
    val skin = JJ.assets.getAsset("ui/uiskin.json", Skin::class)
    val vWidth = 400f
    val vHeight = 400f
    return UIBuilder(ExtendViewport(vWidth, vHeight)) {

        actor("message", Label("PIXEL PONG", skin)) {
            x = vWidth * 0.4f
            y = vHeight * 2 / 3
        }

        actor(TextButton("START", skin)) {
            x = vWidth * 0.45f
            y = vHeight * 0.5f
        }.withListener(object : JJClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                JJ.layers.pop()
                JJ.layers.push("gameHUD")
                newGame()
            }
        })
    }.build()
}

fun gameHUD(): Layer {
    val skin = JJ.assets.getAsset("ui/uiskin.json", Skin::class)
    val vWidth = 500f
    val vHeight = 500f
    return UIBuilder(ExtendViewport(vWidth, vHeight)) {

        actor("playerAScore", Label("0", skin)) {
            x = vWidth * 0.4f
            y = vHeight * 0.8f
        }

        actor("playerBScore", Label("0", skin)) {
            x = vWidth * 0.6f
            y = vHeight * 0.8f
        }
    }.build()
}