package me.abhigya.randomevent

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin

class RandomEvent : JavaPlugin() {

    override fun onEnable() {
        server.pluginManager.registerEvents(SomeListener(), this)

        server.addRecipe(ShapedRecipe(NamespacedKey.minecraft("sexy_diamond"), ItemStack(Material.DIAMOND))
            .shape("ABX", "BXB", "XBA")
            .setIngredient('A', Material.GLASS)
            .setIngredient('B', Material.BLUE_DYE))
    }

}