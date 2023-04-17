package language.coroutine.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.*
import java.net.ServerSocket

const val PORT = 4090

/**
 * 协程服务器
 *
 * 负责启动 TCP / UDP 监听服务，单例模式实现
 */
class CoroutineServer (private val port: Int = PORT ):Thread(){
    private var globalTcpSocket = ServerSocket(port)
    /**
     * runBlocking表示线程调用run的时候，run内部的所有协程结束之前run函数不会结束。
     * runBlocking内部是CoroutineScope, 所以里面可以使用this.launch这种方法
     * launch{}这是kotlin的dsl语法，表示把后面的{}lambda函数传递给launch方法的最后一个参数
     * launch的语义是启动一个协程，和后面的内容并发运行。返回值是Job类型，类似于Thread类型，可以join。
     *
     * coroutineScope 和 runBlocking的区别：后者是普通函数，前者是可暂停函数。
     *
     *  启动100000个协程处理100000个socket连接，应该比启动线程所消耗的内存少的多。
     */
    override fun run() = runBlocking {
//        super.run() //https://coderanch.com/t/250644/certification/call-super-run 千万不能调用这个。
//        log.debug("Thread is running. ")
        while (!interrupted()) {
//            log.debug("before handle ")
            val byteArray = ByteArray(200)
            val newSocket = withContext(Dispatchers.IO) {
                globalTcpSocket.accept()
            }
            launch {
//                log.debug("Goes into the launch. ")
                withContext(Dispatchers.IO) {
//                    val tcpScanner: Scanner =
//                        Scanner(BufferedReader(InputStreamReader(newSocket.getInputStream())))
//                    val tcpWriter: PrintWriter = PrintWriter(
//                        newSocket.getOutputStream(), true
//                    )
////                    log.debug("Get the scanner and writer successfully")
////                    log.debug(newSocket.toString())
//                    while (tcpScanner.hasNextLine()) {
//                        val nextLine = tcpScanner.nextLine()
//                        tcpWriter.println(nextLine)
////                        log.info(nextLine)
//                    }
                    println("就绪。")
                    val objectInputStream = ObjectInputStream((newSocket.getInputStream()))
                    val objectOutputStream = ObjectOutputStream((newSocket.getOutputStream()))
                    while (objectInputStream.available()!=0){
                        val readObject = objectInputStream.readObject() as DataPacket
                        val newObject = DataPacket(readObject.num, readObject.name, "Server")
                        objectOutputStream.writeObject(newObject)
                        objectOutputStream.flush()
                        println("Hello from $newObject")
                    }
                    println("Finished")
                }
            }
//            log.debug("Handle new socket. ")
        }
//        log.debug("Interrupted!")
    }
}


fun main() {
//    CoroutineServer().start() //开一个线程专门跑整个NetworkCore
    CoroutineServer().start() //不启动新线程，直接跑
}