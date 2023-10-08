package com.arnyminerz.filamagenta.account

/**
 * Provides an string that contains an index inside of it. Allows extracting the defaults, and
 * calling a simple function to return the string with the index replaced.
 *
 * @param template The string to use. Replace `%d` with the index to replace. For example `value_%d`
 * being called with index `1` will return `value_1`.
 */
data class StringIndex(
    val template: String
) {
    /**
     * Returns the [template] with its index replaced by [index].
     *
     * TODO: Currently uses the [String.replace] function. should be using format.
     */
    operator fun invoke(index: Int): String = template.replace("%d", index.toString())
}
