package io.pivotal.gemfire

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.composite.CompositeMeterRegistry
import io.micrometer.influx.InfluxConfig
import io.micrometer.influx.InfluxMeterRegistry
import io.micrometer.jmx.JmxConfig
import io.micrometer.jmx.JmxMeterRegistry
import java.time.Duration

class MicrometerStats {
    val meterRegistry =
            CompositeMeterRegistry(Clock.SYSTEM)
//    private val influxMetrics: MeterRegistry = InfluxMeterRegistry(object : InfluxConfig {
//        override fun step(): Duration = Duration.ofSeconds(10)
//        override fun db(): String = "mydb"
//        override fun get(k: String): String? = null
//        override fun uri(): String = "http://10.118.33.32:8086"
//    }, Clock.SYSTEM)

    private val jmxMetrics: MeterRegistry = JmxMeterRegistry(JmxConfig.DEFAULT, Clock.SYSTEM)

    init {
//        meterRegistry.add(influxMetrics)
        meterRegistry.add(jmxMetrics)
    }
}