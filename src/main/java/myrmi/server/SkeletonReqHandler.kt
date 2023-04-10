package myrmi.server

import myrmi.Remote
import java.io.IOException
import java.net.Socket

// 我是服务器，我要处理请求，使用obj真实对象计算，然后发送答案。
class SkeletonReqHandler(private val socket: Socket, private val obj: Remote, private val ref: RemoteObjectRef) : Thread() {
    /**
     * You need to:
     * 1. handle requests from stub, receive invocation arguments, deserialization
     * 2. get result by calling the real object, and handle different cases (non-void method, void method, method throws exception, exception in invocation process)
     * Hint: you can use an int to represent the cases: -1 invocation error, 0 exception thrown, 1 void method, 2 non-void method
     */
    override fun run() {
        var objectKey: Int
        var methodName: String
        var argTypes: Array<Class<*>?>
        var args: Array<Any?>
        var result: Any

        try {
            socket.getInputStream().use { socket.getOutputStream().use {

            } }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun toString(): String {
        return "SkeletonReqHandler(socket=$socket, obj=$obj, ref=$ref)"
    }


}