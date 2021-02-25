package com.zipe.exception

import com.zipe.util.log.logger
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletRequest

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(value = [Exception::class])
    @Throws(Exception::class)
    fun defaultErrorHandler(req: HttpServletRequest, e: Exception): Any? {
        logger().error(
            "---DefaultException Handler---Host {} invokes url {} ERROR: {}",
            req.remoteHost,
            req.requestURL,
            e.message
        )
        return e.message
    }

}
