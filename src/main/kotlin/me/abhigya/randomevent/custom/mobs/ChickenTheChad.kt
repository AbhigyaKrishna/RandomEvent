package me.abhigya.randomevent.custom.mobs

import net.minecraft.network.chat.TextComponent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.Chicken
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld


class ChickenTheChad(loc: Location) : Chicken(EntityType.CHICKEN, (loc.world as CraftWorld).handle) {
    init {
        this.setPos(loc.x, loc.y, loc.z)
        this.customName = TextComponent("Chicken The Chad")
        this.isCustomNameVisible = true
    }
}