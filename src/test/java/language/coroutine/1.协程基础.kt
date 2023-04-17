package language.coroutine

// 一般概念的协程
//
// kotlin的协程
// - 不是标准库，不是语法
// - 实现异步的关键概念是 挂起函数

// A coroutine is an instance of suspendable computation.
// ? 线程和进程是不可暂停的吗
// 不是可以暂停，是可能暂停。
// 暂停是外部信号暂停，还是系统调用、IO导致暂停，还是时钟信号暂停?
// 1. 与 thread 的相似之处：可以与其他部分代码并发执行
// 2. 不同之处： a coroutine is not bound to any particular thread
//    It may suspend its execution in one thread and resume in another one
// 3.

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread

object 协程基础 {
    // 允许使用 delicate(脆弱美丽的， easily damaged or broken)
    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun test1() {
        val job = GlobalScope.launch { // 在后台启动一个新的协程并继续
            // 不会阻塞（线程），但是会挂起（协程）
            delay(1000L) // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
            println("World! (from coroutine)") // 在延迟后打印输出
        }
        println("Hello,") // 协程已在等待时主线程还在继续
//        Thread.sleep(2000L) // 阻塞主线程 2 秒钟来保证 JVM 存活
        runBlocking { delay(2000L) } // 启动一个阻塞的协程。这个协程挂起2s就是外面线程阻塞2s
    }

    @Test
    fun test2() {
        val thr = thread {
            Thread.sleep(1000L)
            println("World! (from thread) ") //
        }

        println("Hello,") // 协程已在等待时主线程还在继续
        Thread.sleep(2000L) // 阻塞主线程 2 秒钟来保证 JVM 存活
    }

    @Test
    fun test2_5() {
        val thr = thread {
            val job = GlobalScope.launch {
//            val job = launch {
                // 应当不只是阻塞了协程，阻塞了整个线程。
//                Thread.sleep(1000L)
                delay(100L)
                println("World! (from launch) ") //
            }
            Thread.sleep(500L)
            println("Hello, ") //
            runBlocking {
                // 因为job是挂起函数，而不是阻塞函数
                job.join()
            }
            // 理论上：job先启动了，等待1000L的同时让主线程等待了。
            // 实际上Hello完才进入协程。
            // 测交实验：job调成delay 100L， 结果应当不变。
            // 实验观察：回答错误！
        }
        thr.join()
    }

    // 1. GlobalScope 叫做顶级协程 ？
    // 是不是无论在多门深层召唤他都是召唤最顶层那一个
    // 2. 忘记对 新 协程的引用，会导致继续运行
    // ？ 不是说不引用不等待JVM就退出了吗
    // 3. 协程中的代码挂起了怎么办？
    // 那就挂起咯，有什么所谓？
    // 4. 启动了太多协程导致内存不足怎么办？
    // 5. 手动保持对协程的引用，否则容易出错。

    // 新概念：结构化并发
    // 1. 线程是全局的资源，没有分组的概念。
    // 2. 协程分为 不同的 Scope
    // 3. runBlocking是一个构建器，
    //   同一个构建器是一个scope吗？
    // 构建器内启动的写成不需要join，因为整个scope内所有协程一起被构建器join
    //
    @Test
    fun test3() = runBlocking {
        this.launch {
            delay(1000L)
            println("我的作用域是$this")
            println("World! ")
        }
        println("我的作用域是$this")
        println("Hello, ")
    }

    // `coroutineScope`是一个作用域。
    // 和runBlocking一样都会等待里面的协程。
    // runBlocking会阻塞外面的线程，这意味这个这个线程卡住了。
    // coroutineScope只是挂起当前协程，这意味着协程所以来的线程可以去做别的协程。
    @Test
    fun test4() = runBlocking {
        launch {
            delay(1000L)//4
            println("我的作用域是$this, Task from runBlocking")
        }
        coroutineScope {
            launch {
                delay(500L)//2
                println("我的作用域是$this, Task from nested launch")
            }
            delay(100L) //1
            println("我的作用域是$this, Task from coroutine scope") // 这一行会在内嵌 launch 之前输出
        }
        // 注意要到这一步之前，协程会被 coroutineScope 挂起！
        println("我的作用域是$this, 整个大scope结束！") // 3
    }

    // suspend 修饰符的新函数
    // 将launch里面的操作提取到独立的函数中。
    // ?本来launch传递的就是suspend lambda表达式
    // 1. 协程Scope中可以使用挂起函数， 挂起函数会阻塞协程
    // 2. 挂起函数本身可以使用挂起函数。
    // 3. 调用挂起函数本身只是把代码抄过去了，至于异步还是同步：
    //  launch调用的话，就是新启动一个协程，挂起了新协程不会影响旧协程；
    //  普通调用的话，就是等待运行结束。
    suspend fun doWorld() {
        println("我在$this") // 没有打印出scope，而是打印出外面那个object
        delay(1000L)
        println("World!")
    }

    @Test
    fun test5() = runBlocking {
        launch {
            doWorld()
        }
        println("Hello, ")
    }
    // 如果提取出的函数包含一个在当前作用域中调用的协程构建器的话，该怎么办？
    // 这个问题啥意思？

    // 协程很轻量
    @Test
    fun test6() {
        val start = System.currentTimeMillis()
        runBlocking {
            repeat(100_000) { // 启动大量的协程
                launch {
                    delay(5000L)
                    print(".")
                }
            }
        }
        println("Time: ${System.currentTimeMillis() - start}ms")
        // 6034ms
    }

    //现在，尝试使用线程来实现。会发生什么？
    @Test
    fun test6_5() {
        val start = System.currentTimeMillis()
//        val k = 100_000
        val k = 100
//        repeat(100_000) { // 启动大量的线程
        val threads = (1..k).map {
            thread {
                Thread.sleep(5000L)
                print(".")
            }
        }.forEach { it.join() }

        println("Time: ${System.currentTimeMillis() - start}ms")
        //
    }

    // 全局协程像守护线程
    // ? 什么是守护线程
    //  是Java的概念，Java线程分为 用户线程和守护线程
    //  用户线程很重要，是被守护的，如果不退出，JVM就不会退出。
    // 守护线程只是为了用户线程而存在，如果用户线程全部走了，那么JVM就杀死守护线程退出。
    // 垃圾回收线程是一个守护线程。

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun test7() = runBlocking {
        GlobalScope.launch {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        } // 没有把新协程加入到runBlocking建立的scope，所以不会join。
        delay(1300L) // 在延迟后退出
    }
    var aThread:Thread? = null
    @Test
    fun test7_5() {
//        thread(isDaemon = true) {
//        thread(isDaemon = false) { ？好像还是退出了啊
        aThread = thread(isDaemon = false) { // 没区别
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                Thread.sleep(500L)
            }
        }
        Thread.sleep(1300L) // 在延迟后退出
    }
    // 注意特例！junit单元测试会关闭非守护进程！所以实验失败。
    // https://cloud.tencent.com/developer/article/1965585


}

// 很好，并没有退出，实验成功了。
fun main(){
    thread(isDaemon = false) { // 没区别
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            Thread.sleep(500L)
        }
    }
    Thread.sleep(1300L) // 在延迟后退出
}