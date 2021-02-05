package com.zipe.enum

import org.quartz.CalendarIntervalScheduleBuilder
import org.quartz.DailyTimeIntervalScheduleBuilder
import org.quartz.DateBuilder
import org.quartz.ScheduleBuilder
import org.quartz.SimpleScheduleBuilder

enum class ScheduleEnum {
    NOW {
        override fun setCycle(interval: Int, repeatCount: Int): SimpleScheduleBuilder {
            return SimpleScheduleBuilder.simpleSchedule()
        }
    },
    SECOND {
        override fun setCycle(interval: Int, repeatCount: Int): SimpleScheduleBuilder {
            return SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval).withRepeatCount(repeatCount)
        }
    },
    MINUTE {
        override fun setCycle(interval: Int, repeatCount: Int): SimpleScheduleBuilder {
            return SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(interval).withRepeatCount(repeatCount)
        }
    },
    HOUR {
        override fun setCycle(interval: Int, repeatCount: Int): SimpleScheduleBuilder {
            return SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(interval).withRepeatCount(repeatCount)
        }
    },
    DAY {
        override fun setCycle(interval: Int, repeatCount: Int): DailyTimeIntervalScheduleBuilder {
            return DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
                .withInterval(interval, DateBuilder.IntervalUnit.DAY).withRepeatCount(repeatCount)
        }
    },
    WEEK {
        override fun setCycle(interval: Int, repeatCount: Int): CalendarIntervalScheduleBuilder {
            return CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withIntervalInWeeks(interval)
        }
    },
    MONTH {
        override fun setCycle(interval: Int, repeatCount: Int): CalendarIntervalScheduleBuilder {
            return CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withIntervalInMonths(interval)
        }
    },
    YEAR {
        override fun setCycle(interval: Int, repeatCount: Int): CalendarIntervalScheduleBuilder {
            return CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withIntervalInYears(interval)
        }
    };

    abstract fun setCycle(interval: Int, repeatCount: Int): ScheduleBuilder<*>?

}
