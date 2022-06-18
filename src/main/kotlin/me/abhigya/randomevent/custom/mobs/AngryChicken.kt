package me.abhigya.randomevent.custom.mobs

import net.minecraft.server.level.WorldServer
import net.minecraft.world.entity.EntityAgeable
import net.minecraft.world.entity.animal.EntityChicken
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld


class AngryChicken(loc: Location) : EntityChicken(net.minecraft.world.entity.EntityTypes.l, (loc.world as CraftWorld).handle) {
    override fun a(p0: WorldServer?, p1: EntityAgeable?): EntityAgeable? {
        TODO("Not yet implemented")
    }
    init {
        this.g(loc.x, loc.y, loc.z)
    }
}

