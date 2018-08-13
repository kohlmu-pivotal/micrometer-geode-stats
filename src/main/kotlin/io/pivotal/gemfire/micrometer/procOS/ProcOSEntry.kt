package io.pivotal.gemfire.micrometer.procOS

import org.slf4j.LoggerFactory

abstract class ProcOSEntry protected constructor(private val reader: ProcOSReader) {
    companion object {
        private const val REFRESH_TIMEOUT_MILLIS = 2000L
    }

    protected val log = LoggerFactory.getLogger(ProcOSEntry::class.java)!!
    private var lastResult: ProcOSReader.ReadResult = ProcOSReader.ReadResult(emptyList(), -1L)
    private val dataLock = Any()
    private var dataResult = emptyMap<ValueKey, Double>()

    interface ValueKey

    operator fun get(key: ValueKey): Double {
        if (lastResult.readTime + REFRESH_TIMEOUT_MILLIS < System.currentTimeMillis()) {
            refresh()
            dataResult = collect(lastResult)
        }
        return dataResult.getOrDefault(key, (-1).toDouble())
    }

    private fun collect(result: ProcOSReader.ReadResult): Map<ValueKey, Double> {
        return handle(result.lines)
    }

    private fun refresh() {
        synchronized(dataLock) {
            var result = reader.read()
            if (lastResult.readTime != result.readTime) {
                lastResult = result
            }
        }
    }

    protected abstract fun handle(lines: Collection<String>): Map<ValueKey, Double>

}
