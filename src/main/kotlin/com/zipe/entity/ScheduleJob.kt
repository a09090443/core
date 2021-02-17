package com.zipe.entity

import com.zipe.enum.ScheduleEnum
import com.zipe.enum.ScheduleJobStatusEnum
import org.quartz.JobDataMap
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
data class ScheduleJob(

    val id: Int = 0,

    @Id
    var jobName: String = "",

    var jobGroup: String = "",

    var jobDescription: String = "",

    var jobClass: String = "",

    @Enumerated(EnumType.STRING)
    var status: ScheduleJobStatusEnum? = null,

    @Enumerated(EnumType.STRING)
    var timeUnit: ScheduleEnum? = null,

    var repeatInterval: Int = 0,

    var repeatTimes: Int = 0,

    @Column(columnDefinition = "TIMESTAMP")
    var startTime: LocalDateTime? = null,

    @Column(columnDefinition = "TIMESTAMP")
    var endTime: LocalDateTime? = null,

    @Transient
    var jobDataMap: JobDataMap = JobDataMap()

) : Serializable
