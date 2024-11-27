package io.multipaper.extras.metrics

import dev.cubxity.plugins.metrics.api.metric.collector.Collector
import dev.cubxity.plugins.metrics.api.metric.collector.CollectorCollection
import dev.cubxity.plugins.metrics.api.metric.data.GaugeMetric
import dev.cubxity.plugins.metrics.api.metric.data.Metric
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class PlayersOnlineLocal : CollectorCollection {
    override val collectors = listOf(PlayersOnlineLocalCollector())

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
}
