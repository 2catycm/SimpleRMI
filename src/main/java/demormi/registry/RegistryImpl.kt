package demormi.registry

import demormi.Remote
import demormi.exception.AlreadyBoundException
import demormi.exception.NotBoundException
import demormi.exception.RemoteException
import kotlin.system.exitProcess

class RegistryImpl(port: Int) : Registry {
    private val bindings = HashMap<String, Remote>()

    init {
        // Skeleton 用来处理我这个对象的请求。
        // 我被实例化的时候，Skeleton 线程就一直帮我监听操作。
//        val skeleton = Skeleton(this, "127.0.0.1", port, 0)
//        skeleton.start()
    }

    @Throws(RemoteException::class, NotBoundException::class)
    override fun lookup(name: String): Remote {
//        println("RegistryImpl: lookup($name)")
        if (name !in bindings) throw NotBoundException("$name is not found on this registry. ")
        return bindings[name]?: throw NotBoundException("null object was bound on $name. ")
    }

    @Throws(RemoteException::class, AlreadyBoundException::class)
    override fun bind(name: String, obj: Remote) {
//        println("RegistryImpl: bind($name)")
        if (name in bindings) throw AlreadyBoundException("$name already exists on this registry. ")
        bindings[name]= obj
    }

    @Throws(RemoteException::class, NotBoundException::class)
    override fun unbind(name: String) {
//        println("RegistryImpl: unbind($name)")
        if (name !in bindings) throw NotBoundException("$name is not found on this registry, so cannot unbind it.")
        bindings.remove(name)
    }

    @Throws(RemoteException::class)
    override fun rebind(name: String, obj: Remote) {
//        println("RegistryImpl: rebind($name)")
        bindings[name] = obj
    }

    @Throws(RemoteException::class)
    override fun list(): Array<String> {
        return bindings.keys.toTypedArray()
    }
}
fun main(args: Array<String>) {
    val regPort = if (args.isNotEmpty()) args[0].toInt() else Registry.REGISTRY_PORT
    val registry: RegistryImpl
    try {
        registry = RegistryImpl(regPort)
    } catch (e: RemoteException) {
        exitProcess(1)
    }
    System.out.printf("RMI Registry is listening on port %d\n", regPort)
}