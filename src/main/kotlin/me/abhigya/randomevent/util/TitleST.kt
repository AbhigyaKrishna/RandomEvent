package me.abhigya.randomevent.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import net.kyori.adventure.util.Ticks
import org.apache.commons.lang.math.NumberUtils
import org.bukkit.entity.Player
import java.util.regex.Matcher
import java.util.regex.Pattern

class TitleST(
    title: Component,
    subTitle: Component,
    fadeIn: Long,
    stay: Long,
    fadeOut: Long
) : Cloneable {
    private var title: Component
    private var subTitle: Component
    var fadeIn: Long
    var stay: Long
    var fadeOut: Long

    init {
        this.title = title
        this.subTitle = subTitle
        this.fadeIn = fadeIn
        this.stay = stay
        this.fadeOut = fadeOut
    }

    override fun toString(): String {
        return MiniMessage.miniMessage().serialize(title) + "\n" + MiniMessage.miniMessage().serialize(subTitle) + "::" + fadeIn + "::" + stay + "::" + fadeOut
    }

    fun getTitle(): Component {
        return title
    }

    fun setTitle(title: Component) {
        this.title = title
    }

    fun getSubTitle(): Component {
        return subTitle
    }

    fun setSubTitle(subTitle: Component) {
        this.subTitle = subTitle
    }

    fun send(member: Player) {
        member.showTitle(Title.title(
            title,
            subTitle,
            Title.Times.times(
                Ticks.duration(fadeIn),
                Ticks.duration(stay),
                Ticks.duration(fadeOut)
            )
        ))
    }

    public override fun clone(): TitleST {
        return TitleST(title, subTitle, fadeIn, stay, fadeOut)
    }

    companion object {
        const val DEFAULT_FADE_IN = 10L
        const val DEFAULT_STAY = 70L
        const val DEFAULT_FADE_OUT = 20L
        private val PATTERN: Pattern = Pattern.compile(
            "^(?<title>.+?(?=::|$))(?:::[+-]?(?<fadein>\\d*\\.?\\d*)::[+-]?(?<stay>\\d*\\.?\\d*)::[+-]?(?<fadeout>\\d*\\.?\\d*))?$",
            Pattern.CASE_INSENSITIVE
        )
        private val MESSAGE_PATTERN: Pattern =
            Pattern.compile("^(?<title>.+?(?=\\\\n|$))(?:\\\\n(?<subtitle>.*))?$", Pattern.CASE_INSENSITIVE)

        fun of(title: Component): TitleST {
            return of(title, Component.empty())
        }

        fun of(title: Component, subtitle: Component): TitleST {
            return of(title, subtitle, DEFAULT_FADE_IN, DEFAULT_STAY, DEFAULT_FADE_OUT)
        }

        fun of(title: Component, subTitle: Component, fadeIn: Long, stay: Long, fadeOut: Long): TitleST {
            return TitleST(title, subTitle, fadeIn, stay, fadeOut)
        }

        fun valueOf(str: String): TitleST? {
            val matcher: Matcher = PATTERN.matcher(str)
            if (matcher.matches()) {
                var title: String = matcher.group("title")
                var subTitle: String? = null
                val msgMatcher: Matcher = MESSAGE_PATTERN.matcher(title)
                if (msgMatcher.matches()) {
                    title = msgMatcher.group("title")
                    subTitle = msgMatcher.group("subtitle")
                }
                return if (matcher.groupCount() > 1) {
                    of(
                        MiniMessage.miniMessage().deserialize(title),
                        MiniMessage.miniMessage().deserialize(subTitle ?: ""),
                        NumberUtils.toDouble(matcher.group("fadein"), DEFAULT_FADE_IN.toDouble()).toLong(),
                        NumberUtils.toDouble(matcher.group("stay"), DEFAULT_STAY.toDouble()).toLong(),
                        NumberUtils.toDouble(matcher.group("fadeout"), DEFAULT_FADE_OUT.toDouble()).toLong()
                    )
                } else {
                    of(
                        MiniMessage.miniMessage().deserialize(title),
                        MiniMessage.miniMessage().deserialize(subTitle ?: "")
                    )
                }
            }
            return null
        }
    }
}