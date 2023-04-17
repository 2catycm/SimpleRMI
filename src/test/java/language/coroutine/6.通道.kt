package language.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.junit.jupiter.api.Test

object ChannelTest{
    @Test
    fun test1() = runBlocking {
        val channel = Channel<Int>()
        launch {
            // 这里可能是消耗大量 CPU 运算的异步逻辑，我们将仅仅做 5 次整数的平方并发送
            for (x in 1..5) channel.send(x * x)
        }
// 这里我们打印了 5 次被接收的整数：
        repeat(5) { println(channel.receive()) }
        println("Done!")
    }
}