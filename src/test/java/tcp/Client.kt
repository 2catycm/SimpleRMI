package tcp

import sun.rmi.transport.tcp.TCPEndpoint
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.net.SocketAddress
import javax.net.SocketFactory

fun main(){
    val createSocket = Socket("localhost", 10900)
    createSocket.use {socket->
        val data = DataPacket(1, "Hello world")
        ObjectOutputStream(socket.getOutputStream()).use {
            it.writeObject(data)
            println("客户端：$data")
            ObjectInputStream(socket.getInputStream()).use {
//                println("服务端：${it.readObject()}")
            }
        }

    }
}