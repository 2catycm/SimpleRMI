package fastrmi.server

import java.io.Serializable
import java.net.InetSocketAddress
import java.util.*

/**
 * 标识一个远程对象的引用。
 * RemoteRef是一个二元组 （网络主机位置，主机上的内存位置）
 * 主构造器：若无参，可以自动构造服务器上的一个新的远程对象。
 */
data class RemoteRef(
    // 指代对象原本所在的位置。
    val objectLocation: InetSocketAddress = RemoteObjectRegedit.instance.objectLocation,
    // 每次创建一个对象，注册在RMI服务器中。
    // 1. 不能使用hashCode， hashCode只是判断equals的一种简单方法，可能哈希冲突；不能判断对象的内存地址。
    // 2. 无法获得java对象的内存地址。
    // 3. 于是使用UUID。
    val objectId: UUID = UUID.randomUUID(),
) : Serializable {

}
