package io.multipaper.extras.metrics

import dev.cubxity.plugins.metrics.api.metric.collector.Collector
import dev.cubxity.plugins.metrics.api.metric.collector.CollectorCollection
import dev.cubxity.plugins.metrics.api.metric.data.GaugeMetric
import dev.cubxity.plugins.metrics.api.metric.data.Metric
import org.bukkit.Bukkit

class Chunk : CollectorCollection {
    override val collectors = listOf(ChunkOwnershipCollector())

    class ChunkOwnershipCollector : Collector {
        private val serverName = Bukkit.getLocalServerName()
        override fun collect(): List<Metric> {
            val metric = mutableListOf<Metric>()
            for (world in Bukkit.getWorlds())
                for (chunk in world.loadedChunks) {
                    if (chunk.isExternalChunk) continue
                    if (!chunk.isLocalChunk) continue
                    metric.add(GaugeMetric("mc_chunk_ownership", mapOf(
                        "world" to world.name,
                        "chunk_x" to "${chunk.x}",
                        "chunk_z" to "${chunk.z}",
                        "owner" to serverName
                    ), 1))
                }
            return metric
        }
    }
}