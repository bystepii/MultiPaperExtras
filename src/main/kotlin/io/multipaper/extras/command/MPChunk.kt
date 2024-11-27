package io.multipaper.extras.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


@CommandAlias("mpchunk")
@Description("Commands for chunk management")
class MPChunk : BaseCommand() {

    @Subcommand("isLocal")
    @Syntax("[x z] [world]")
    fun onIsLocal(sender: CommandSender, args: Array<String>) {
        handleChunkCheck(sender, args, "local") { chunk ->
            chunk.isLocalChunk
        }
    }

    @Subcommand("isExternal")
    @Syntax("[x z] [world]")
    fun onIsExternal(sender: CommandSender, args: Array<String>) {
        handleChunkCheck(sender, args, "external") { chunk ->
            chunk.isExternalChunk
        }
    }

    private fun handleChunkCheck(
        sender: CommandSender,
        args: Array<String>,
        type: String,
        checkChunk: (org.bukkit.Chunk) -> Boolean
    ) {
        if (args.isEmpty()) {
            if (sender is Player) {
                val chunk = sender.location.chunk
                val result = checkChunk(chunk)
                sender.sendMessage("Chunk ${chunk.x}, ${chunk.z} in world ${chunk.world.name} is $type: $result")
            } else {
                sender.sendMessage("You must be a player to run this command without arguments.")
            }
        } else if (args.size >= 2) {
            val x = args[0].toIntOrNull()
            val z = args[1].toIntOrNull()

            if (x == null || z == null) {
                sender.sendMessage("Invalid chunk coordinates. They must be integers.")
                return
            }

            val worldName = args.getOrNull(2)
            val world = if (worldName != null) Bukkit.getWorld(worldName) else Bukkit.getWorlds().firstOrNull()

            if (world != null) {
                val chunk = world.getChunkAt(x, z)
                val result = checkChunk(chunk)
                sender.sendMessage("Chunk $x, $z in world ${world.name} is $type: $result")
            } else {
                sender.sendMessage("World not found${if (worldName != null) ": $worldName" else ""}.")
            }
        } else {
            sender.sendMessage("Invalid arguments. Syntax: [x z] [world].")
        }
    }
}
