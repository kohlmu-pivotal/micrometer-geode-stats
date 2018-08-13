package io.pivotal.gemfire

import io.micrometer.core.instrument.binder.MeterBinder
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics
import io.pivotal.gemfire.micrometer.binder.LoadAvgMetrics
import io.pivotal.gemfire.micrometer.binder.MemInfoMetrics
import io.pivotal.gemfire.micrometer.binder.StatMetrics
import io.pivotal.gemfire.micrometer.procOS.ProcOSLoadAvg
import io.pivotal.gemfire.micrometer.procOS.ProcOSMemInfo
import io.pivotal.gemfire.micrometer.procOS.ProcOSReaderFactory
import io.pivotal.gemfire.micrometer.procOS.ProcOSStat
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MicrometerLinuxStats {
    private val registry = MicrometerStats().meterRegistry
    private val registeredMetricsBinders = ArrayList<MeterBinder>()

    fun registerMetrics(metricsBinders: MeterBinder) {
        metricsBinders.bindTo(registry)
        registeredMetricsBinders.add(metricsBinders)
    }

    fun createExecutorService(): ExecutorService {
//        val threadPool = Executors.newCachedThreadPool()
        val threadPool = Executors.newWorkStealingPool()
//        val threadPool = Executors.newFixedThreadPool(20)
        Flux.interval(Duration.ofMillis(50L)).doOnEach {
            threadPool.submit {
                val nextInt = Random().nextInt(10000)
                when {
                    nextInt % 7 == 0 -> Thread.sleep(200L)
                    nextInt % 3 == 0 -> Thread.sleep(300L)
                    nextInt % 4 == 0 -> Thread.sleep(500L)
                    nextInt % 5 == 0 -> Thread.sleep(1000L)
                }
            }

        }.subscribe()
        return threadPool
    }

    fun registerExecutorMetrics(executorService: ExecutorService) {
        ExecutorServiceMetrics.monitor(registry, executorService, "testService")
//        registeredMetricsBinders.add(executorServiceMonitor)

    }

}

fun main(args: Array<String>) {
    val micrometerLinuxStats = MicrometerLinuxStats()
    val procOSReaderFactory = ProcOSReaderFactory()
    micrometerLinuxStats.registerMetrics(LoadAvgMetrics(procOSLoadAvg = ProcOSLoadAvg(procOSReaderFactory.getInstance(LoadAvgMetrics.LOAD_AVG))))
    micrometerLinuxStats.registerMetrics(MemInfoMetrics(procOSMemInfo = ProcOSMemInfo(procOSReaderFactory.getInstance(MemInfoMetrics.MEM_INFO))))
    micrometerLinuxStats.registerMetrics(StatMetrics(procOSStat = ProcOSStat(procOSReaderFactory.getInstance(StatMetrics.STAT))))

    micrometerLinuxStats.registerMetrics(JvmGcMetrics())
    micrometerLinuxStats.registerMetrics(JvmMemoryMetrics())
    micrometerLinuxStats.registerMetrics(JvmThreadMetrics())
    micrometerLinuxStats.registerMetrics(FileDescriptorMetrics())
//
//    val executorService = micrometerLinuxStats.createExecutorService()
//    micrometerLinuxStats.registerExecutorMetrics(executorService)


    Flux.interval(Duration.ofSeconds(1L)).doOnEach { }.blockLast()
}