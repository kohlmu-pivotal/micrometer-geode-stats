package io.pivotal.gemfire

import io.micrometer.core.instrument.binder.MeterBinder
import io.pivotal.gemfire.micrometer.binder.LoadAvgMetrics
import io.pivotal.gemfire.micrometer.binder.MemInfoMetrics
import io.pivotal.gemfire.micrometer.binder.StatMetrics
import io.pivotal.gemfire.micrometer.procOS.ProcOSLoadAvg
import io.pivotal.gemfire.micrometer.procOS.ProcOSMemInfo
import io.pivotal.gemfire.micrometer.procOS.ProcOSReaderFactory
import io.pivotal.gemfire.micrometer.procOS.ProcOSStat

class MicrometerLinuxStats {
    private val registry = MicrometerStats().meterRegistry

    fun registerMetrics(metricsBinders: MeterBinder) {
        metricsBinders.bindTo(registry)
    }
}

fun main(args: Array<String>) {
    val micrometerLinuxStats = MicrometerLinuxStats()
    val procOSReaderFactory = ProcOSReaderFactory()
    micrometerLinuxStats.registerMetrics(LoadAvgMetrics(procOSLoadAvg = ProcOSLoadAvg(procOSReaderFactory.getInstance(LoadAvgMetrics.LOADAVG))))
    micrometerLinuxStats.registerMetrics(MemInfoMetrics(procOSMemInfo = ProcOSMemInfo(procOSReaderFactory.getInstance(MemInfoMetrics.MEMINFO))))
    micrometerLinuxStats.registerMetrics(StatMetrics(procOSStat = ProcOSStat(procOSReaderFactory.getInstance(StatMetrics.STAT))))
    while (true) {
        Thread.sleep(1000)
    }
}