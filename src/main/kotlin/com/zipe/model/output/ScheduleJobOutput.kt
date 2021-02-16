package com.zipe.model.output

import org.quartz.JobDataMap
import java.time.LocalDateTime

data class ScheduleJobOutput(
    val jobName: String = "",
    val group: String = "",
    val description: String = "",
    val classPath: String = "",
    var status: Int = 0,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val time: String = "",
    val interval: Int = 0,
    val repeatTimes: Int = 0,
    val timeUnit: Int = 0,
    val jobDataMap: JobDataMap? = JobDataMap(),
    var errorMessage: String? = null
)
