package myrmi.exception

class AlreadyBoundException : Exception {
    constructor() : super() {}
    constructor(s: String?) : super(s) {}
}