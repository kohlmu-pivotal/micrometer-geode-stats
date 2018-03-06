package io.pivotal.gemfire.micrometer.binder

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.binder.MeterBinder
import io.pivotal.gemfire.micrometer.procOS.ProcOSLoadAvg

class LoadAvgMetrics(val tags: Iterable<Tag> = emptyList(), val procOSLoadAvg: ProcOSLoadAvg) : MeterBinder {
    companion object {
        val LOADAVG = "loadavg"
    }

    override fun bindTo(registry: MeterRegistry) {
        Gauge.builder("system.load.average.1m", procOSLoadAvg, { it[ProcOSLoadAvg.Companion.Key.ONE_MIN] }).tags(tags).register(registry)
        Gauge.builder("system.load.average.5m", procOSLoadAvg, { it[ProcOSLoadAvg.Companion.Key.FIVE_MIN] }).tags(tags).register(registry)
        Gauge.builder("system.load.average.15m", procOSLoadAvg, { it[ProcOSLoadAvg.Companion.Key.FIFTEEN_MIN] }).tags(tags).register(registry)

    }
}