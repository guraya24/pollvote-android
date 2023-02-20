package  com.pollvote.network

/**
 * This is basic response for all API responses
 */
class WebResponse<T>(status: Int, data: T?, errorMsg: String?) {
    var status: Int
    var data: T?
    var errorMsg: String? = null

    init {
        this.status = status
        this.data = data
        this.errorMsg = errorMsg
    }
}