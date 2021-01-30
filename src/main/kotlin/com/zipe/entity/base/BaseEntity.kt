package com.zipe.entity.base

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class BaseEntity {
    val updateTime: LocalDateTime = LocalDateTime.now()
    val updater: String = ""
    val createTime: LocalDateTime = LocalDateTime.now()
    val creator: String = ""
}
