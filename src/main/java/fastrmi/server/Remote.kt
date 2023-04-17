package fastrmi.server

/**
 * 远程对象接口。实现了这个接口的对象获得了被远程调用的能力。
 */
interface Remote {
    val remoteObj: RemoteObject
    val objectId
        get() = remoteObj.remoteRef.objectId
}

/**
 * 用法：对于要变成远程对象的类Q， Q必须 implements Remote。除此之外，需要用下列方法之一来获得功能。
 * 1. 让Q extends RemoteObject， 构造器中调用无参构造器super()方法。
 * 2. 使用组合替代继承，在Q中 写 private RemoteObject ro = new RemoteObject(); 然后使用 RemoteObject 实现 Remote 接口的方法。
 *    目前Remote 接口没有方法。所以不需要然后。这种方法是为了让您可以自由的选择Q继承什么类。
 * 3. 在kotlin中，可以很简洁地实现2.逻辑。您可以让 Q : Remote by RemoteObject(), T1, T2,..., Tn
 *      其中 T1-Tn是您想让Q继承或者实现的其他类。
 *
*  RemoteObject 构造时，自动获取一个新的RemoteRef，并且将对象自身注册到RMI服务器中。
 */
open class RemoteObject (self: Remote) {
    val remoteRef = RemoteRef()
    init {
        RemoteObjectRegedit.instance[remoteRef.objectId] = self
    }

}
