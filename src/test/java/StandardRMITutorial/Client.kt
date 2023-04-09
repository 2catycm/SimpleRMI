import java.io.Serializable
import java.math.BigDecimal
import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry


// 接口，client和server都有的内容

// Server 有 Compute的实现，Client永远没有这个实现

// Client 有 Task的实现，Server可以获得这个实现。

internal class Pi(val digits: Int) : Task<BigDecimal>, Serializable {
    override fun execute(): BigDecimal {
        val scale = digits + 5
        val arctan1_5 = arctan(5, scale)
        val arctan1_239 = arctan(239, scale)
        val pi: BigDecimal = arctan1_5.multiply(FOUR).subtract(
            arctan1_239
        ).multiply(FOUR)
        return pi.setScale(
            digits,
            BigDecimal.ROUND_HALF_UP
        )
    }

    private fun arctan(inverseX: Int, scale: Int): BigDecimal {

        var result: BigDecimal
        var numer: BigDecimal
        var term: BigDecimal
        val invX: BigDecimal = BigDecimal.valueOf(inverseX.toDouble())
        val invX2: BigDecimal = BigDecimal.valueOf(inverseX.toDouble() * inverseX)
        numer = BigDecimal.ONE.divide(
            invX,
            scale, roundingMode
        )

        result = numer
        var i = 1
        do {
            numer = numer.divide(invX2, scale, roundingMode)
            val denom = 2 * i + 1
            term = numer.divide(
                BigDecimal.valueOf(denom.toLong()),
                scale, roundingMode
            )
            result = if (i % 2 != 0) {
                result.subtract(term)
            } else {
                result.add(term)
            }
            i++
        } while (term.compareTo(BigDecimal.ZERO) != 0)
        return result
    }


    companion object {
        private const val serialVersionUID = 227L
        val FOUR = BigDecimal.valueOf(4)
        val roundingMode = BigDecimal.ROUND_HALF_EVEN
    }
}


fun main(args: Array<String>) {
//    if (System.getSecurityManager() == null) System.setSecurityManager(SecurityManager())
    try {
        val name = "engine"
//        val registry: Registry = LocateRegistry.getRegistry(args[0])
        val registry: Registry = LocateRegistry.getRegistry("localhost", 8080)
        val comp = registry.lookup(name) as Compute
        val task = Pi(100)
        val pi = comp.executeTask(task)
        println(pi)
    } catch (e: Exception) {
        System.err.println("ComputePi exception:")
        e.printStackTrace()
    }
}