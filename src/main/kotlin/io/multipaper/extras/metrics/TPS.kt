package io.multipaper.extras.metrics

import dev.cubxity.plugins.metrics.api.metric.collector.Collector
import dev.cubxity.plugins.metrics.api.metric.collector.CollectorCollection
import dev.cubxity.plugins.metrics.api.metric.data.GaugeMetric
import dev.cubxity.plugins.metrics.api.metric.data.Metric
import me.lucko.spark.api.SparkProvider
import me.lucko.spark.api.statistic.StatisticWindow

class TPS : CollectorCollection {
    override val collectors = listOf(TPSCollector())

    class TPSCollector : Collector {
        private val spark = SparkProvider.get()

        override fun collect(): List<Metric> {
            val tps = spark.tps()?.poll(StatisticWindow.TicksPerSecond.SECONDS_5) ?: 0.0
            // Cap TPS at 20 (ignore catch-up ticks)
            return listOf(GaugeMetric("mc_tps", emptyMap(), if (tps > 20.0) 20.0 else tps))
        }
    }
}
