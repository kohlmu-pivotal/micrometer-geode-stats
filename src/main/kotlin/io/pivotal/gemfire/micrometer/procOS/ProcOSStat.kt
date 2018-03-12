package io.pivotal.gemfire.micrometer.procOS

import io.pivotal.gemfire.micrometer.procOS.ProcOSStat.Companion.CPU.*

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
            SOFTIRQ,
            STEAL,
            /** stands for aggregation of all columns not present in the enum list  */
            GUEST,
            GUEST_NICE
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

    override fun handle(lines: Collection<String>): Map<ProcOSEntry.ValueKey, Double> {
        val result = HashMap<ValueKey, Double>()
        lines
                .map { it.split(" ") }
                .forEach {
                    when (it[0]) {
                    //cpu  7979968 8004 2001916 822016041 1053405 0 18328 0 0 0

                        CPU_TOKEN -> {
                            val cpuData = calculateStats(it)
                            result[IDLE] = computeDifference(cpuData[CPU.IDLE.ordinal],IDLE)
                            result[NICE] = computeDifference(cpuData[CPU.NICE.ordinal], NICE)
                            result[SYSTEM] = computeDifference(cpuData[CPU.SYSTEM.ordinal], SYSTEM)
                            result[USER] = computeDifference(cpuData[CPU.USER.ordinal], USER)
                            result[STEAL] = computeDifference(cpuData[CPU.STEAL.ordinal], STEAL)
                            result[IOWAIT] = computeDifference(cpuData[CPU.IOWAIT.ordinal], IOWAIT)
                            result[IRQ] = computeDifference(cpuData[CPU.IRQ.ordinal], IRQ)
                            result[SOFTIRQ] = computeDifference(cpuData[CPU.SOFTIRQ.ordinal], SOFTIRQ)
                            result[GUEST] = computeDifference(cpuData[CPU.GUEST.ordinal], GUEST)
                            result[GUEST_NICE] = computeDifference(cpuData[CPU.GUEST_NICE.ordinal], GUEST_NICE)
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
        val lengthWithOutLabel = dataLine.size - 2
        val cpuStatsArray = Array(lengthWithOutLabel, { 0.toDouble() })

        for (count in 2 until dataLine.size) {
            cpuStatsArray[count - 2] = dataLine[count].toDouble()
        }

        return cpuStatsArray
    }
}