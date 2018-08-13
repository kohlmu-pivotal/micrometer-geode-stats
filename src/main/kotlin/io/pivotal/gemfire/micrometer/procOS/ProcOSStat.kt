package io.pivotal.gemfire.micrometer.procOS

import io.pivotal.gemfire.micrometer.procOS.ProcOSStat.Companion.CPU.*
import java.util.regex.Pattern

class ProcOSStat(reader: ProcOSReader) : ProcOSEntry(reader) {
    companion object {

        private const val CPU_TOKEN = "cpu"
        private const val PAGE = "page"
        private const val SWAP = "swap"
        private const val CONTEXT = "ctxt"
        private const val PROCESSES = "processes"

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
    private var previousCPUStats = Array(CPU.values().size) { 0L }
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
                            result[USER] = cpuData[CPU.USER.ordinal]
                            result[NICE] = cpuData[CPU.NICE.ordinal]
                            result[SYSTEM] = cpuData[CPU.SYSTEM.ordinal]
                            result[IDLE] = cpuData[CPU.IDLE.ordinal]
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
                        CONTEXT -> result[Context.SWITCHES] = computeDifference(it[1].toDouble(), Context.SWITCHES)
                        PROCESSES -> result[Processes.COUNT] = computeDifference(it[1].toDouble(), Processes.COUNT)
                    }
                }
        return result
    }

    private fun computeDifference(cpuData: Double, valueKey: ValueKey): Double {
        val difference = cpuData - previousDataset.getOrDefault(valueKey, 0.toDouble())
        previousDataset[valueKey] = cpuData
        return difference
    }

    private fun calculateStats(dataLine: List<String>): Array<Double> {
        val newCpuStatsArray = convertStringArrayToLongArray(previousCPUStats.size, dataLine)
        val deltaCPUStatsArray = calculateDeltaCPUStats(previousCPUStats, newCpuStatsArray)
        val totalTime = calculateTotalTimeSpent(deltaCPUStatsArray)

        val cpuStatsArray = Array(deltaCPUStatsArray.size) { 0.toDouble() }
        for (count in 0 until deltaCPUStatsArray.size) {
            cpuStatsArray[count] = deltaCPUStatsArray[count].toDouble().div(totalTime)
        }

        previousCPUStats = newCpuStatsArray
        return cpuStatsArray
    }

    private fun calculateDeltaCPUStats(previousCPUStats: Array<Long>, newCpuStatsArray: Array<Long>): Array<Long> {
        val returnArray = Array(previousCPUStats.size) { 0L }
        for (index in 0 until previousCPUStats.size) {
            returnArray[index] = newCpuStatsArray[index] - previousCPUStats[index]
        }
        return returnArray
    }

    private fun calculateTotalTimeSpent(cpuStatsArray: Array<Long>): Long = cpuStatsArray.reduce { sum, element -> sum + element }

    private fun convertStringArrayToLongArray(enumSize: Int, dataLine: List<String>): Array<Long> {
        val longArray = Array(enumSize) { 0L }
        //cpu  7979968 8004 2001916 822016041 1053405 0 18328 0 0 0
        for (count in 1 until enumSize) {
            longArray[count - 1] = dataLine[count].toLong()
        }
        return longArray
    }
}