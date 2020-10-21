package com.zipe.job

import com.zipe.entity.ScheduleJob
import com.zipe.enum.ScheduleJobStatusEnum
import com.zipe.model.input.ScheduleJobInput
import com.zipe.model.output.ScheduleJobOutput
import com.zipe.service.IScheduleJobService
import com.zipe.util.log.logger
import org.quartz.Job
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.SchedulerConfigException
import org.quartz.SchedulerException
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import java.text.ParseException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

abstract class AbstractJob {

    val logger = logger()

    @Autowired
    protected lateinit var scheduler: Scheduler

    @Autowired
    protected lateinit var scheduleJobService: IScheduleJobService

    protected lateinit var result: ScheduleJobOutput

    @Throws(ClassNotFoundException::class, ParseException::class, SchedulerException::class)
    fun executeJobProcess(scheduleJob: ScheduleJob) {
        try {
            val jobDetail = buildJobDetail(scheduleJob)
            val trigger = buildJobTrigger(jobDetail, scheduleJob.startTime, scheduleJob.endTime, scheduleJob)
            scheduler.scheduleJob(jobDetail, trigger)
        } catch (e: SchedulerException) {
            throw e
        }
    }

    @Throws(SchedulerException::class, ParseException::class, ClassNotFoundException::class)
    fun deleteJobProcess(scheduleJob: ScheduleJob): ScheduleJobOutput {
        return scheduleJobStatusProcess(scheduleJob, ScheduleJobStatusEnum.DELETE)
    }

    @Throws(SchedulerException::class, ParseException::class, ClassNotFoundException::class)
    fun suspendJobProcess(scheduleJob: ScheduleJob): ScheduleJobOutput {
        return scheduleJobStatusProcess(scheduleJob, ScheduleJobStatusEnum.SUSPEND)
    }

    @Throws(SchedulerException::class, ParseException::class, ClassNotFoundException::class)
    fun resumeJobProcess(scheduleJob: ScheduleJob): ScheduleJobOutput {
        return scheduleJobStatusProcess(scheduleJob, ScheduleJobStatusEnum.RESUME)
    }

    @Throws(SchedulerException::class, ParseException::class, ClassNotFoundException::class)
    fun createJobProcess(scheduleJob: ScheduleJob): ScheduleJobOutput {
        return scheduleJobStatusProcess(scheduleJob, ScheduleJobStatusEnum.CREATE)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassNotFoundException::class)
    fun buildJobDetail(scheduleJob: ScheduleJob): JobDetail {
        val clazz: Class<out Job> = Class.forName(scheduleJob.classPath) as Class<out Job>

        return JobBuilder.newJob(clazz)
            .withIdentity(scheduleJob.jobName, scheduleJob.group)
            .withDescription(scheduleJob.description)
            .usingJobData(scheduleJob.jobDataMap)
            .storeDurably()
            .build()
    }

    private fun buildJobTrigger(
        jobDetail: JobDetail,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        scheduleJob: ScheduleJob
    ): Trigger {
        return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity(jobDetail.key.name, scheduleJob.jobName)
            .withDescription(scheduleJob.description)
            .startAt(Date.from(startTime?.atZone(ZoneId.systemDefault())?.toInstant()))
            .endAt(Date.from(endTime?.atZone(ZoneId.systemDefault())?.toInstant()))
            .startNow()
            .withSchedule(
                scheduleJob.timeUnit?.setCycle(scheduleJob.interval, scheduleJob.repeatTimes)
            )
            .build()
    }

    private fun buildOnceJobTrigger(
        jobDetail: JobDetail,
        scheduleJob: ScheduleJob
    ): Trigger {
        return TriggerBuilder.newTrigger()
            .withIdentity(jobDetail.key.name, scheduleJob.jobName)
            .withDescription(scheduleJob.description).startNow()
            .build()
    }

    @Throws(SchedulerException::class, ParseException::class, ClassNotFoundException::class)
    private fun scheduleJobStatusProcess(
        scheduleJob: ScheduleJob,
        scheduleJobStatusEnum: ScheduleJobStatusEnum
    ): ScheduleJobOutput {
        val jobKey = JobKey.jobKey(scheduleJob.jobName, scheduleJob.group)
        val output = ScheduleJobOutput().apply { BeanUtils.copyProperties(scheduleJob, this) }
        try {
            if (!scheduler.checkExists(jobKey) && scheduleJobStatusEnum.name != ScheduleJobStatusEnum.CREATE.name) {
                throw SchedulerConfigException("The job is not exist.")
            }
        } catch (e: SchedulerException) {
            output.errorMessage = e.message
        }
        try {
            when (scheduleJobStatusEnum) {
                ScheduleJobStatusEnum.CREATE -> {
                    scheduler.deleteJob(jobKey)
                    executeJobProcess(scheduleJob)
                }
                ScheduleJobStatusEnum.SUSPEND -> scheduler.pauseJob(jobKey)
                ScheduleJobStatusEnum.RESUME -> scheduler.resumeJob(jobKey)
                ScheduleJobStatusEnum.DELETE -> scheduler.deleteJob(jobKey)
            }
        } catch (e: SchedulerException) {
            logger.error("Schedule action error {}", scheduleJobStatusEnum.name)
            output.errorMessage = "${scheduleJobStatusEnum.name} Schedule job : ${scheduleJob.jobName} error."
        }
        return output
    }

    @Throws(Exception::class)
    protected fun saveOrUpdateScheduleJobStatus(input: ScheduleJobInput, status: ScheduleJobStatusEnum): ScheduleJob {
        val scheduleJob: ScheduleJob
        try {
            scheduleJob = scheduleJobService.findByJobName(input.jobName) ?: ScheduleJob()
            BeanUtils.copyProperties(input, scheduleJob)
            scheduleJobService.save(input, status)
        } catch (e: Exception) {
            logger.error(e.message)
            throw e
        }
        return scheduleJob
    }
}
