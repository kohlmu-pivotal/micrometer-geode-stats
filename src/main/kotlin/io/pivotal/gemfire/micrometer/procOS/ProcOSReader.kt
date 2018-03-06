/*
 * Copyright © 2016 Michael Weirauch (michael.weirauch@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    private val instances = HashMap<String, ProcOSReader>()
    private val instancesLock = Any()
    private val BASE = Paths.get("/proc")

    fun getInstance(entry: String): ProcOSReader {
        synchronized(instancesLock) {
            instances[entry]?.let { return it }
                    ?: run {
                        instances[entry] = ProcOSReader(BASE, entry, false)
                        return instances[entry]!!
                    }
        }
    }
}