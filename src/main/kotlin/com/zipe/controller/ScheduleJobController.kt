package com.zipe.controller

import com.zipe.entity.ScheduleJob
import com.zipe.enum.ScheduleJobStatusEnum
import com.zipe.job.AbstractJob
import com.zipe.model.input.ScheduleJobInput
import com.zipe.model.output.ScheduleJobOutput
import org.quartz.SchedulerException
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.ParseException

@RestController
@RequestMapping("/job")
class ScheduleJobController : AbstractJob() {

    @PostMapping("/register")
    @Throws(Exception::class)
    fun register(@RequestBody input: ScheduleJobInput): ScheduleJobOutput {
        try {
            val scheduleJob = saveOrUpdateScheduleJobStatus(input, ScheduleJobStatusEnum.START)
            result = createJobProcess(scheduleJob)
        } catch (ex: SchedulerException) {
            logger.error("Error scheduling message", ex)
            return result
        } catch (e: ParseException) {
            throw e
        } catch (e: ClassNotFoundException) {
            throw e
        }
        return result
    }

    @DeleteMapping("/delete")
    @Throws(Exception::class)
    fun delete(@RequestBody input: ScheduleJobInput): ScheduleJobOutput {
        try {
            scheduleJobServiceImpl.delete(input.jobName)
        } catch (e: Exception) {
            logger.error("Error scheduling message", e)
            return result
        }
        result = deleteJobProcess(ScheduleJob(jobName = input.jobName, jobGroup = input.jobGroup))
        return result
    }

    @PostMapping("/stop")
    @Throws(Exception::class)
    fun stop(@RequestBody input: ScheduleJobInput): ScheduleJobOutput {
        try {
            val scheduleJob = saveOrUpdateScheduleJobStatus(input, ScheduleJobStatusEnum.SUSPEND)
            result = suspendJobProcess(scheduleJob)
        } catch (e: Exception) {
            logger.error("Error scheduling message", e)
            return result
        }
        return result
    }

    @PostMapping("/start")
    @Throws(Exception::class)
    fun start(@RequestBody input: ScheduleJobInput): ScheduleJobOutput {
        try {
            val scheduleJob = saveOrUpdateScheduleJobStatus(input, ScheduleJobStatusEnum.START)
            result = resumeJobProcess(scheduleJob)
        } catch (e: Exception) {
            logger.error("Error scheduling message", e)
            return result
        }
        return result
    }

    @PostMapping("/once")
    @Throws(Exception::class)
    fun once(@RequestBody input: ScheduleJobInput) {
        val scheduleJob = saveOrUpdateScheduleJobStatus(input, ScheduleJobStatusEnum.START)
//        buildOnceJobTrigger(input.jobName, scheduleJob)
    }

}
