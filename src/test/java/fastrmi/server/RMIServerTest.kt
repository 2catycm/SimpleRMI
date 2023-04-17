package fastrmi.server

import fastrmi.Request
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class RMIServerTest {

    @Test
    fun testHandleRequest() {
        val rmiServer = RMIServer()
        val testObj = "I am a String Object." as String
        val ref = RemoteRef()
        RemoteObjectRegedit.instance[ref.objectId] = testObj
//    val method = testObj.javaClass.interfaces[0].methods[0]
        val method = testObj.javaClass.getMethod("toString")
        println(method)
        val request = Request(remoteRef = ref, method = method, args = arrayOf())
        val response = rmiServer.handleRequest(request)
        println(response)
        assertEquals(testObj, response.result)
        assertEquals(request.callingId, response.callingId)
    }
}