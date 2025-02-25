package io.multipaper.extras.metrics

import dev.cubxity.plugins.metrics.api.metric.collector.Collector
import dev.cubxity.plugins.metrics.api.metric.collector.CollectorCollection
import dev.cubxity.plugins.metrics.api.metric.data.GaugeMetric
import dev.cubxity.plugins.metrics.api.metric.data.Metric
import me.lucko.spark.api.SparkProvider
import me.lucko.spark.api.statistic.StatisticWindow
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo
import java.util.*
import kotlin.math.min

class TPS : CollectorCollection {
    override val collectors = listOf(TPSCollector())

    class TPSCollector : Collector {
        private val spark = SparkProvider.get()

        override fun collect(): List<Metric> {
            val tps = min(spark.tps()?.poll(StatisticWindow.TicksPerSecond.SECONDS_5) ?: 0.0, 20.0)
            val tpsMetrics = StatisticWindow.TicksPerSecond.entries.map {
                it.name to min(spark.tps()?.poll(it) ?: 0.0, 20.0)
            }.map {
                GaugeMetric("mc_tps_" + it.first.lowercase(Locale.getDefault()), emptyMap(), it.second)
            }

            val mspt = spark.mspt()?.poll(StatisticWindow.MillisPerTick.SECONDS_10) ?: 0.0
            val msptMetrics = StatisticWindow.MillisPerTick.entries.map {
                it.name to spark.mspt()?.poll(it)
            }.map {
                DoubleAverageInfo::class.java.declaredMethods.filter { it.parameterCount == 0 }.map { method ->
                    val name = method.name
                    GaugeMetric("mc_mspt_"
                            + it.first.lowercase(Locale.getDefault()) + "_"
                            + name.lowercase(Locale.getDefault()), emptyMap(), method.invoke(it.second) as Double)
                }
            }.flatten()

            return listOf(GaugeMetric("mc_tps", emptyMap(), tps)) + tpsMetrics + msptMetrics
        }
    }
}
