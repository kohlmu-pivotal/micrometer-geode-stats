package io.pivotal.gemfire.micrometer.procOS

import java.util.regex.Pattern

class ProcOSLoadAvg(reader: ProcOSReader) : ProcOSEntry(reader) {
    companion object {
        enum class Key : ValueKey {
            //1 minute load avg
            ONE_MIN,
            //5 minute load avg
            FIVE_MIN,
            //15 minute load avg
            FIFTEEN_MIN
        }
    }

    private val pattern = Pattern.compile("\\s+")

    override fun handle(lines: Collection<String>): Map<ValueKey, Double> {
        val result = HashMap<ValueKey, Double>()
        if (lines.size != 1) {
            result[Key.ONE_MIN] = (-1).toDouble()
            result[Key.FIVE_MIN] = (-1).toDouble()
            result[Key.FIFTEEN_MIN] = (-1).toDouble()
        } else {
            for (line in lines) {
                // Example of /proc/loadavg
                // 0.00 0.00 0.07 1/218 7907
                log.warn(line)
                val loadAvgs = line.split(pattern)
                result[Key.ONE_MIN] = loadAvgs[0].toDouble()
                result[Key.FIVE_MIN] = loadAvgs[1].toDouble()
                result[Key.FIFTEEN_MIN] = loadAvgs[2].toDouble()
                return result
            }
        }
        return result
    }
}