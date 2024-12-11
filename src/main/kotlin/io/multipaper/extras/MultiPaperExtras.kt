package io.multipaper.extras

import co.aikar.commands.PaperCommandManager
import dev.cubxity.plugins.metrics.api.UnifiedMetricsProvider
import io.multipaper.extras.command.MPChunk
import io.multipaper.extras.metrics.Chunk
import io.multipaper.extras.metrics.Player
import io.multipaper.extras.metrics.TPS
import org.bukkit.plugin.java.JavaPlugin


class MultiPaperExtras : JavaPlugin() {

    override fun onEnable() {
        dataFolder.mkdirs()
        TickTimeCollector(this)
        UnifiedMetricsProvider.get().metricsManager.registerCollection(TPS())
        if (isMultiPaper()) {
            val manager = PaperCommandManager(this)
            manager.registerCommand(MPChunk())
            UnifiedMetricsProvider.get().metricsManager.registerCollection(Player(this))
            UnifiedMetricsProvider.get().metricsManager.registerCollection(Chunk())
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun isFolia(): Boolean {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer")
            return true
        } catch (e: ClassNotFoundException) {
            return false
        }
    }

    private fun isMultiPaper(): Boolean {
        try {
            Player::class.java.getMethod("isExternalPlayer")
            return true
        } catch (e: NoSuchMethodException) {
            return false
        } catch (e: NoSuchMethodError) {
            return false
        }
    }
}
