package com.wanotube.wanotubeapp.util

import java.util.Formatter

private val PUNCTUATION = listOf(", ", "; ", ": ", " ")

/**
 * Truncate long text with a preference for word boundaries and without trailing punctuation.
 */
fun String.smartTruncate(length: Int): String {
    val words = split(" ")
    var added = 0
    var hasMore = false
    val builder = StringBuilder()
    for (word in words) {
        if (builder.length > length) {
            hasMore = true
            break
        }
        builder.append(word)
        builder.append(" ")
        added += 1
    }

    PUNCTUATION.map {
        if (builder.endsWith(it)) {
            builder.replace(builder.length - it.length, builder.length, "")
        }
    }

    if (hasMore) {
        builder.append("...")
    }
    return builder.toString()
}

fun getThumbnailYoutubeVideo(url: String): String {
    val id = url.split("=")[1]
    return "https://img.youtube.com/vi/$id/0.jpg"
}

fun stringForTime(timeMs: Float): String {
    val totalSeconds = (timeMs / 1000).toInt()
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    val hours = totalSeconds / 3600
    val mFormatter = Formatter()
    return if (hours > 0) {
        mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
    } else {
        mFormatter.format("%02d:%02d", minutes, seconds).toString()
    }
}