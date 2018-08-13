package io.pivotal.gemfire.micrometer.procOS

import java.io.IOException
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Locale

class ProcOSReader internal constructor(private val base: Path, private val entry: String, private val forceOSSupport: Boolean) {

    private val osSupport = forceOSSupport || System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("linux")

    data class ReadResult(val lines: List<String>, val readTime: Long)

    @Throws(IOException::class, InvalidPathException::class)
    fun read(): ReadResult = ReadResult(readPath(base.resolve(entry)), System.currentTimeMillis())

    @Throws(IOException::class)
    private fun readPath(path: Path): List<String> {
        return if (!osSupport) {
            emptyList()
        } else Files.readAllLines(path)
    }
}

class ProcOSReaderFactory {
    companion object {
        private val BASE_PATH = Paths.get("/proc")
    }

    private val instances = HashMap<String, ProcOSReader>()

    fun getInstance(entry: String): ProcOSReader =
            instances[entry] ?: run {
                instances[entry] = ProcOSReader(BASE_PATH, entry, false)
                return instances[entry]!!
            }
}