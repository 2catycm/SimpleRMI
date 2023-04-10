package demormi.registry

import demormi.Remote
import demormi.exception.RemoteException
import java.lang.reflect.Proxy
import java.net.InetAddress

// LocateRegistry 不是Registry，而是Locate（定位） Registry的工厂。
object LocateRegistry {
    /**
     * 客户端获得一个 Registry
     */
    fun getRegistry(host: String = "127.0.0.1", port: Int = Registry.REGISTRY_PORT): Registry {
        // 默认的port
        val normalizedPort = port.takeIf { it > 0 } ?: Registry.REGISTRY_PORT

        val normalizedHost = host.ifEmpty {
            try {
                InetAddress.getLocalHost().hostAddress
            } catch (e: Exception) {
                ""
            }
        }
        // 创建一个Registry的代理对象。
        // 具体的行为是 RegistryStubInvocationHandler 决定的，
        // 我们是Client，此时我们通过 normalizedHost, normalizedPort 信息 和 Registry实际的位置通信
        val stub = Proxy.newProxyInstance(
            Registry::class.java.classLoader, arrayOf<Class<*>>(Registry::class.java),
            RegistryStubInvocationHandler(normalizedHost, normalizedPort)
        ) as Remote
        return stub as Registry
    }

    /**
     * create a registry locally,
     * but we still need to wrap around the lookup() method
     */
    @Throws(RemoteException::class)
    fun createRegistry(port: Int = Registry.REGISTRY_PORT): Registry {
        //Notice here the registry can only bind to 127.0.0.1, can you extend that?
        val normalizedPort = port.takeIf { it > 0 } ?: Registry.REGISTRY_PORT

        //        return Proxy.newProxyInstance(
//            Registry::class.java.classLoader, arrayOf<Class<*>>(
//                Registry::class.java
//            ), RegistryStubInvocationHandler("127.0.0.1", normalizedPort)
//        ) as Registry

        return RegistryImpl(port)
    }
}