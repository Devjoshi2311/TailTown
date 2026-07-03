package com.tailtown.backend.platform.config

import com.tailtown.backend.application.payments.PaymentReconciliationJob
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.SimpleScheduleBuilder
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QuartzConfig {

    @Bean
    fun paymentReconciliationJobDetail(): JobDetail =
        JobBuilder.newJob(PaymentReconciliationJob::class.java)
            .withIdentity("paymentReconciliationJob")
            .storeDurably()
            .build()

    @Bean
    fun paymentReconciliationTrigger(paymentReconciliationJobDetail: JobDetail): Trigger =
        TriggerBuilder.newTrigger()
            .forJob(paymentReconciliationJobDetail)
            .withIdentity("paymentReconciliationTrigger")
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInMinutes(5)
                    .repeatForever()
            )
            .build()
}
