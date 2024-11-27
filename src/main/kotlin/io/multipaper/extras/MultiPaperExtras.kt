package io.multipaper.extras

import co.aikar.commands.PaperCommandManager
import dev.cubxity.plugins.metrics.api.UnifiedMetricsProvider
import io.multipaper.extras.command.MPChunk
import io.multipaper.extras.metrics.PlayersOnlineLocal
import io.multipaper.extras.metrics.TPS
import org.bukkit.plugin.java.JavaPlugin


class MultiPaperExtras : JavaPlugin() {

    override fun onEnable() {
        val manager = PaperCommandManager(this)
        manager.registerCommand(MPChunk())
        UnifiedMetricsProvider.get().metricsManager.registerCollection(TPS())
        UnifiedMetricsProvider.get().metricsManager.registerCollection(PlayersOnlineLocal())
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
