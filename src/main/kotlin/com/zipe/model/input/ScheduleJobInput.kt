package com.zipe.model.input

import com.zipe.enum.ScheduleEnum
import org.quartz.JobDataMap

data class ScheduleJobInput(
    val jobName: String = "",
    val group: String = "",
    val description: String = "",
    val classPath: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val time: String = "",
    val interval: Int = 0,
    val repeatTimes: Int = 0,
    val timeUnit: ScheduleEnum? = null,
    val jobDataMap: JobDataMap = JobDataMap()
)
