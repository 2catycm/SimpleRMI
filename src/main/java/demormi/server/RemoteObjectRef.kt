package demormi.server

import demormi.Remote
import java.io.Serializable

data class TCPEndpoint(
    var host: String,
    var port: Int,
) {
}

// 表示 location 上面的 实现了 interfaceName 的 第 objectKey 个对象
class RemoteObjectRef(
    var location: TCPEndpoint,
    var interfaceName: Array<String>,
    var objectKey: Int,
) : Serializable, Remote {

    // 复制构造
//    constructor(ref: RemoteObjectRef) : this(ref.host, ref.port, ref.objectKey, ref.interfaceName)
    constructor(realObj:Remote, location: TCPEndpoint):this(location, realObj.javaClass.interfaces.map { it.toString() }.toTypedArray(), 0){

    }

    override fun toString(): String {
        return "RemoteObjectRef(location=$location, interfaceName='$interfaceName', objectKey='$objectKey')"
    }
}