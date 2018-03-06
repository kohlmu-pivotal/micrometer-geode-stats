
package io.pivotal.gemfire.micrometer.procOS

import org.slf4j.LoggerFactory

abstract class ProcOSEntry protected constructor(private val reader: ProcOSReader) {

    private val log = LoggerFactory.getLogger(ProcOSEntry::class.java)
    private var lastResult: ProcOSReader.ReadResult = ProcOSReader.ReadResult(emptyList(), -1L)
    private val REFRESH_TIMEOUT_MILLIS = 2000L
    private val dataLock = Any()

    interface ValueKey

    operator fun get(key: ValueKey): Double {
        val collect = collect()
        val returnValue = collect.getOrDefault(key, (-1).toDouble())
        return returnValue
    }
    private fun collect(): Map<ValueKey, Double> {
        synchronized(dataLock) {
            if (lastResult.readTime + REFRESH_TIMEOUT_MILLIS < System.currentTimeMillis()) {
                var result = reader.read()
                if (lastResult.readTime != result.readTime) {
                    lastResult = result
                }
            }
            return handle(lastResult.lines)
        }
    }

    protected abstract fun handle(lines: Collection<String>): Map<ValueKey, Double>

}
