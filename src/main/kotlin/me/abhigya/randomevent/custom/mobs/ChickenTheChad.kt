package me.abhigya.randomevent.custom.mobs

import net.minecraft.network.chat.TextComponent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.Chicken
import net.minecraft.world.entity.player.Player
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld


class ChickenTheChad(loc: Location) : Chicken(EntityType.CHICKEN, (loc.world as CraftWorld).handle) {
    init {
        this.setPos(loc.x, loc.y, loc.z)
        this.customName = TextComponent("Chicken The Chad")
        this.isCustomNameVisible = true
    }

    override fun registerGoals() {
        targetSelector.addGoal(0, NearestAttackableTargetGoal(this, Player::class.java, true))
        targetSelector.addGoal(1, HurtByTargetGoal(this))
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.00, true))
        goalSelector.addGoal(5, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(6, LookAtPlayerGoal(this, Player::class.java, 6.0F))
        goalSelector.addGoal(7, RandomLookAroundGoal(this))
    }
}