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

    override fun handle(lines: Collection<String>): Map<ProcOSEntry.ValueKey, Double> {
        val result = HashMap<ValueKey, Double>()
        lines
                .map { it.split(" ") }
                .forEach {
                    when (it[0]) {
                    //cpu  7979968 8004 2001916 822016041 1053405 0 18328 0 0 0

                        CPU_TOKEN -> {
                            val cpuData = calculateStats(it)
                            result[IDLE] = cpuData[IDLE.ordinal]
                            result[NICE] = cpuData[CPU.NICE.ordinal]
                            result[SYSTEM] = cpuData[CPU.SYSTEM.ordinal]
                            result[USER] = cpuData[CPU.USER.ordinal]
                            result[STEAL] = cpuData[CPU.STEAL.ordinal]
                            result[IOWAIT] = cpuData[CPU.IOWAIT.ordinal]
                            result[IRQ] = cpuData[CPU.IRQ.ordinal]
                            result[SOFTIRQ] = cpuData[CPU.SOFTIRQ.ordinal]
                            result[GUEST] = cpuData[CPU.GUEST.ordinal]
                            result[GUEST_NICE] = cpuData[CPU.GUEST_NICE.ordinal]
                        }
                        PAGE -> {
                            result[Paging.PAGE_IN] = it[1].toDouble()
                            result[Paging.PAGE_OUT] = it[2].toDouble()
                        }
                        SWAP -> {
                            result[Swap.SWAPIN] = it[1].toDouble()
                            result[Swap.SWAPOUT] = it[2].toDouble()
                        }
                        CTXT -> result[Context.SWITCHES] = it[1].toDouble()
                        PROCESSES -> result[Processes.COUNT] = it[1].toDouble()
                    }
                }
        return result
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