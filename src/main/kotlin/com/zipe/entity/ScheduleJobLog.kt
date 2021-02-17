package com.zipe.entity

import com.zipe.enum.ScheduleJobStatusEnum
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
data class ScheduleJobLog(

    @Id
    var id: Int = 0,

    var jobName: String = "",

    var jobDescription: String = "",

    @Enumerated(EnumType.STRING)
    var status: ScheduleJobStatusEnum? = null,

    var startTime: LocalDateTime? = null,

    var endTime: LocalDateTime? = null,

    var message: String = ""

) : Serializable
