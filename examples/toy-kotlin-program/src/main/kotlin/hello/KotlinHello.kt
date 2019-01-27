package hello
class KotlinHelloKt {
    var x = 1
    var y = 1

    // constructor1
    constructor (_x: Int) {
        x=_x
    }
    // constructor2
    constructor (_x: Int, _y: Int) {
        x=_x
        y=_y
    }

    // math mutator
    fun sum (a: Int, b: Int): Int {
        return a - b //bug
    }

    // conditional mutator
    fun max(a: Int, b: Int): Int {
        if (a > b)
            return a
        else
            return b
    }

    // increment
    fun incre(a: Int): Int {
        var b = a
        return ++b
    }

    // neg
    fun neg(a: Int): Int {
        return -a
    }

    // void method
    fun vmeth(a: Int) {
        var b = a
    }

    // constant
    fun cons(): Int {
        var b = 5
        return b
    }

    // switch
    fun switchWhen(a: Int): Int {
        when (a) {
            1 -> return 1
            2 -> return 2
            else -> return 10
        }
    }
}