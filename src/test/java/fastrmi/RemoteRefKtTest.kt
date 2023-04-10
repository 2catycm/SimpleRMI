package fastrmi

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.*
import java.util.*
import java.util.function.IntFunction


internal class RemoteRefKtTest {

    // 我想测试一下stub是不是可以序列化的。
    // 实验结果：可以的。Java实际上序列化了 Invocation Handler.
    @Test
    fun testCreateStubOnServer() {
        val chessBoardImpl = ChessBoardImpl(5,4)
        val stub = createStubOnServer(chessBoardImpl)
        assertNotEquals(chessBoardImpl, stub)
        val s = stub as Serializable
        // 内存中的中转流
        val output = ByteArrayOutputStream()
        ObjectOutputStream(output).use {
            it.writeObject(s)
        }
        val input = ByteArrayInputStream(output.toByteArray())
        ObjectInputStream(input).use {
            val chessBoardStub = it.readObject() as ChessBoard
            println(chessBoardStub)
//            assertEquals(chessBoardStub, stub)
            chessBoardStub.set(2,3, PieceState.Healthy)
            println((chessBoardStub).display())
        }
        //        test2.invoke(null,  ints);
//        test2.invoke(null, *Arrays.stream(ints).mapToObj<Any>(IntFunction<Any?> { i: Int -> i as Any }).toArray())


    }
}