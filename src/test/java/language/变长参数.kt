package language

import java.lang.reflect.InvocationTargetException
import java.util.*

object 变长参数 {
    @Throws(NoSuchMethodException::class, InvocationTargetException::class, IllegalAccessException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val ints = intArrayOf(1, 2, 3)
        val test2 = 变长参数::class.java.getDeclaredMethod(
            "test2",
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType
        )
        test2.invoke(null, ints[0], ints[1], ints[2])
        //        test2.invoke(null,  ints);
        test2.invoke(null, *Arrays.stream(ints).mapToObj { i: Int -> i }.toArray())
    }

    fun test2(arg1: Int, arg2: Int, arg3: Int) {
        println("test2 was invoked")
    }

    fun test(vararg args: Int) {
        println(Arrays.toString(args))
    }
}