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
import javax.persistence.Table

@Entity
@Table(name = "schedule_job")
data class ScheduleJob(

    @Column(name = "id")
    val id: Int = 0,

    @Id
    @Column(name = "job_name", unique = true)
    var jobName: String = "",

    @Column(name = "job_group")
    var group: String = "",

    @Column(name = "job_description")
    var description: String = "",

    @Column(name = "job_class")
    var classPath: String = "",

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: ScheduleJobStatusEnum? = null,

    @Column(name = "time_unit")
    @Enumerated(EnumType.STRING)
    var timeUnit: ScheduleEnum? = null,

    @Column(name = "repeat_interval")
    var interval: Int = 0,

    @Column(name = "repeat_times")
    var repeatTimes: Int = 0,

    @Column(name = "start_time", columnDefinition = "TIMESTAMP")
    var startTime: LocalDateTime? = null,

    @Column(name = "end_time", columnDefinition = "TIMESTAMP")
    var endTime: LocalDateTime? = null,

    @Transient
    var jobDataMap: JobDataMap = JobDataMap()

) : Serializable
