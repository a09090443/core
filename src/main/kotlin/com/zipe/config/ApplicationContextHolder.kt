package com.zipe.config

import org.quartz.spi.TriggerFiredBundle
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.scheduling.quartz.SpringBeanJobFactory
import org.springframework.stereotype.Component


@Component
class ApplicationContextHolder : SpringBeanJobFactory(), ApplicationContextAware {
    @Transient
    private var beanFactory: AutowireCapableBeanFactory? = null

    @Throws(BeansException::class)
    override fun setApplicationContext(ctx: ApplicationContext) {
        beanFactory = ctx.getAutowireCapableBeanFactory()
        context = ctx
    }

    @Throws(Exception::class)
    override fun createJobInstance(bundle: TriggerFiredBundle): Any {
        val job = super.createJobInstance(bundle)
        beanFactory!!.autowireBean(job)
        return job
    }

    companion object {
        private var context: ApplicationContext? = null
        fun getContext(): ApplicationContext? {
            return context
        }
    }
}
