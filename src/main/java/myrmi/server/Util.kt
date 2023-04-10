package myrmi.server

import myrmi.Remote
import myrmi.server.RemoteObjectRef
import java.lang.reflect.Proxy

object Util {
    /**
     * finish here, instantiate an StubInvocationHandler for ref and then return a stub
     */
    fun createStub(ref: RemoteObjectRef): Remote {
        return Proxy.newProxyInstance(
            Util::class.java.classLoader,  // 随便给一个类加载器都可以。
            arrayOf<Class<*>>(RemoteObjectRef::class.java), // 要实现什么接口
            StubInvocationHandler(ref) // 想要怎么实现。
        ) as Remote
    }
}