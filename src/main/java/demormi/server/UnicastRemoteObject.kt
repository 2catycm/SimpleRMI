package demormi.server

import demormi.Remote
import demormi.exception.RemoteException
import java.io.Serializable

open class UnicastRemoteObject protected constructor(var port: Int = 0) : Remote, Serializable {
    init {
        exportObject(this, port=port)
    }
    companion object {
        private val interfaceCount = HashMap<String, Int>()
        /**
         * 我是服务器，我是一个 UnicastRemoteObject 对象，
         * 我需要返回给别人一个stub，用来调用我。
         *
         * 1. create a skeleton of the given object ``obj'' and bind with the address ``host:port''
         * 2. return a stub of the object ( Util.createStub() )
         */
        @Throws(RemoteException::class)
        fun exportObject(obj: Remote, host: String="127.0.0.1", port: Int=0): Remote{
//            RemoteObjectRef(host, port, )
//            obj.javaClass.interfaces
//            Skeleton()
            return obj
        }
    }
}