package io.pivotal.gemfire.micrometer.binder

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.binder.MeterBinder
import io.pivotal.gemfire.micrometer.procOS.ProcOSStat

class StatMetrics(private val tags: Iterable<Tag> = emptyList(), val procOSStat: ProcOSStat) : MeterBinder {
    companion object {
        const val STAT = "stat"
    }

    override fun bindTo(registry: MeterRegistry) {

        Gauge.builder("system.cpu", procOSStat) { it[ProcOSStat.Companion.CPU.SYSTEM] }
                .tags(addCustomTagToTags(tags, "system")).description("Percentage of CPU used")
                .baseUnit("percentage").register(registry)
        Gauge.builder("system.cpu", procOSStat) { it[ProcOSStat.Companion.CPU.NICE] }
                .tags(addCustomTagToTags(tags, "nice")).description("Percentage of CPU used")
                .baseUnit("percentage").register(registry)
        Gauge.builder("system.cpu", procOSStat) { it[ProcOSStat.Companion.CPU.IDLE] }
                .tags(addCustomTagToTags(tags, "idle")).description("Percentage of CPU used")
                .baseUnit("percentage").register(registry)
        Gauge.builder("system.cpu", procOSStat) { it[ProcOSStat.Companion.CPU.USER] }
                .tags(addCustomTagToTags(tags, "user")).description("Percentage of CPU used")
                .baseUnit("percentage").register(registry)
        Gauge.builder("system.cpu", procOSStat) { it[ProcOSStat.Companion.CPU.IOWAIT] }
                .tags(addCustomTagToTags(tags, "iowait")).description("Percentage of CPU used")
                .baseUnit("percentage").register(registry)
        Gauge.builder("system.cpu", procOSStat) { it[ProcOSStat.Companion.CPU.IRQ] }
                .tags(addCustomTagToTags(tags, "irq")).description("Percentage of CPU used")
                .baseUnit("percentage").register(registry)
        Gauge.builder("system.cpu", procOSStat) { it[ProcOSStat.Companion.CPU.SOFTIRQ] }
                .tags(addCustomTagToTags(tags, "softirq")).description("Percentage of CPU used")
                .baseUnit("percentage").register(registry)

        Gauge.builder("system.paging", procOSStat) { it[ProcOSStat.Companion.Paging.PAGE_IN] }
                .tags(addCustomTagToTags(tags, "in")).baseUnit("kiloByte").register(registry)
        Gauge.builder("system.paging", procOSStat) { it[ProcOSStat.Companion.Paging.PAGE_OUT] }
                .tags(addCustomTagToTags(tags, "out")).baseUnit("kiloByte").register(registry)

        Gauge.builder("system.swap", procOSStat) { it[ProcOSStat.Companion.Swap.SWAPIN] }
                .tags(addCustomTagToTags(tags, "in")).baseUnit("kiloByte").register(registry)
        Gauge.builder("system.swap", procOSStat) { it[ProcOSStat.Companion.Swap.SWAPOUT] }
                .tags(addCustomTagToTags(tags, "out")).baseUnit("kiloByte").register(registry)

        Gauge.builder("system.context.switches", procOSStat) { it[ProcOSStat.Companion.Context.SWITCHES] }
                .tags(tags).register(registry)
        Gauge.builder("system.processes.count", procOSStat) { it[ProcOSStat.Companion.Processes.COUNT] }
                .tags(tags).register(registry)
    }

    private fun addCustomTagToTags(tags: Iterable<Tag>, tagType: String): Iterable<Tag> {
        val newTags = ArrayList<Tag>()
        newTags.addAll(tags)
        newTags.add(Tag.of("type", tagType))
        return newTags
    }
}