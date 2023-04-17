package language.coroutine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

// 取消协程的执行
// 为什么需要取消?
// 因为启动协程的那个东西本身关闭了，协程的执行结果不再需要。
object 取消与超时 {
    @Test
    fun test1() = runBlocking {
        val job = launch {
            repeat(1000) { i ->
                println("job: I'm sleeping $i ...")
                delay(500L)
//                Thread.sleep(500L) // 这下坏事了，整个线程被阻塞了。
                suspend {
                    Thread.sleep(500L)
                } // 似乎救回来了？但是不太可能啊，这毕竟也只是个普通协程
            }
        }
        delay(1300L) // 延迟一段时间
        println("main: I'm tired of waiting!")
        job.cancel() // 取消该作业 ?怎么做到的
        job.join() // 等待作业执行结束 ? cancel之后不就结束了吗，为什么要join
        println("main: Now I can quit.")
    }

    // 取消必须有配合才能取消。
    // 取消是针对 suspend function的取消，这是基本单位。
    // kotlin库的都是可取消的，自己写的就不一定，下面是不可以取消的例子
    @Test
    fun test2() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (i < 5) { // 一个执行计算的循环，只是为了占用 CPU
                // 每秒打印消息两次
                // 轮询判断要不要继续
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // 等待一段时间
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // 取消一个作业并且等待它结束
        println("main: Now I can quit.")
    }

    // 怎么让 协程操作可取消呢？
    @Test
    fun test3() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            // 检查这个协程组是不是可以用
            while (this.isActive) { // 可以被取消的计算循环
                // 每秒打印消息两次
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // 等待一段时间
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // 取消该作业并等待它结束
        println("main: Now I can quit.")
    }

    // 使用 Exception 来做
    @Test
    fun test4() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            } catch (e: CancellationException) {
                println("外面说您赶紧停了")
//                e.printStackTrace() // 原来是在delay发出的
            } finally {
                println("我就是要不听")
//                this.isActive = true // 我命由我不由天！实际上居然是val
                delay(5000L) // 由于scope not isActive，直接往上面抛异常了。
                println("嘿嘿，我坚持五秒不倒地！") // 实际上这一行不会被执行
//                suspend { Thread.sleep(500) }
            }
        }
        delay(1300L) // 延迟一段时间
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // 取消该作业并且等待它结束
        println("main: Now I can quit.")
    }

    // 问题，finally中不能调用 suspend 函数
    // 关闭一个文件或者资源或者信道的时候，不能调用suspend函数，否则无法关闭！
    // 如果我非要在取消的时候，比如说打开一个日志文件，记录错误信息呢，那还得阻塞！
    @Test
    fun test5() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                    // 情况2，这是个原子操作，事关重大，不得轻易取消
                    withContext(NonCancellable) {
                        println("今天就是天王老子来了我也要数到1000")
//                        repeat(1000){
//                            delay(1)
//                        }
                        delay(1000L)
                        println("1000")
                    }
                }
            } finally {
                // 情况1. 我的协程快结束了，但是我还得做点操作
                withContext(NonCancellable) {
                    println("job: I'm running finally")
                    delay(5000L)
                    println("job: And I've just delayed for 5 sec because I'm non-cancellable")
                }
            }
        }
        delay(1300L) // 延迟一段时间
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // 取消该作业并等待它结束
        println("main: Now I can quit.")
    }
    //?好像没用啊，delay没有数那么久。 不不不，确实数到了1s

    // 超时
    // 取消一个协程是因为他太慢了，可能出问题了
    @Test
    fun test6() = runBlocking {
//        coroutineScope {
//            withTimeout(1300L) {
//                repeat(1000) { i ->
//                    println("I'm sleeping $i ...")
//                    delay(500L)
//                }
//            }
//        }
//
//        println("1300年！我等的你好久！1300年啊！你知道我是怎么过的吗？")
        // 实际上没有打印，因为抛出了异常, 没有捕获处理

        val job = launch {
            withTimeout(1300L) {
                repeat(1000) { i ->
                    println("I'm sleeping $i ...")
                    delay(500L)
                }
            }
        }
        job.join()
        println("1300年！我等的你好久！1300年啊！你知道我是怎么过的吗？")
    }

    // 好的实践，如果失败就null，检查一下的事情。
    @Test
    fun test6_5() = runBlocking {
        val result = withTimeoutOrNull(1300L) {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
            "Done" // 在它运行得到结果之前取消它
        }
        println("Result is $result")
    }

    // Timeout随时可能发生，这个时候有些资源可能来不及释放
    var acquired = 0

    // Note, that incrementing and decrementing acquired counter here from 100K coroutines is completely safe,
    // since it always happens from the same main thread.
    // More on that will be explained in the next chapter on coroutine context.
    class Resource {
        init {
            acquired++
        } // Acquire the resource

        fun close() {
            acquired--
        } // Release the resource
    }

    @Test
    fun test7() = runBlocking {
        runBlocking {
            repeat(100_000) { // Launch 100K coroutines
                launch {
                    val resource = withTimeout(2) { // Timeout of 60 ms
//                        delay(50) // Delay for 50 ms
                        delay(1) // Delay for 50 ms
                        Resource() // Acquire a resource and return it from withTimeout block
                    }
                    resource.close() // Release the resource
                }
            }
        }
        // Outside of runBlocking all coroutines have completed
        println(acquired) // Print the number of resources still acquired

    }

    // 解决方案，在有问题的时候, 对着timeout包裹一个finnaly
    @Test
    fun test8() = runBlocking {
        runBlocking {
            repeat(100_000) { // Launch 100K coroutines
                launch {
                    var resource: Resource? = null // Not acquired yet
                    try {
                        withTimeout(60) { // Timeout of 60 ms
                            delay(50) // Delay for 50 ms
                            resource = Resource() // Store a resource to the variable if acquired
                        }
                        // We can do something else with the resource here
                    } finally {
                        resource?.close() // Release the resource if it was acquired
                    }
                }
            }
        }
// Outside of runBlocking all coroutines have completed
        println(acquired) // Print the number of resources still acquired
    }
}
