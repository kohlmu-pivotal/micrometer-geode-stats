package io.pivotal.gemfire.micrometer.binder

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.binder.MeterBinder
import io.pivotal.gemfire.micrometer.procOS.ProcOSMemInfo

class MemInfoMetrics(private val tags: Iterable<Tag> = emptyList(), val procOSMemInfo: ProcOSMemInfo) : MeterBinder {
    companion object {
        const val MEM_INFO = "meminfo"
    }

    override fun bindTo(registry: MeterRegistry) {
        Gauge.builder("system.mem.physical", procOSMemInfo) { it[ProcOSMemInfo.Companion.Key.PHYSICAL_MEM] }.tags(tags).baseUnit("kiloByte").register(registry)
        Gauge.builder("system.mem.free", procOSMemInfo) { it[ProcOSMemInfo.Companion.Key.FREE_MEM] }.tags(tags).baseUnit("kiloByte").register(registry)
        Gauge.builder("system.mem.available", procOSMemInfo) { it[ProcOSMemInfo.Companion.Key.AVAILABLE_MEM] }.tags(tags).baseUnit("kiloByte").register(registry)
        Gauge.builder("system.mem.shared", procOSMemInfo) { it[ProcOSMemInfo.Companion.Key.SHARED_MEM] }.tags(tags).baseUnit("kiloByte").register(registry)
        Gauge.builder("system.mem.buffer", procOSMemInfo) { it[ProcOSMemInfo.Companion.Key.BUFFER_MEM] }.tags(tags).baseUnit("kiloByte").register(registry)
        Gauge.builder("system.mem.swap.allocated", procOSMemInfo) { it[ProcOSMemInfo.Companion.Key.ALLOCATED_SWAP] }.tags(tags).baseUnit("kiloByte").register(registry)
        Gauge.builder("system.mem.swap.free", procOSMemInfo) { it[ProcOSMemInfo.Companion.Key.UNALLOCATED_SWAP] }.tags(tags).baseUnit("kiloByte").register(registry)
        Gauge.builder("system.mem.cached", procOSMemInfo) { it[ProcOSMemInfo.Companion.Key.CACHED_MEM] }.tags(tags).baseUnit("kiloByte").register(registry)
        Gauge.builder("system.mem.dirty", procOSMemInfo) { it[ProcOSMemInfo.Companion.Key.DIRTY_MEM] }.tags(tags).baseUnit("kiloByte").register(registry)

    }
}