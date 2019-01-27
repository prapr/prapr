package com.binarymonks.jj.core.input.mapping

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.api.InputAPI


class InputMapper : InputAPI, InputProcessor {

    internal var keyToActionToFunctionMap = ObjectMap<Int, ObjectMap<Actions.Key, GeneralHandler>>()
    internal var keyToHandlerMap = ObjectMap<Int, KeyHandler>()
    internal var keyToKeyActionToGameActionMap = ObjectMap<Int, ObjectMap<Actions.Key, String>>()
    internal var gameActionToFunctionMap = ObjectMap<String, GeneralHandler>()

    override fun map(keyCode: Int, keyAction: Actions.Key, handler: GeneralHandler) {
        if (!keyToActionToFunctionMap.containsKey(keyCode)) {
            keyToActionToFunctionMap.put(keyCode, ObjectMap<Actions.Key, GeneralHandler>())
        }
        keyToActionToFunctionMap.get(keyCode).put(keyAction, handler)
    }

    override fun map(keyCode: Int, keyHandler: KeyHandler) {
        keyToHandlerMap.put(keyCode, keyHandler)
    }


    override fun keyDown(keycode: Int): Boolean {
        return handleKey(keycode, Actions.Key.PRESSED)
    }

    private fun handleKey(keycode: Int, action: Actions.Key): Boolean {
        var handled = false
        if (keyToActionToFunctionMap.containsKey(keycode)) {
            val actionToFunction = keyToActionToFunctionMap.get(keycode)
            if (actionToFunction.containsKey(action)) {
                return actionToFunction.get(action)()
            }
        }
        if (!handled && keyToHandlerMap.containsKey(keycode)) {
            handled = keyToHandlerMap.get(keycode)(action)
        }
        if (!handled && keyToKeyActionToGameActionMap.containsKey(keycode)) {
            val actionToGameAction = keyToKeyActionToGameActionMap.get(keycode)
            if (actionToGameAction.containsKey(action)) {
                val gameAction = actionToGameAction.get(action)
                if (gameActionToFunctionMap.containsKey(gameAction)) {
                    handled = gameActionToFunctionMap.get(gameAction)()
                }
            }
        }
        return handled
    }

    override fun keyUp(keycode: Int): Boolean {
        return handleKey(keycode, Actions.Key.RELEASED)
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amount: Int): Boolean {
        return false
    }

    override fun mapToAction(keyCode: Int, keyAction: Actions.Key, actionName: String) {
        if (!keyToKeyActionToGameActionMap.containsKey(keyCode)) {
            keyToKeyActionToGameActionMap.put(keyCode, ObjectMap<Actions.Key, String>())
        }
        keyToKeyActionToGameActionMap.get(keyCode).put(keyAction, actionName)
    }

    override fun bindToAction(handler: GeneralHandler, actionName: String) {
        gameActionToFunctionMap.put(actionName, handler)
    }
}
