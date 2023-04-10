package demormi.registry

import demormi.exception.AlreadyBoundException
import demormi.exception.NotBoundException
import demormi.exception.RemoteException
import demormi.server.RemoteObjectRef
import demormi.server.TCPEndpoint
import demormi.server.Util
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class RegistryStubInvocationHandler(host: String, port: Int) : InvocationHandler {
    // 在 host, port 上面的，key为0的，实现了Registry接口的一个神奇对象。
    private val registryRef: RemoteObjectRef = RemoteObjectRef(TCPEndpoint(host, port),
        arrayOf("myrmi.registry.Registry"), 0)

    // Stub 是个具体对象。我们现在在 Client上，get到一个Registry，对Registry调用方法需要远程调用。
    private val registryStub: Registry = Util.createStub(registryRef) as Registry

    @Throws(RemoteException::class, AlreadyBoundException::class, NotBoundException::class, Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any {
        val result: Any
        try {
            println("RegistryInvocationHandler辅助调用了 $method。")
            // 使用stub作为对象来invoke，stub返回一个结果。
            result = method.invoke(registryStub, *args)
        } catch (e: InvocationTargetException) {
            throw RemoteException(cause=e)
        }
        return result
    }
}