package myrmi.server

import myrmi.exception.RemoteException
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.io.IOException
import java.io.Serializable
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method


class StubInvocationHandler(private val ref: RemoteObjectRef) :
    InvocationHandler, Serializable {
    init {
        println("Stub created to $ref.host:$ref.port, object key = $ref.objectKey")
    }

    /**
     * 我可能是被服务器创建或者client创建。
     * 但是我就是要向 ref 发送请求。
     *  You need to do:
     * 1. connect to remote skeleton, send method and arguments.
     * 2. get result back and return to caller transparently
     */
    @Throws(RemoteException::class, IOException::class, ClassNotFoundException::class, Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any {
        TODO()
    }
}