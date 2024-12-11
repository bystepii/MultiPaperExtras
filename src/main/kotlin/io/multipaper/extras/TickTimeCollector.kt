package io.multipaper.extras

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.io.BufferedWriter
import java.io.FileWriter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TickTimeCollector(val plugin: Plugin) {
    private val SAMPLING_INTERVAL = 2500 // 2.5 seconds
    private val executor = Executors.newScheduledThreadPool(1)
    private val fileWriter = BufferedWriter(FileWriter(plugin.dataFolder.path + "/tick_times.csv"))

    init {
        fileWriter.write("timestamp,tick_time\n")
        executor.scheduleAtFixedRate(TickTimeRunnable(), 0, SAMPLING_INTERVAL.toLong(), TimeUnit.MILLISECONDS)
    }

    inner class TickTimeRunnable : Runnable {
        private var prev: LongArray? = null
        override fun run() {
            val startTime = System.currentTimeMillis()
            val tickTimes = getTickTimes()
            if (tickTimes.size != 100) {
                plugin.logger.warning("Tick times array has size ${tickTimes.size} instead of 100")
                return
            }
            if (prev != null) {
                val newTickTimes = findNew(prev!!, tickTimes)
                if (newTickTimes.size != 1) {
                    for (x in newTickTimes.indices) {
                        val toWrite = (startTime + 50L * x.toLong()).toString() + "," + newTickTimes[x] + "\n"
                        fileWriter.write(toWrite)
                    }
                    fileWriter.flush()
                }
            }
            prev = tickTimes
        }
    }

    private fun getTickTimes(): LongArray {
        return Bukkit.getServer().tickTimes
    }

    fun findNew(prev: LongArray, curr: LongArray): LongArray {
        val diff: LongArray = listDiff(prev, curr)
        var farthest = -1

        for (i in diff.indices) {
            if (diff[i] == 0L) {
                if (i + 1 == diff.size) {
                    if (diff[0] != 0L) {
                        farthest = 0
                    }
                } else if (diff[i + 1] != 0L) {
                    farthest = i + 1
                }
            }
        }

        if (farthest == -1) {
            plugin.logger.warning("No change in tickTime array: ")
            plugin.logger.warning("prev: " + printArray(prev))
            plugin.logger.warning("curr: " + printArray(curr))
            return LongArray(1)
        } else {
            return getNonZero(diff, farthest, 50)
        }
    }

    private fun listDiff(prev: LongArray, curr: LongArray): LongArray {
        val diff = curr.clone()

        for (i in prev.indices) {
            if (curr[i] - prev[i] == 0L) {
                diff[i] = 0L
            }
        }

        return diff
    }

    private fun getNonZero(array: LongArray, start: Int, max: Int): LongArray {
        val temp = LongArray(max)
        Arrays.fill(temp, -1L)
        var j = 0

        for (i in 0..<max) {
            j = start + i
            if (j >= array.size) {
                j = start + i - array.size
            }

            if (array[j] != 0L) {
                temp[i] = array[j]
            }
        }

        return temp
    }

    private fun printArray(array: LongArray): String {
        val sb = StringBuilder()
        sb.append("[ ")
        for (i in array.indices) {
            sb.append(array[i])
            if (i != array.size - 1) {
                sb.append(", ")
            }
        }
        sb.append(" ]")
        return sb.toString()
    }
}