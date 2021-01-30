package com.zipe.config

import com.p6spy.engine.spy.appender.MessageFormattingStrategy
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class P6SpyLogger : MessageFormattingStrategy {
    override fun formatMessage(
        connectionId: Int,
        now: String?,
        elapsed: Long,
        category: String?,
        prepared: String?,
        sql: String?,
        url: String?
    ): String {
        return sql?.isBlank().takeIf { it == true }.let {
            (LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")) + " | took $elapsed ms | $category | connection $connectionId \n $sql;")
        }
//        return if (!sql!!.trim().equals("")) {
//            (LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")) + " | took " + elapsed + "ms | "
//                    + category + " | connection " + connectionId + "\n " + sql + ";")
//        } else ""
    }
}
