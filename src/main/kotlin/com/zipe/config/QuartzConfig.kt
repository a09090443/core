package com.zipe.config

import com.zipe.model.SystemConfigProperties
import org.quartz.spi.JobFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.PropertiesFactoryBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import java.io.IOException
import java.util.Properties
import javax.sql.DataSource

@Configuration
class QuartzConfig {

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Autowired
    lateinit var transactionManager: PlatformTransactionManager

    @Autowired
    lateinit var systemConfigProperties: SystemConfigProperties

    /**
     * Create the job factory bean
     * @return Job factory bean
     */
    @Bean
    fun jobFactory(): JobFactory {
        val jobFactory = ApplicationContextHolder()
        jobFactory.setApplicationContext(applicationContext)
        return jobFactory
    }

    @Bean
    @Throws(IOException::class)
    fun scheduler(): SchedulerFactoryBean {
        val scheduler = SchedulerFactoryBean()
        if (systemConfigProperties.jobUsingDatabase) {
            scheduler.setDataSource(dataSource)
        }
        scheduler.setTransactionManager(transactionManager)
        scheduler.setOverwriteExistingJobs(true)
        scheduler.setQuartzProperties(quartzProperties())
        scheduler.setJobFactory(jobFactory())
        return scheduler
    }

    @Bean
    @Throws(IOException::class)
    fun quartzProperties(): Properties {
        val propertiesFactoryBean = PropertiesFactoryBean()
        propertiesFactoryBean.setLocation(ClassPathResource("/quartz.properties"))
        propertiesFactoryBean.afterPropertiesSet()
        return propertiesFactoryBean.getObject() ?: throw IOException("Quartz properties file Error")
    }

}
