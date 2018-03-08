package io.pivotal.gemfire

import io.micrometer.core.instrument.binder.MeterBinder
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

class MicrometerLinuxStats {
    private val registry = MicrometerStats().meterRegistry
    private val registeredMetricsBinders = ArrayList<MeterBinder>()

    fun registerMetrics(metricsBinders: MeterBinder) {
        metricsBinders.bindTo(registry)
        registeredMetricsBinders.add(metricsBinders)
    }
}

fun main(args: Array<String>) {
    val micrometerLinuxStats = MicrometerLinuxStats()
    val procOSReaderFactory = ProcOSReaderFactory()
    micrometerLinuxStats.registerMetrics(LoadAvgMetrics(procOSLoadAvg = ProcOSLoadAvg(procOSReaderFactory.getInstance(LoadAvgMetrics.LOADAVG))))
    micrometerLinuxStats.registerMetrics(MemInfoMetrics(procOSMemInfo = ProcOSMemInfo(procOSReaderFactory.getInstance(MemInfoMetrics.MEMINFO))))
    micrometerLinuxStats.registerMetrics(StatMetrics(procOSStat = ProcOSStat(procOSReaderFactory.getInstance(StatMetrics.STAT))))
    micrometerLinuxStats.registerMetrics(JvmGcMetrics())
    micrometerLinuxStats.registerMetrics(JvmMemoryMetrics())
    micrometerLinuxStats.registerMetrics(JvmThreadMetrics())
    micrometerLinuxStats.registerMetrics(FileDescriptorMetrics())

    Flux.interval(Duration.ofSeconds(1L)).doOnEach {  }.blockLast()
}