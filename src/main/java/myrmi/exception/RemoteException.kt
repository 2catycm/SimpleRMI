package myrmi.exception

import java.io.IOException

class RemoteException(override val message: String="", override val cause: Throwable?=null) : IOException() {

}
fun main(){
    throw RemoteException("what", cause=IOException("ex"))
}