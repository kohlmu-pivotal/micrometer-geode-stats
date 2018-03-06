package io.pivotal.gemfire.micrometer.binder

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.binder.MeterBinder
import io.pivotal.gemfire.micrometer.procOS.ProcOSStat

class StatMetrics(val tags: Iterable<Tag> = emptyList(), val procOSStat: ProcOSStat) : MeterBinder {
    companion object {
        val STAT = "stat"
    }

    override fun bindTo(registry: MeterRegistry) {
        Gauge.builder("system.cpu.system", procOSStat, { it[ProcOSStat.Companion.CPU.SYSTEM] }).tags(tags).register(registry)
        Gauge.builder("system.cpu.nice", procOSStat, { it[ProcOSStat.Companion.CPU.NICE] }).tags(tags).register(registry)
        Gauge.builder("system.cpu.idle", procOSStat, { it[ProcOSStat.Companion.CPU.IDLE] }).tags(tags).register(registry)
        Gauge.builder("system.cpu.user", procOSStat, { it[ProcOSStat.Companion.CPU.USER] }).tags(tags).register(registry)
        Gauge.builder("system.cpu.steal", procOSStat, { it[ProcOSStat.Companion.CPU.STEAL] }).tags(tags).register(registry)
        Gauge.builder("system.cpu.iowait", procOSStat, { it[ProcOSStat.Companion.CPU.IOWAIT] }).tags(tags).register(registry)
        Gauge.builder("system.cpu.irq", procOSStat, { it[ProcOSStat.Companion.CPU.IRQ] }).tags(tags).register(registry)
        Gauge.builder("system.cpu.softirq", procOSStat, { it[ProcOSStat.Companion.CPU.SOFTIRQ] }).tags(tags).register(registry)
        Gauge.builder("system.cpu.guest", procOSStat, { it[ProcOSStat.Companion.CPU.GUEST] }).tags(tags).register(registry)
        Gauge.builder("system.cpu.guest.nice", procOSStat, { it[ProcOSStat.Companion.CPU.GUEST_NICE] }).tags(tags).register(registry)



        Gauge.builder("system.paging.in", procOSStat, { it[ProcOSStat.Companion.Paging.PAGE_IN] }).tags(tags).baseUnit("kiloByte").register(registry)
        Gauge.builder("system.paging.out", procOSStat, { it[ProcOSStat.Companion.Paging.PAGE_OUT] }).tags(tags).baseUnit("kiloByte").register(registry)

        Gauge.builder("system.swap.in", procOSStat, { it[ProcOSStat.Companion.Swap.SWAPIN] }).tags(tags).baseUnit("kiloByte").register(registry)
        Gauge.builder("system.swap.out", procOSStat, { it[ProcOSStat.Companion.Swap.SWAPOUT] }).tags(tags).baseUnit("kiloByte").register(registry)


        Gauge.builder("system.context.switches", procOSStat, { it[ProcOSStat.Companion.Context.SWITCHES] }).tags(tags).register(registry)
        Gauge.builder("system.processes.count", procOSStat, { it[ProcOSStat.Companion.Processes.COUNT] }).tags(tags).register(registry)

    }
}