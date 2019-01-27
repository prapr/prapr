package com.binarymonks.jj.core.api

interface ClockAPI {

    /**
     * The current loop delta as a Double.
     */
    val delta: Double

    /**
     * The current loop delta as a Float.
     */
    val deltaFloat: Float

    /**
     * The total elapsed time as a Double
     */
    val time: Double

    /**
     * The total elapsed time as a Float
     */
    val timeFloat: Float

    /**
     * Lets you schedule a function to occur after a delay, and then to repeat.
     * Be aware to cancel scheduled tasks if you no longer need them. For example when a scene gets destroyed.
     *
     * @property function The function to call.
     * @property delaySeconds The delay in seconds to at first call the function, and then between successive repeats.
     * @property name Name of the scheduled task. Useful for debugging.
     *
     * @return The id of the scheduled task. Use this to cancel the task.
     */
    fun schedule(function: () -> Unit, delaySeconds: Float, repeat: Int = 1, name: String? = null): Int

    /**
     * Lets you schedule a function to occur after a random windowed delay, and then to repeat.
     * Be aware to cancel scheduled tasks if you no longer need them. For example when a scene gets destroyed.
     *
     * @property function The function to call.
     * @property delayMinSeconds The minimum delay in seconds to at first call the function, and then between successive repeats.
     * @property delayMaxSeconds The maximum delay in seconds to at first call the function, and then between successive repeats.
     * @property name Name of the scheduled task. Useful for debugging.
     *
     * @return The id of the scheduled task. Use this to cancel the task.
     */
    fun schedule(function: () -> Unit, delayMinSeconds: Float, delayMaxSeconds: Float, repeat: Int = 1, name: String? = null): Int

    /**
     * Cancel a scheduled task by id.
     *
     * Will complete silently if the id does not exist or the task is already complete.
     */
    fun cancel(id: Int)
}
