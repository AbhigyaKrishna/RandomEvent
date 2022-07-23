package me.abhigya.randomevent.util

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection

class LocationSerializer(
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float
) {

    constructor(location: Location) : this(
        location.world.name,
        location.x,
        location.y,
        location.z,
        location.yaw,
        location.pitch
    )

    constructor(section: ConfigurationSection) : this(
        section.getString("world")!!,
        section.getDouble("x"),
        section.getDouble("y"),
        section.getDouble("z"),
        section.getDouble("yaw").toFloat(),
        section.getDouble("pitch").toFloat()
    )

    constructor(section: ConfigurationSection, key: String) : this(
        section.getConfigurationSection(key)!!
    )

    fun toLocation(): Location {
        return Location(
            Bukkit.getWorld(world),
            x,
            y,
            z,
            yaw,
            pitch
        )
    }

    fun save(section: ConfigurationSection) {
        section.set("world", world)
        section.set("x", x)
        section.set("y", y)
        section.set("z", z)
        section.set("yaw", yaw)
        section.set("pitch", pitch)
    }

    fun save(section: ConfigurationSection, key: String) {
        val sub = section.getConfigurationSection(key) ?: section.createSection(key)
        save(sub)
    }

}