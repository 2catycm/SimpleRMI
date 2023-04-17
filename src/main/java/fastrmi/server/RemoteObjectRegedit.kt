package fastrmi.server

import fastrmi.Request
import fastrmi.Response
import kotlinx.coroutines.*
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

/**
 * RMIServer
 *  1. 本身是RMI服务端上可用远程对象的映射表。
 *  2. 本身是一个线程, 可以处理网络请求。
 *  3.
 * RMIServer是懒汉式单例。
 */
class RemoteObjectRegedit private constructor(

) : MutableMap<UUID, Any> by ConcurrentHashMap() {
    companion object {
        val instance by lazy { RemoteObjectRegedit() }
    }
    private val rmiServer = RMIServer()
    val objectLocation = rmiServer.objectLocation
    init {
        rmiServer.isDaemon = true // 类似于垃圾回收服务。
        rmiServer.start()
    }
}

class RMIServer : Thread() {
    private val socket = ServerSocket(0)
    private val listeningPort = socket.localPort
    val objectLocation = InetSocketAddress(InetAddress.getLocalHost(), listeningPort)
    private val scope = CoroutineScope(Dispatchers.Default)
    override fun run() {
        while (!interrupted()) {
            val requestSocket = socket.accept()
            val requestStream = ObjectInputStream(requestSocket.getInputStream())
            val responseStream = ObjectOutputStream(requestSocket.getOutputStream())
            scope.launch {
                withContext(Dispatchers.IO) {
                    val request = requestStream.readObject() as Request
                    val response = handleRequest(request)
                    responseStream.writeObject(response)
                }
            }
        }
    }

    /**
     * 试图调用一个对象的方法。
     */
    fun handleRequest(request: Request): Response {
        val remoteRef = request.remoteRef
//        remoteRef.objectLocation == this.objectLocation // 判断地址是不是到达本机的地址。 TODO 如果不是，报错，丢弃或者转发
        val remoteObject = RemoteObjectRegedit.instance[remoteRef.objectId]
        val result = request.method.invoke(remoteObject, *request.args) as Serializable
        return Response(result, callingId = request.callingId)
    }

    fun cancelRequests() {
        scope.cancel()
    }
}

