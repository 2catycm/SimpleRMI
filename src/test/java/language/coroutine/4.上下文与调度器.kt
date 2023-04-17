package language.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

// 协程总是运行在一些以 CoroutineContext 类型为代表的上下文中.
//协程上下文 是 元组 (job, 调度器，)

object 上下文与调度器 {
    // 调度器 确定了相关的协程在哪个线程或哪些线程上执行
    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun test(): Unit = runBlocking {
        val superScope = this
        launch { // 运行在父协程的上下文中，即 runBlocking 主协程
//            assertEquals(this.coroutineContext, superScope.coroutineContext)
            println("${this.coroutineContext.job}")
            println("main runBlocking      : I'm working in thread ${Thread.currentThread().name}") // 协程是2，但是线程是main
        }
        launch(Dispatchers.Unconfined) { // 不受限的——将工作在主线程中。 这是高级机制
            println("Unconfined            : I'm working in thread ${Thread.currentThread().name}") //协程是3，线程是main
        }
        launch(Dispatchers.Default) { // 将会获取默认调度器。 与 GlobalScope 的调度器一样。
            println("Default               : I'm working in thread ${Thread.currentThread().name}")
        }
        launch(newSingleThreadContext("MyOwnThread")) { // 将使它获得一个新的线程
            println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
        }
    }
    // 子协程继承
//    当一个父协程被取消的时候，所有它的子协程也会被递归的取消。


    class Activity {
//        private val mainScope = MainScope() // 使用Dispatchers.Main
        private val mainScope = CoroutineScope(Dispatchers.IO) // 使用Dispatchers.Main
        fun destroy() {
            mainScope.cancel()
        }

        fun doSomething() {
            // 在示例中启动了 10 个协程，且每个都工作了不同的时长
            repeat(10) { i ->
                mainScope.launch {
                    delay((i + 1) * 200L) // 延迟 200 毫秒、400 毫秒、600 毫秒等等不同的时间
                    println("Coroutine $i is done")
                }
            }
        }
    }

    @Test
    fun test2() = runBlocking {
        val activity = Activity()
        activity.doSomething() // 运行测试函数
        println("Launched coroutines")
        delay(500L) // 延迟半秒钟
        println("Destroying activity!")
        activity.destroy() // 取消所有的协程
        delay(1000) // 为了在视觉上确认它们没有工作
    }
}