package io.pivotal.gemfire.micrometer.procOS

import io.pivotal.gemfire.micrometer.procOS.ProcOSStat.Companion.CPU.*
import java.util.regex.Pattern

class ProcOSStat(reader: ProcOSReader) : ProcOSEntry(reader) {

    companion object {

        private val CPU_TOKEN = "cpu"
        private val PAGE = "page"
        private val SWAP = "swap"
        private val CTXT = "ctxt"
        private val PROCESSES = "processes"

        enum class CPU : ValueKey {
            USER,
            NICE,
            SYSTEM,
            IDLE,
            IOWAIT,
            IRQ,
            SOFTIRQ
        }

        enum class Paging : ValueKey {
            PAGE_IN,
            PAGE_OUT
        }

        enum class Swap : ValueKey {
            SWAPIN,
            SWAPOUT
        }

        enum class Context : ValueKey {
            SWITCHES
        }

        enum class Processes : ValueKey {
            COUNT
        }
    }

    private val previousDataset = HashMap<ValueKey, Double>()
    private val pattern = Pattern.compile("\\s+")

    override fun handle(lines: Collection<String>): Map<ProcOSEntry.ValueKey, Double> {
        val result = HashMap<ValueKey, Double>()
        lines
                .map { it.split(pattern) }
                .forEach {
                    when (it[0]) {
                    //cpu  7979968 8004 2001916 822016041 1053405 0 18328 0 0 0

                        CPU_TOKEN -> {
                            val cpuData = calculateStats(it)
                            result[IDLE] = cpuData[CPU.IDLE.ordinal]
                            result[NICE] = cpuData[CPU.NICE.ordinal]
                            result[SYSTEM] = cpuData[CPU.SYSTEM.ordinal]
                            result[USER] = cpuData[CPU.USER.ordinal]
                            result[IOWAIT] = cpuData[CPU.IOWAIT.ordinal]
                            result[IRQ] = cpuData[CPU.IRQ.ordinal]
                            result[SOFTIRQ] = cpuData[CPU.SOFTIRQ.ordinal]
                        }
                        PAGE -> {
                            result[Paging.PAGE_IN] = computeDifference(it[1].toDouble(), Paging.PAGE_IN)
                            result[Paging.PAGE_OUT] = computeDifference(it[2].toDouble(), Paging.PAGE_IN)
                        }
                        SWAP -> {
                            result[Swap.SWAPIN] = computeDifference(it[1].toDouble(), Swap.SWAPIN)
                            result[Swap.SWAPOUT] = computeDifference(it[2].toDouble(), Swap.SWAPOUT)
                        }
                        CTXT -> result[Context.SWITCHES] = computeDifference(it[1].toDouble(), Context.SWITCHES)
                        PROCESSES -> result[Processes.COUNT] = computeDifference(it[1].toDouble(), Processes.COUNT)
                    }
                }
        return result
    }

    private fun computeDifference(cpuData: Double, valueKey: ValueKey) : Double {
        val difference = cpuData - previousDataset.getOrDefault(valueKey, 0.toDouble())
        previousDataset[valueKey] = cpuData
        return difference
    }

    private fun calculateStats(dataLine: List<String>): Array<Double> {
        val enumSize = CPU.values().size
        val cpuStatsArray = Array(enumSize, { 0.toDouble() })

        var totalTime:Long = 0

        for (count in 1 until enumSize) {
            val data = dataLine[count].toLong()
            totalTime += data
            cpuStatsArray[count - 1] = data.toDouble()
        }

        for (count in 0 until enumSize) {
            cpuStatsArray[count] = cpuStatsArray[count].div(totalTime).times(100)
        }

        return cpuStatsArray
    }
}