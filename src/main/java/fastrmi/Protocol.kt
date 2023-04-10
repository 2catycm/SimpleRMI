package fastrmi

import java.io.Serializable
import java.lang.reflect.Method
import java.util.*

data class Request(
    val callingId:UUID = UUID.randomUUID(),
    val method: Method,
    val args: Array<Serializable>,
):Serializable{
    // https://blog.csdn.net/sANsHiErHuA/article/details/112729026
    val requestTime:Long = System.currentTimeMillis()

    // 因为有array存在，所以得重写equals。
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Request

        if (callingId != other.callingId) return false
        if (method != other.method) return false
        if (!args.contentEquals(other.args)) return false
        if (requestTime != other.requestTime) return false

        return true
    }

    override fun hashCode(): Int {
        return callingId.hashCode()
    }
}
data class Response(
    val callingId:UUID,
    val result:Serializable
):Serializable{
    val responseTime:Long = System.currentTimeMillis()
    override fun hashCode(): Int {
        return callingId.hashCode()
    }
}