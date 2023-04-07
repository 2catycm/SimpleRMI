import org.junit.jupiter.api.Test
import java.lang.StringBuilder
import java.rmi.Remote
import java.rmi.RemoteException
import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry
import java.rmi.server.UnicastRemoteObject
import java.util.*


object StandardRMITest {
    @Test
    fun client(): Unit {
        val r = LocateRegistry.getRegistry("localhost", 8080)
        val chessboard = r.lookup("chessboard") as ChessBoard // 不是寻找到服务器的object，而是动态产生一个代理object。实际代码是在服务器执行的。
//        println(chessboard.toString()) // 调用的是Stub对象的 toString(), 因为名字重复了，优先是代理的方法
        println(chessboard.display())
        chessboard[2, 3] = PieceState.Protected
    }

    @Test
    fun server(): Unit {
        val remoteObject: ChessBoard = ChessBoardImpl(5, 10)
        val registry: Registry = LocateRegistry.createRegistry(8080)
        registry.bind("chessboard", remoteObject)
        remoteObject[0, 0] = PieceState.Destroyed
        println("RMI server started")
        while (remoteObject[2, 3] != PieceState.Protected) {
            Thread.sleep(300)
        }
        println("Find Client!")
        println(remoteObject)
    }
}

enum class PieceState {
    Nothing,
    Healthy,
    Destroyed,
    Protected, ;

    override fun toString(): String {
        return this.ordinal.toString()
    }
}

interface ChessBoard : Remote {
    @get:Throws(RemoteException::class)
    val rows: Int

    @get:Throws(RemoteException::class)
    val cols: Int

    // 运算符重载 []
    @Throws(RemoteException::class)
    operator fun get(x: Int, y: Int): PieceState

    @Throws(RemoteException::class)
    operator fun set(x: Int, y: Int, value: PieceState)
    @Throws(RemoteException::class)
    fun display(): String

}

// 服务器上的
class ChessBoardImpl(
    override val rows: Int,
    override val cols: Int
) : UnicastRemoteObject(), ChessBoard {
    private val board = Array<PieceState>(rows * cols) { PieceState.Nothing }

    override operator fun get(x: Int, y: Int): PieceState {
        return board[x * cols + y]
    }

    override operator fun set(x: Int, y: Int, value: PieceState) {
        board[x * cols + y] = value
    }


    override fun display():String{
        val stringBuilder = StringBuilder()
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                stringBuilder.append("${this[i, j]} ")
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }
    override fun toString(): String {
        return display()
    }
}