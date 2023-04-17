package language.coroutine.server

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*

class DataPacket(
    val num: Int = 0,
    val name: String = "",
    val source: String = "客户端"
) :Serializable{
    override fun toString(): String {
        return "DataPacket(num=$num, name='$name', source='$source')"
    }
}

class Client {
    val connectAndSayHello = { i: Int ->
        Socket() // 随机的一个端口
            .use { socket: Socket ->
                socket.connect(InetSocketAddress(PORT), 2000)  // 链接服务器的端口
//                val scanner = Scanner(BufferedReader(InputStreamReader(socket.getInputStream())))
//                val writer = PrintWriter(
//                    BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true
//                )
//                writer.printf("Hello World from %d\n", i)
//                println("Hey from $i")
//                println(scanner.nextLine())
                println("就绪。")
                val objectOutputStream = ObjectOutputStream((socket.getOutputStream()))
                objectOutputStream.writeObject(DataPacket(i, "Test ", "Client"))
                objectOutputStream.flush()
                val objectInputStream = ObjectInputStream((socket.getInputStream()))
                println("Hey from ${objectInputStream.readObject()}")
            }
    }
    @Test
    fun testOnce()= runBlocking{
        launch {
            connectAndSayHello(1)
        }.join()

    }

    @Test
    fun testClientConnectionByCoroutine() = runBlocking {
        val num = 10000
//        val num = 100000
        for (i in 1..num) {
            launch {
                connectAndSayHello(i)
            }
        }
    }

    @Test
    fun testClientConnectionByThread() {
        val num = 10000
        //        int num = 100000;
        val threads: MutableList<Thread> = ArrayList()
        for (i in 0 until num) {
            val thread = Thread {
                try {
                    connectAndSayHello(i)
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
            threads.add(thread)
            thread.start()
        }
        for (i in 0 until num) {
            try {
                threads[i].join()
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
        }
        println("Finished")
    }
}