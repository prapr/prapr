package com.binarymonks.jj.core.async

import com.badlogic.gdx.utils.Array

class TaskMaster {

    internal var tasks = Array<Task>()
    internal var addTasks = Array<Task>()
    internal var removeTasks = Array<Task>()


    fun update() {
        clean()
        for (task in tasks) {
            if (task.isDone()) {
                removeTasks.add(task)
            } else {
                task.doWork()
            }
        }
    }

    fun clean() {
        for (removeTask in removeTasks) {
            removeTask.tearDown()
            tasks.removeValue(removeTask, true)
        }
        removeTasks.clear()
        for (addTask in addTasks) {
            tasks.add(addTask)
            addTask.getReady()
        }
        addTasks.clear()
    }

    fun neutralise() {
        clean()
        for (task in tasks) {
            task.tearDown()
        }
    }

    fun reactivate() {
        for (task in tasks) {
            task.getReady()
        }
    }

    fun addTask(task: Task) {
        addTasks.add(task)
    }

}
