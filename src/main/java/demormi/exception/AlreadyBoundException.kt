package demormi.exception

class AlreadyBoundException : Exception {
    constructor() : super() {}
    constructor(s: String?) : super(s) {}
}