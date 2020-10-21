package com.zipe.service

import com.zipe.entity.ScheduleJob
import com.zipe.enum.ScheduleJobStatusEnum
import com.zipe.model.input.ScheduleJobInput

interface IScheduleJobService {
    @Throws(Exception::class)
    fun findAllJobs(): List<ScheduleJob?>?

    @Throws(Exception::class)
    fun findById(id: Int): ScheduleJob?

    @Throws(Exception::class)
    fun findByJobName(jobName: String?): ScheduleJob?

    @Throws(Exception::class)
    fun save(input: ScheduleJobInput, status: ScheduleJobStatusEnum): ScheduleJob

    @Throws(Exception::class)
    fun update(input: ScheduleJobInput, status: ScheduleJobStatusEnum): ScheduleJob

    @Throws(Exception::class)
    fun delete(jobName: String)
}
