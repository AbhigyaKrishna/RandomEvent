package me.abhigya.randomevent

import me.abhigya.randomevent.command.SetArenaCentreCommand
import me.abhigya.randomevent.command.SetSubmitLocationCommand
import me.abhigya.randomevent.util.Util
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class RandomEvent : JavaPlugin() {

    companion object {
        lateinit var instance: RandomEvent
    }

    var config: YamlConfiguration? = null
    var arenaLocation: Location? = null
    var submitLocation: Location? = null
    val chaseSequence = ChaseSequence(this)

    override fun onEnable() {
        instance = this
        server.pluginManager.registerEvents(SomeListener(this), this)

        val recipe = FurnaceRecipe(
            NamespacedKey(this, "diamond_recipe"), ItemStack(Material.DIAMOND).apply {
                Util.setCustomDiamond(this)
            }, RecipeChoice.MaterialChoice(
                Material.DIAMOND_AXE,
                Material.DIAMOND_HOE,
                Material.DIAMOND_SWORD,
                Material.DIAMOND_PICKAXE,
                Material.DIAMOND_SHOVEL,
                Material.DIAMOND_HELMET,
                Material.DIAMOND_CHESTPLATE,
                Material.DIAMOND_LEGGINGS,
                Material.DIAMOND_BOOTS
            ),
            0f, 120
        )

        server.addRecipe(recipe)

        reloadConfig0()

        getCommand("setarena")!!.setExecutor(SetArenaCentreCommand(this))
        getCommand("setsubmit")!!.setExecutor(SetSubmitLocationCommand(this))

    }

    fun reloadConfig0() {
        if (!dataFolder.isDirectory)
            dataFolder.mkdirs()

        val file = File(dataFolder, "config.yml")
        if (!file.exists()) {
            saveResource("config.yml", false)
        }

        config?.save(file)

        config = YamlConfiguration.loadConfiguration(file)

        arenaLocation = config!!.getLocation("arena-location")
        submitLocation = config!!.getLocation("submit-location")
    }

}