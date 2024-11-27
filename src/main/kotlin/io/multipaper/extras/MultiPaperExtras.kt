package io.multipaper.extras

import co.aikar.commands.PaperCommandManager
import io.multipaper.extras.command.MPChunk
import org.bukkit.plugin.java.JavaPlugin


class MultiPaperExtras : JavaPlugin() {

    override fun onEnable() {
        val manager = PaperCommandManager(this)
        manager.registerCommand(MPChunk())
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
