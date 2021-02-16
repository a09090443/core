package com.zipe.job

import com.zipe.entity.ScheduleJobLog
import com.zipe.enum.ScheduleJobStatusEnum
import com.zipe.repository.IScheduleJobLogRepository
import org.quartz.JobExecutionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

abstract class QuartzJobFactory : QuartzJobBean() {
    @Autowired
    lateinit var scheduleJobLogRepository: IScheduleJobLogRepository

    @Transactional
    override fun executeInternal(jobExecutionContext: JobExecutionContext) {

        val scheduleJobLog = ScheduleJobLog().apply {
            this.jobName = jobExecutionContext.jobDetail.key.name
            this.jobDescription = jobExecutionContext.jobDetail.description
            this.startTime = LocalDateTime.now()
        }

        try {
            scheduleJobLog.status = ScheduleJobStatusEnum.RUN
            scheduleJobLogRepository.save(scheduleJobLog)
            executeJob(jobExecutionContext)
            scheduleJobLog.status = ScheduleJobStatusEnum.COMPLETE
        } catch (e: Exception) {
            scheduleJobLog.message = e.message ?: ""
            scheduleJobLog.status = ScheduleJobStatusEnum.ERROR
        } finally {
            scheduleJobLog.endTime = LocalDateTime.now()
            scheduleJobLogRepository.saveAndFlush(scheduleJobLog)
        }
    }

    abstract fun executeJob(jobExecutionContext: JobExecutionContext)
}
