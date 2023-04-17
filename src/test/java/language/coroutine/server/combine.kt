package language.coroutine.server

import kotlinx.coroutines.*
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.channels.SocketChannel

@OptIn(DelicateCoroutinesApi::class)
fun main() {
//    // 创建一个ServerSocket，监听8080端口
    val server = ServerSocket(8080)
//    // 创建一个Socket，用于连接到服务器
    val client = Socket()
//    val server = SocketChannel
    // 创建一个协程作用域，包含两个协程
//    val job = GlobalScope.launch {
    runBlocking {
        // 第一个协程：服务器端
//        launch (Dispatchers.IO){
        launch (newSingleThreadContext("Server")){
            while (true) {
                // 等待客户端连接
                println("Waiting for connection")
                val socket = withContext(Dispatchers.IO) {
                    server.accept()
                }.also { println("accepted!") }
//                val socket = withContext(Dispatchers.IO) {
//                    server.accept()
//                }
//                val socket = suspend {  server.accept() }
                launch (Dispatchers.IO){
                    // 获取输入输出流

                    val input = ObjectInputStream(socket.getInputStream())
                    val output = ObjectOutputStream(socket.getOutputStream()).also { println("get string") }
                    // 读取客户端发送的消息并打印到控制台上
                    val message = input.readObject() as String
                    println("Server received message: $message")
                    // 向客户端发送消息
                    output.writeObject("Server received message: $message")
                }
            }
        }
        // 第二个协程：客户端
//        launch (Dispatchers.Unconfined){
        launch (newSingleThreadContext("Client")){
            // 连接到服务器
            withContext(Dispatchers.IO) {
                client.connect(InetSocketAddress("localhost", 8080))
            }.also { println("Connected") }
            // 获取输入输出流
            val input = withContext(Dispatchers.IO) {
                ObjectInputStream(withContext(Dispatchers.IO) {
                    client.getInputStream()
                })
            }
            val output = withContext(Dispatchers.IO) {
                ObjectOutputStream(withContext(Dispatchers.IO) {
                    client.getOutputStream()
                })
            }
            // 向服务器发送消息并打印到控制台上
            withContext(Dispatchers.IO) {
                output.writeObject("Hello from client")
            }
            println("Client received message: ${
                withContext(Dispatchers.IO) {
                    input.readObject()
                }
            }")
        }
    }
}