package language.coroutine

import kotlinx.coroutines.*
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    // 创建一个ServerSocket，监听8080端口
    val server = ServerSocket(8080)
    // 创建一个Socket，用于连接到服务器
    val client = Socket()
    // 创建一个协程作用域，包含两个协程
    val job = GlobalScope.launch {
        // 第一个协程：服务器端
        launch {
            while (true) {
                // 等待客户端连接
                val socket = withContext(Dispatchers.IO) {
                    server.accept()
                }
                launch {
                    // 获取输入输出流

                    val input = ObjectInputStream(socket.getInputStream())
                    val output = ObjectOutputStream(socket.getOutputStream())
                    // 读取客户端发送的消息并打印到控制台上
                    val message = input.readObject() as String
                    println("Server received message: $message")
                    // 向客户端发送消息
                    output.writeObject("Server received message: $message")
                }
            }
        }
        // 第二个协程：客户端
        launch {
            // 连接到服务器
            client.connect(InetSocketAddress("localhost", 8080))
            // 获取输入输出流
            val input = ObjectInputStream(client.getInputStream())
            val output = ObjectOutputStream(client.getOutputStream())
            // 向服务器发送消息并打印到控制台上
            output.writeObject("Hello from client")
            println("Client received message: ${input.readObject()}")
        }
    }
    runBlocking { job.join() }
}