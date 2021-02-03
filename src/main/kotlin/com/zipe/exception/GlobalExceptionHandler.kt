package com.zipe.exception

import com.zipe.util.log.logger
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(value = [Exception::class])
    @ResponseBody
    @Throws(Exception::class)
    fun defaultErrorHandler(req: HttpServletRequest, e: Exception): Any? {
        e.printStackTrace()
        logger().error(
            "---DefaultException Handler---Host {} invokes url {} ERROR: {}",
            req.remoteHost,
            req.requestURL,
            e.message
        )
        return e.message
    }

}
