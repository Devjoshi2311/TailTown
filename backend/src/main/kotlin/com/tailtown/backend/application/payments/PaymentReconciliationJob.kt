package com.tailtown.backend.application.payments

import org.quartz.DisallowConcurrentExecution
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

/**
 * Quartz instantiates this via reflection (no-arg constructor), then Spring Boot's Quartz
 * autoconfiguration autowires it — so dependencies must be field/setter injected, not constructor injected.
 */
@DisallowConcurrentExecution
class PaymentReconciliationJob : QuartzJobBean() {

    @Autowired
    lateinit var paymentService: PaymentService

    private val log = LoggerFactory.getLogger(PaymentReconciliationJob::class.java)

    override fun executeInternal(context: JobExecutionContext) {
        log.info("Running payment reconciliation job")
        paymentService.reconcilePending()
    }
}
