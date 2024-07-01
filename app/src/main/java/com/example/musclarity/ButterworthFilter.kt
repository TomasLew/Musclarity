package com.example.musclarity

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class ButterworthFilter(private val sampleRate: Double) {
    private lateinit var a: DoubleArray
    private lateinit var b: DoubleArray
    private lateinit var z: DoubleArray

    fun bandPass(order: Int, lowCutoff: Double, highCutoff: Double) {
        val nyquist = 0.5 * sampleRate
        val low = lowCutoff / nyquist
        val high = highCutoff / nyquist
        val n = 2 * order

        val ar = DoubleArray(n + 1)
        val ai = DoubleArray(n + 1)
        val br = DoubleArray(n + 1)
        val bi = DoubleArray(n + 1)

        for (i in 0..order) {
            val angle = PI * (i + 0.5) / n
            val real = cos(angle)
            val imag = sin(angle)
            ar[i] = real
            ai[i] = imag
            br[i] = 0.0
            bi[i] = 0.0
        }

        b = br
        a = ar
        z = DoubleArray(order)
    }

    fun filter(input: DoubleArray): DoubleArray {
        val output = DoubleArray(input.size)
        for (i in input.indices) {
            output[i] = b[0] * input[i] + z[0]
            for (j in 1 until z.size) {
                z[j - 1] = b[j] * input[i] + z[j] - a[j] * output[i]
            }
            z[z.size - 1] = b[z.size] * input[i] - a[z.size] * output[i]
        }
        return output
    }
}