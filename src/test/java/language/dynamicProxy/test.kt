package language.dynamicProxy
//https://juejin.cn/post/6992602018380513294
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy


interface Vehicle {
    fun run()
}

class Car : Vehicle {
    override fun run() {
        println("Car Running")
    }
}


class VehicleInvocationHandler(private val vehicle: Vehicle) : InvocationHandler {
    @Throws(Throwable::class)
    override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any {
        // proxy是虚假对象。
        println("---------before-------")
        // 利用真实对象实现需求。

//        val invoke: Any = method.invoke(vehicle, args?: arrayOf<Any?>(vehicle))
        val invoke: Any = method.invoke(vehicle, args)
        println("---------after-------")
        return invoke // 未来你想变成什么接口都行。
    }
}

fun main() {
    val car: Vehicle = Car()
    val vehicle: Vehicle = Proxy.newProxyInstance(
        car.javaClass.classLoader,
        Car::class.java.interfaces,
        VehicleInvocationHandler(car)
    ) as Vehicle

    vehicle.run()
}