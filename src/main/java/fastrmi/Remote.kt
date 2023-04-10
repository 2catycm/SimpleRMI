package fastrmi

import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

interface Remote {
    val remoteObj:RemoteObject
    val objectId
        get() = remoteObj.remoteRef.objectId
}

/**
 * 用法：对于要变成远程对象的类Q， Q必须 implements Remote。除此之外，需要用下列方法之一来获得功能。
 * 1. 让Q extends RemoteObject， 构造器中调用无参构造器super()方法。
 * 2. 使用组合替代继承，在Q中 写 private RemoteObject ro = new RemoteObject(); 然后使用 RemoteObject 实现 Remote 接口的方法。
 *    目前Remote 接口没有方法。所以不需要然后。这种方法是为了让您可以自由的选择Q继承什么类。
 * 3. 在kotlin中，可以很简洁地实现2.逻辑。您可以让 Q : Remote by RemoteObject(), T1, T2,..., Tn
 *      其中 T1-Tn是您想让Q继承或者实现的其他类。
 */
open class RemoteObject (self:Remote) {
    // 每次创建一个对象，注册在RMI服务器中。
    // 1. 不能使用hashCode， hashCode只是判断equals的一种简单方法，可能哈希冲突；不能判断对象的内存地址。
    // 2. 无法获得java对象的内存地址。
    // 3. 于是使用UUID。
    val remoteRef = RemoteRef()
    init {
        RMIServer.instance[remoteRef.objectId] = self
    }

}



// 懒汉式单例。
class RMIServer private constructor() : MutableMap<UUID, Any> by ConcurrentHashMap(), Thread() {
    companion object {
        val instance by lazy { RMIServer() }
    }

    init {
        this.start()
    }

    private val socket = ServerSocket(0)
    private val listeningPort = socket.localPort
    val objectLocation = InetSocketAddress(InetAddress.getLocalHost(), listeningPort)
    override fun run() {
        while (!interrupted()) {
            try {
                println("提醒：RMIServer 还没写呢")
                sleep(10000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}

fun main() {
    RMIServer.instance
}