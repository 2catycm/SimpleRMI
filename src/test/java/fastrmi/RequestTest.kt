package fastrmi

import fastrmi.server.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.reflect.Method
import java.util.ResourceBundle
import kotlin.reflect.jvm.internal.impl.load.kotlin.KotlinClassFinder.Result.KotlinClass
import kotlin.reflect.jvm.jvmName

internal class RequestTest {
    interface Upper {
        fun toUpper(): String
    }

    class RemoteString(private val s: String) : Upper, Remote {
        override val remoteObj = RemoteObject(this)
        override fun toUpper(): String {
            return s.uppercase()
        }

    }
    @Test
    fun testReflection() {
        val testObj = ("I am a String Object.")
        val clientClazz1 = String::class.also { println(it) }
        val clientClazz2 = java.lang.String::javaClass.also { println(it) }
        val serverClazz1 = Class.forName(clientClazz1.jvmName)
//        val serverClazz2 = Class.forName(clientClazz2.)
//        assertEquals(serverClazz2, serverClazz1) // 服务端硬着头皮用java
        println(serverClazz1.name)
    }
    // 注意， java.io.NotSerializableException: java.lang.reflect.Method
    // 不能直接发送给 Method。
    @Test
    fun testIsSerializable() {
        val testObj = RemoteString("I am a String Object.")
        //        val method = testObj.javaClass.getMethod("toString") as Method
//        println(method)
        val ref = RemoteRef()
        RemoteObjectRegedit.instance[ref.objectId] = testObj

//        val requestOnClient = Request(remoteRef = ref, method = method, args = arrayOf())
//        // 内存中的中转流
//        val output = ByteArrayOutputStream()
//        ObjectOutputStream(output).use {
//            it.writeObject(requestOnClient)
//        }
//        val input = ByteArrayInputStream(output.toByteArray())
//        ObjectInputStream(input).use {
//            val requestOnServer = it.readObject() as Request
//            assertEquals(requestOnClient, requestOnServer)
//        }
    }
}