package fastrmi

import java.io.IOException
import java.io.InvalidClassException
import java.lang.Exception

open class RMIException: Exception()

// 表示发生在远程服务器上的异常。其他RMI异常都是发生在本地的。
class RemoteException(override val message: String="远程服务器上方法执行发生异常。", override val cause: Throwable?=null) : RMIException() {

}
//
class ArgumentsNotSerializableException(override val message: String="方法无法远程执行，因为参数无法序列化", override val cause: Throwable?=null):RMIException()

class NullReferenceException(override val message: String="", override val cause: Throwable?=null):RMIException()