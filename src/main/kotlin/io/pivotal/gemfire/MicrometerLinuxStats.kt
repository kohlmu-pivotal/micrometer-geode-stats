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

    // shared fields
    internal val allocatedSwapINT = 0
    internal val bufferMemoryINT = 1
    internal val sharedMemoryINT = 2
    internal val cpuActiveINT = 3
    internal val cpuIdleINT = 4
    internal val cpuNiceINT = 5
    internal val cpuSystemINT = 6
    internal val cpuUserINT = 7
    internal val iowaitINT = 8
    internal val irqINT = 9
    internal val softirqINT = 10
    internal val cpusINT = 11
    internal val freeMemoryINT = 12
    internal val physicalMemoryINT = 13
    internal val processesINT = 14
    internal val unallocatedSwapINT = 15
    internal val cachedMemoryINT = 16
    internal val dirtyMemoryINT = 17
    internal val cpuNonUserINT = 18
    internal val cpuStealINT = 19

    internal val loopbackPacketsLONG = 0
    internal val loopbackBytesLONG = 1
    internal val recvPacketsLONG = 2
    internal val recvBytesLONG = 3
    internal val recvErrorsLONG = 4
    internal val recvDropsLONG = 5
    internal val xmitPacketsLONG = 6
    internal val xmitBytesLONG = 7
    internal val xmitErrorsLONG = 8
    internal val xmitDropsLONG = 9
    internal val xmitCollisionsLONG = 10
    internal val contextSwitchesLONG = 11
    internal val processCreatesLONG = 12
    internal val pagesPagedInLONG = 13
    internal val pagesPagedOutLONG = 14
    internal val pagesSwappedInLONG = 15
    internal val pagesSwappedOutLONG = 16
    internal val readsCompletedLONG = 17
    internal val readsMergedLONG = 18
    internal val bytesReadLONG = 19
    internal val timeReadingLONG = 20
    internal val writesCompletedLONG = 21
    internal val writesMergedLONG = 22
    internal val bytesWrittenLONG = 23
    internal val timeWritingLONG = 24
    internal val iosInProgressLONG = 25
    internal val timeIosInProgressLONG = 26
    internal val ioTimeLONG = 27

    internal val loadAverage1DOUBLE = 0
    internal val loadAverage15DOUBLE = 1
    internal val loadAverage5DOUBLE = 2

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