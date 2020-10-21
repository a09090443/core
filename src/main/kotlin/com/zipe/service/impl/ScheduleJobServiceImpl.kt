package com.zipe.service.impl

import com.zipe.entity.ScheduleJob
import com.zipe.enum.ScheduleJobStatusEnum
import com.zipe.model.input.ScheduleJobInput
import com.zipe.repository.IScheduleJobRepository
import com.zipe.service.IScheduleJobService
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Transactional
@Service("scheduleJobService")
class ScheduleJobServiceImpl : IScheduleJobService {

    @Autowired
    private lateinit var scheduleJobRepository: IScheduleJobRepository

    @Throws(Exception::class)
    override fun findAllJobs(): List<ScheduleJob?>? {
        return scheduleJobRepository.findAll()
    }

    @Throws(Exception::class)
    override fun findById(id: Int): ScheduleJob? {
        return scheduleJobRepository.findById(id)
    }

    @Throws(Exception::class)
    override fun findByJobName(jobName: String?): ScheduleJob? {
        return scheduleJobRepository.findByJobName(jobName)
    }

    @Transactional(rollbackFor=[Exception::class])
    override fun save(input: ScheduleJobInput, status: ScheduleJobStatusEnum): ScheduleJob {

        val scheduleJob = ScheduleJob().apply {
            BeanUtils.copyProperties(input, this)

            this.startTime = LocalDateTime.parse(
                "${input.startDate} ${input.time}",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.TAIWAN)
            )
            this.endTime = LocalDateTime.parse(
                "${input.endDate} ${input.time}",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.TAIWAN)
            )
            this.status = status
        }

        scheduleJobRepository.save(scheduleJob).let { return scheduleJob }
    }

    override fun update(input: ScheduleJobInput, status: ScheduleJobStatusEnum): ScheduleJob {
        TODO("Not yet implemented")
    }

    @Throws(Exception::class)
    override fun delete(jobName: String) {
        scheduleJobRepository.deleteByJobName(jobName)
    }
}
