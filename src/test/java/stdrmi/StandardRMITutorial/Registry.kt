package stdrmi.StandardRMITutorial

import java.rmi.registry.LocateRegistry

fun main(){
    val registry = LocateRegistry.createRegistry(8080)
    while (!Thread.interrupted()){
        Thread.sleep(2000)
        println("Server is alive. ")
    }
}