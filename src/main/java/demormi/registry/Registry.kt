package demormi.registry

import demormi.Remote
import demormi.exception.AlreadyBoundException
import demormi.exception.NotBoundException
import demormi.exception.RemoteException

interface Registry : Remote {
    @Throws(RemoteException::class, NotBoundException::class)
    fun lookup(name: String): Remote

    @Throws(RemoteException::class, AlreadyBoundException::class)
    fun bind(name: String, obj: Remote)

    @Throws(RemoteException::class, NotBoundException::class)
    fun unbind(name: String)

    @Throws(RemoteException::class)
    fun rebind(name: String, obj: Remote)

    @Throws(RemoteException::class)
    fun list(): Array<String>

    companion object {
        const val REGISTRY_PORT = 11099
        // 所有Registry默认共享一个bindings。
//        val bindings = HashMap<String, Remote>()
    }
}