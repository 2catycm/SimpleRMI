package fastrmi

import java.io.Serializable
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.net.InetSocketAddress
import java.util.*

/**
 * Stub 本身是Remote接口的。可以网络传输
 */
data class RemoteRef(
    // 指代对象原本所在的位置。
    val objectLocation: InetSocketAddress = RMIServer.instance.objectLocation,
    val objectId: UUID = UUID.randomUUID(),
    ) : Serializable {

}


class StubInvocationHandler(private val ref: RemoteRef) : InvocationHandler, Serializable {

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        method ?: throw NullReferenceException()
        println("调用了$method, 参数为$args")
        val nonNullArgs = args ?: arrayOf()
        for (arg in nonNullArgs) {
            if (arg !is Serializable)
                throw ArgumentsNotSerializableException()
        }
        if (method.name == "toString") {
            return this.ref.toString()
        }

        if (ref.objectLocation == RMIServer.instance.objectLocation) {
            val realObj = RMIServer.instance[ref.objectId]
            return if (nonNullArgs.isEmpty()) method.invoke(realObj) else method.invoke(realObj, nonNullArgs)
        }

        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StubInvocationHandler

        if (ref != other.ref) return false

        return true
    }

    override fun hashCode(): Int {
        return ref.hashCode()
    }
}

fun createStubOnServer(remoteObject: Remote): Remote {
    return Proxy.newProxyInstance(
        StubInvocationHandler::class.java.classLoader,
        remoteObject.javaClass.interfaces,
        StubInvocationHandler(RemoteRef(RMIServer.instance.objectLocation, remoteObject.objectId))
    ) as Remote
}
