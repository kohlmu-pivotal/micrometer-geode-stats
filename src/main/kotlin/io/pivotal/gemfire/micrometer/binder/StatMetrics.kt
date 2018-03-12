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

        Gauge.builder("system.cpu", procOSStat, { it[ProcOSStat.Companion.CPU.SYSTEM] }).tags(getCPUTags(tags, "system")).register(registry)
        Gauge.builder("system.cpu", procOSStat, { it[ProcOSStat.Companion.CPU.NICE] }).tags(getCPUTags(tags, "nice")).register(registry)
        Gauge.builder("system.cpu", procOSStat, { it[ProcOSStat.Companion.CPU.IDLE] }).tags(getCPUTags(tags, "idle")).register(registry)
        Gauge.builder("system.cpu", procOSStat, { it[ProcOSStat.Companion.CPU.USER] }).tags(getCPUTags(tags, "user")).register(registry)
        Gauge.builder("system.cpu.steal", procOSStat, { it[ProcOSStat.Companion.CPU.STEAL] }).tags(tags).register(registry)
        Gauge.builder("system.cpu.iowait", procOSStat, { it[ProcOSStat.Companion.CPU.IOWAIT] }).tags(tags).register(registry)
        Gauge.builder("system.cpu.irq", procOSStat, { it[ProcOSStat.Companion.CPU.IRQ] }).tags(tags).register(registry)
        Gauge.builder("system.cpu.softirq", procOSStat, { it[ProcOSStat.Companion.CPU.SOFTIRQ] }).tags(tags).register(registry)
        Gauge.builder("system.cpu.guest", procOSStat, { it[ProcOSStat.Companion.CPU.GUEST] }).tags(tags).register(registry)
        Gauge.builder("system.cpu.guest.nice", procOSStat, { it[ProcOSStat.Companion.CPU.GUEST_NICE] }).tags(tags).register(registry)



        Gauge.builder("system.paging", procOSStat, { it[ProcOSStat.Companion.Paging.PAGE_IN] }).tags(getPagingTags(tags, "in")).baseUnit("kiloByte").register(registry)
        Gauge.builder("system.paging", procOSStat, { it[ProcOSStat.Companion.Paging.PAGE_OUT] }).tags(getPagingTags(tags, "out")).baseUnit("kiloByte").register(registry)

        Gauge.builder("system.swap", procOSStat, { it[ProcOSStat.Companion.Swap.SWAPIN] }).tags(getSwapTags(tags, "in")).baseUnit("kiloByte").register(registry)
        Gauge.builder("system.swap", procOSStat, { it[ProcOSStat.Companion.Swap.SWAPOUT] }).tags(getSwapTags(tags, "out")).baseUnit("kiloByte").register(registry)


        Gauge.builder("system.context.switches", procOSStat, { it[ProcOSStat.Companion.Context.SWITCHES] }).tags(tags).register(registry)
        Gauge.builder("system.processes.count", procOSStat, { it[ProcOSStat.Companion.Processes.COUNT] }).tags(tags).register(registry)

    }

    private fun getCPUTags(tags: Iterable<Tag>, cpuType: String): Iterable<Tag> {
        val cpuTags = ArrayList<Tag>()
        cpuTags.addAll(tags)
        cpuTags.add(Tag.of("type", cpuType))
        return cpuTags
    }

    private fun getSwapTags(tags: Iterable<Tag>, swapType: String): Iterable<Tag> {
        val cpuTags = ArrayList<Tag>()
        cpuTags.addAll(tags)
        cpuTags.add(Tag.of("type", swapType))
        return cpuTags
    }

    private fun getPagingTags(tags: Iterable<Tag>, pagingType: String): Iterable<Tag> {
        val cpuTags = ArrayList<Tag>()
        cpuTags.addAll(tags)
        cpuTags.add(Tag.of("type", pagingType))
        return cpuTags
    }
}