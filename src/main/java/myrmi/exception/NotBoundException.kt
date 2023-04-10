package myrmi.exception

class NotBoundException : Exception {
    constructor() : super() {}
    constructor(s: String?) : super(s) {}
}