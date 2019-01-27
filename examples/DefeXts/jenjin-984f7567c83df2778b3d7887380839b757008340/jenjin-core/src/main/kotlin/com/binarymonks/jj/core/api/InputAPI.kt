package com.binarymonks.jj.core.api

import com.binarymonks.jj.core.input.mapping.Actions
import com.binarymonks.jj.core.input.mapping.GeneralHandler
import com.binarymonks.jj.core.input.mapping.KeyHandler


interface InputAPI {
    /**
     * Lets you map input events to handlers and actions.
     * Lets you map actions to handlers.
     */

    /**
     * Map a key code and a particular key action to a handler.
     */
    fun map(keyCode: Int, keyAction: Actions.Key, handler: GeneralHandler)

    /**
     * Map a key code to a handler that will handle all key actions.
     */
    fun map(keyCode: Int, keyHandler: KeyHandler)

    /**
     * Map a particular key code action to a game action.
     * Allows for multiple mappings to an action, which can then be bound to a handler.
     */
    fun mapToAction(keyCode: Int, keyAction: Actions.Key, actionName: String)

    /**
     * Bind a handler to a particular action.
     */
    fun bindToAction(handler: GeneralHandler, actionName: String)
}