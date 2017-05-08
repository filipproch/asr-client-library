package cz.zcu.kky.asrclientlibrary.util

import java.lang.AssertionError
import java.lang.StringBuilder

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
object RandomKey {

    private val PUSH_CHARS = "-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz"

    // Timestamp of last push, used to prevent local collisions if you push twice in one ms.
    private var lastPushTime = -1L

    // We generate 72-bits of randomness which get turned into 12 characters and appended to the
    // timestamp to prevent collisions with other clients.  We store the last characters we
    // generated because in the event of a collision, we'll use those same characters except
    // "incremented" by one.
    private val cachedLastRandChars = IntArray(12)

    fun generate(): String {
        var now = System.currentTimeMillis()
        val duplicateTime = (now == lastPushTime)
        lastPushTime = now

        // Generate the first 8 characters
        val timeStampChars = CharArray(8)
        (7 downTo 0).forEach {
            val module = now % 64L
            now /= 64L
            timeStampChars[it] = PUSH_CHARS[module.toInt()]
        }
        if (now != 0L) {
            throw AssertionError("We should have converted the entire timestamp.")
        }

        // Generate the last 12 characters
        if (!duplicateTime) {
            cachedLastRandChars.forEachIndexed { i, _ -> cachedLastRandChars[i] = (Math.random() * 64.0).toInt() }
        } else {
            // If the timestamp hasn't changed since last push, use the same random number, except incremented by 1.
            val lastNot63 = cachedLastRandChars.indexOfLast { it != 63 }
            cachedLastRandChars.fill(0, lastNot63 + 1)
            cachedLastRandChars[lastNot63]++
        }
        val lastRandChars = cachedLastRandChars.fold(StringBuilder(12), { str, i -> str.append(PUSH_CHARS[i]) })

        // Join both characters lists
        val id = String(timeStampChars) + lastRandChars
        if (id.length != 20) {
            throw AssertionError("Length should be 20.")
        }
        return id
    }

}