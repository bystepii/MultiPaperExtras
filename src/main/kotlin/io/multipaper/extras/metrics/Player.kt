package io.multipaper.extras.metrics

import dev.cubxity.plugins.metrics.api.metric.collector.Collector
import dev.cubxity.plugins.metrics.api.metric.collector.CollectorCollection
import dev.cubxity.plugins.metrics.api.metric.data.GaugeMetric
import dev.cubxity.plugins.metrics.api.metric.data.Metric
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class Player(plugin: Plugin) : CollectorCollection {
    override val collectors = listOf(PlayersOnlineLocalCollector(), PlayerLocationCollector())
    private val logger = plugin.logger

    class PlayersOnlineLocalCollector : Collector {
        override fun collect(): List<Metric> {
            val metric = mutableListOf<Metric>()
            for (world in Bukkit.getWorlds()) {
                val players = world.players.stream().filter(Player::isLocalPlayer).count()
                metric.add(GaugeMetric("mc_players_online_local", mapOf("world" to world.name), players))
            }
            return metric
        }
    }

    inner class PlayerLocationCollector : Collector {
        private val serverName = Bukkit.getLocalServerName()
        override fun collect(): List<Metric> {
            val metric = mutableListOf<Metric>()
            for (player in Bukkit.getLocalOnlinePlayers()) {
                val chunk = player.location.chunk
                val chunkOwner = if (chunk.isExternalChunk) chunk.externalServerName
                else if (chunk.isLocalChunk) serverName
                else if (!chunk.isLoaded) {
                    logger.warning("Chunk is not loaded: $chunk")
                    "unloaded"
                }
                else {
                    logger.warning("Chunk is not local or external: $chunk")
                    "unknown"
                }
                metric.add(GaugeMetric("mc_player_location", mapOf(
                    "name" to player.name,
                    "world" to player.world.name,
                    "chunk_x" to "${chunk.x}",
                    "chunk_z" to "${chunk.z}",
                    "chunk_owner" to chunkOwner
                ), 1))
            }
            return metric
        }
    }
}
