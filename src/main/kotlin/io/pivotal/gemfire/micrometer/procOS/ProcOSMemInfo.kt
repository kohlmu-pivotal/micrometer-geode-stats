package io.pivotal.gemfire.micrometer.procOS

class ProcOSMemInfo(reader: ProcOSReader) : ProcOSEntry(reader) {
    companion object {
        enum class Key : ValueKey {
            PHYSICAL_MEM,
            FREE_MEM,
            SHARED_MEM,
            AVAILABLE_MEM,
            BUFFER_MEM,
            ALLOCATED_SWAP,
            UNALLOCATED_SWAP,
            CACHED_MEM,
            DIRTY_MEM
        }
    }

    override fun handle(lines: Collection<String>): Map<ValueKey, Double> {
        val result = HashMap<ValueKey, Double>()
        for (line in lines) {
            val split = line.split(" ")
            when(split[0])
            {
                "MemTotal:" -> result[Key.PHYSICAL_MEM] = split[split.size-2].toDouble()
                "MemFree:" -> result[Key.FREE_MEM] = split[split.size-2].toDouble()
                "SharedMem:" -> result[Key.SHARED_MEM] = split[split.size-2].toDouble()
                "MemAvailable:" -> result[Key.AVAILABLE_MEM] = split[split.size-2].toDouble()
                "Buffers:" -> result[Key.BUFFER_MEM] = split[split.size-2].toDouble()
                "SwapTotal:" -> result[Key.ALLOCATED_SWAP] = split[split.size-2].toDouble()
                "SwapFree:" -> result[Key.UNALLOCATED_SWAP] = split[split.size-2].toDouble()
                "Cached:" -> result[Key.CACHED_MEM] = split[split.size-2].toDouble()
                "Dirty:" -> result[Key.DIRTY_MEM] = split[split.size-2].toDouble()
                "Inact_dirty:" -> result[Key.DIRTY_MEM] = split[split.size-2].toDouble()
            }
        }
        return result
    }
}