package com.zipe.util

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import kotlin.properties.Delegates

@Component
class SpringUtil : ApplicationContextAware, EnvironmentAware {

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        sApplicationContext = applicationContext
    }

    override fun setEnvironment(environment: Environment) {
        sEnvironment = environment
    }

    companion object {
        private var sApplicationContext: ApplicationContext by Delegates.notNull()
        private var sEnvironment: Environment by Delegates.notNull()

        /**
         * 获取 ApplicationContext
         */
        fun getApplicationContext(): ApplicationContext {
            return sApplicationContext
        }

        /**
         * 根据 name 获取 Bean
         */
        fun getBean(name: String): Any {
            return sApplicationContext.getBean(name)
        }

        /**
         * 通过 class 获取 Bean
         */
        fun <T> getBean(clazz: Class<T>): T {
            return sApplicationContext.getBean(clazz)
        }

        /**
         * 通过 name 和 class 获取 Bean
         */
        fun <T> getBean(name: String, clazz: Class<T>): T {
            return sApplicationContext.getBean(name, clazz)
        }

    }

}
