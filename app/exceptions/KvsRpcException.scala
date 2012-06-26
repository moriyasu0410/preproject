package exceptions

/**
 * KVSへのRPCでの例外.
 */
class KvsRpcException(val statusCode: Int, val body: String) extends Exception {
}
