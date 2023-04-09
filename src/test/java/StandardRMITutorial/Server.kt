import java.lang.Exception
import java.rmi.server.UnicastRemoteObject
import java.rmi.Remote
import java.rmi.RemoteException
import java.rmi.registry.LocateRegistry


// 接口，client和server都有的内容

/**
 * 本地Object，是来自于Client的，但是里面的执行逻辑希望在Server上执行。
 * ？需要实现Serializable吗？
 */
interface Task<T> {
    fun execute(): T
}

/**
 * 远程对象，在Server上。Client通过stub与之连接，提交任务。
 */
interface Compute : Remote {
    /**
     * 当被执行是，RMI自动将参数t序列化，序列化的同时附带子类信息+子类代码。
     * RMI允许作为参数的类型有：
     * - 原始类型：Pass by value。
     * - Serializable：Pass by value。除了static和transient（/ˈtrænziənt/）的所有字段。 序列化行为可以被“class-by-class basis”覆盖。
     *  注意，状态不会被远程修改。
     *   ？原始类型和Serializable的数组、集合呢？
     * - Remote：Pass by reference，传递的是stub
     */
    @Throws(RemoteException::class)
    fun <T> executeTask(t: Task<T>): T
}

// Server 有 Compute的实现，Client永远没有这个实现

internal class ComputeEngine : Compute {
    override fun <T> executeTask(t: Task<T>): T {
        return t.execute()
    }
}

fun main(args: Array<String>) {
    //安装安全管理器
    // 作用：阻止不信任源下载代码。或者阻止下载下来的代码的文件系统、操作系统操作。
    // 如果不设置，默认策略是只从本地的代码获取，如果没有的话就失败。？本地代码和远程名字一样，但是版本不一样怎么办
//    if (System.getSecurityManager() == null) System.setSecurityManager(SecurityManager())
    try {
        // 创建对象
        val computeEngine = ComputeEngine() as Compute
        // 弃用了RMIC+静态生成。现在stub都是动态代理。 注意不要用没有port的那个方法，那个是static的。
        // The proxy's invocation handler is a RemoteObjectInvocationHandler instance constructed with a RemoteRef.
        // port是服务器TCP监听socket的port。0 表示让操作系统分配。
        // 每一个stub都会有不一样的TCP socket。
        val stub = UnicastRemoteObject.exportObject(computeEngine, 0) as Compute
        // 命名服务。
        // 一般是第一个RemoteObject必须要用Registry获得，而其他的Remote对象可以在方法返回值中获得。
        val registry = LocateRegistry.getRegistry("localhost", 8080)
        // 如果已经有engine，原本的会被丢弃。 bind则会抛出异常。
        // 在Registry中，stub被复制，而不是使用原本的stub
        // 本host机上（？相同ip还是相同port）的Registry，允许使用bind，rebind，unbind，lookup。而非本机的只能lookup。
        registry.rebind("engine", stub)
        // bound 是 bind的过去分词。
        println("ComputeEngine bound")
        // main进程结束。但是，因为对于Client来说Registry可见，因此ComputeEngine的进程（线？）保持
        // ?意思是，client有Registry的（远程）引用，Registry有ComputeEngine的（远程）引用，根据垃圾回收规则，不得回收。
    } catch (e: Exception) {
        // 在exportObject这一步可能会有 RemoteException：端口无法绑定
        System.err.println("Engine error: ")
        e.printStackTrace()
        // 有些应用中，可以重试、换一台服务器重试。
    }
}
// Client 有 Task的实现，Server可以获得这个实现。




