package com.pollvote.network.exceptions

import com.pollvote.network.Constant
import java.io.IOException

/**
 * This is No internet exception class which directly implemented in service generator
 */

class NoInternetException : IOException() {

    override val message: String?
        get() = Constant.NO_INTERNET.toString()


}