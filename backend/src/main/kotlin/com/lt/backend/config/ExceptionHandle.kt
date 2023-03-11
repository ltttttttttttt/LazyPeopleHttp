package com.lt.backend.config

import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import java.io.PrintWriter
import java.io.StringWriter
import java.net.UnknownHostException


/**
 * 创    建:  lt  2019/2/22--14:25    lt.dygzs@qq.com
 * 作    用:  全局捕获异常处理类
 * 注意事项:
 */
@ControllerAdvice
class ExceptionHandle {

    @ExceptionHandler(value = [Throwable::class])
    @ResponseBody
    fun handle(e: Throwable): String {
        println(_getThrowableMessage(e))
        throw e
    }

    /**
     * 获取异常的日志信息
     */
    fun _getThrowableMessage(t: Throwable?): String {
        if (t == null) {
            return ""
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        var throwable = t
        while (throwable != null) {
            if (throwable is UnknownHostException) {
                return ""
            }
            throwable = throwable.cause
        }
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        t.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }
}