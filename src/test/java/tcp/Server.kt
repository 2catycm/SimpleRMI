package tcp

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.ServerSocket
import javax.net.ServerSocketFactory
import kotlin.concurrent.thread

//class Server {
//}
class DataPacket(
    val test: Int = 0,
    val name: String = "",
    val source: String = "客户端"
) :Serializable{
    override fun toString(): String {
        return "DataPacket(test=$test, name='$name', source='$source')"
    }
}

fun main() {
    val server = ServerSocketFactory.getDefault().createServerSocket(10900)
    while (true) {
        val accept = server.accept()
        println("新的连接建立了。")
         thread {
            ObjectInputStream(accept.getInputStream()).use {
                it.readObject().takeIf { it is DataPacket }?.let { it ->
                    val obj = it as DataPacket
                    println("服务器收到了对象$obj")
                    ObjectOutputStream(accept.getOutputStream()).use {
                        val obj = DataPacket(obj.test, obj.name, "服务端")
//                        it.writeObject(obj)
                    }
                }
            }
             println("Finished")
//            accept.close()
        }.start()

    }
}