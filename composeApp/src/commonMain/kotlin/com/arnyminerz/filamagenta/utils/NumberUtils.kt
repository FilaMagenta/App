package com.arnyminerz.filamagenta.utils

import kotlin.math.round
import kotlin.random.Random

private const val ApproxTwoPlaces = 100.0

val Double.euros: String
    get() {
        // Round to two digits
        val rounded = round(this * ApproxTwoPlaces) / ApproxTwoPlaces
        val str = rounded.toString()
        val split = str.split('.')
        return split[0] + '.' + split[1].padEnd(2, '0') + " €"
    }

fun randomFloat(min: Float, max: Float): Float {
    require(min < max) { "max must be greater than min" }
    return min + (max - min) * Random.nextFloat()
}
