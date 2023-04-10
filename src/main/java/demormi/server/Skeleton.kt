package demormi.server

import demormi.Remote
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket


// 在 Server上用来处理  (host, port) 上面的 实现了 interfaceName 的 第 objectKey 个对象
// 的一个线程。具体处理方式是 使用 remoteObj 这个 真实类。
class Skeleton(
    private val remoteObj: Remote,
    private val ref: RemoteObjectRef
) : Thread() {
    init {
//        The Java Virtual Machine exits when the only threads running are all daemon threads.
        // 我们不是 Daemon，而是User Thread。
        this.isDaemon = false
    }
    /**
     * You need to:
        * 1. create a server socket to listen for incoming connections
        * 2. use a handler thread to process each request (use SkeletonReqHandler)
     */
    override fun run() = try {
        // 这个是 TCP 的 Welcome socket, 用来等待连接者的访问。
        val welcomeSocket = ServerSocket(this.ref.location.port, BACKLOG)
        welcomeSocket.use {
            while (!this.isInterrupted) {
                // 我是服务器，某个客户端连接我。
                val accept: Socket = it.accept()
                // 开启一个线程帮我处理这个连接，使用 remoteObj。
                SkeletonReqHandler(accept, remoteObj, ref).start()
            }
        }

    }catch (e: IOException) {
        throw RuntimeException(e)
    }

    override fun toString(): String {
        return "Skeleton(remoteObj=$remoteObj, ref=$ref)"
    }

    companion object {
        const val BACKLOG = 5
    }

}