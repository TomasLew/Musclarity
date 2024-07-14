package com.example.musclarity

import android.util.Log
import org.apache.commons.math3.stat.regression.SimpleRegression
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType
import kotlin.math.pow


class SignalProcessingUtils {
    fun process(signal: DoubleArray, intervals: Int, overlap: Double, sampleRate: Double, low: Double, high: Double): DoubleArray {
        //val butterworth = ButterworthFilter(sampleRate)
        //butterworth.bandPass(4, low, high)
        //val filtered = butterworth.filter(signal)
        val medians = DoubleArray(intervals)
        val windowSize = signal.size / intervals

        for (i in 0 until intervals) {
            val startIndex = (i * windowSize - (if (i != 0) (windowSize * overlap).toInt() else 0)).coerceAtLeast(0)
            val endIndex = (startIndex + windowSize).coerceAtMost(signal.size)
            val segment = signal.copyOfRange(startIndex, endIndex)
            //Log.d("Window size", "Window size: ${segment.size}")
            medians[i] = welch(segment, sampleRate)
        }

        return medians
    }

    private fun welch(segment: DoubleArray, sampleRate: Double): Double {
        // Crear una instancia de la FFT
        val fft = FastFourierTransformer(DftNormalization.STANDARD)

        // Calcular la FFT del segmento
        val spectrum = fft.transform(segment, TransformType.FORWARD)

        // Calcular el espectro de potencia (magnitud al cuadrado de los coeficientes FFT)
        val powerSpectrum = spectrum.map { it.abs().pow(2.0) }.toDoubleArray()

        // Crear un array de frecuencias
        val frequencies = DoubleArray(powerSpectrum.size) { it * sampleRate / powerSpectrum.size }

        // Calcular la potencia total
        val totalPower = powerSpectrum.sum()
        //Log.d("Power", "Total power: ${totalPower}")

        // Calcular la potencia acumulada y encontrar el índice de la frecuencia media
        var cumulativePower = 0.0
        var medianIndex = 0
        for (i in powerSpectrum.indices) {
            cumulativePower += powerSpectrum[i]
            //Log.d("Cum. Power", "Cumulative power: ${cumulativePower}")
            if (cumulativePower > totalPower / 2) {
                medianIndex = i
                break
            }
            Log.d("Median Frequency", "Median freqr: ${frequencies[medianIndex]*100}")
        }

        // Devolver la frecuencia correspondiente al índice de la frecuencia media
        return frequencies[medianIndex]*100f
    }

    fun percFatigue(medFrec: Double, maxFrec: Float?, thresholdMax: Double = 0.5, transitionFatigue: Double = 0.7): Double {
        val topFatigueDif = thresholdMax * maxFrec!!
        val result = minOf(1.0, 1.0 - (maxFrec - medFrec) / topFatigueDif)
        val result2 = maxOf(result, 0.toDouble())
        return result2*100f
        //return (1-topFatigueDif)*100f
    }

    fun regression(medians: DoubleArray, requiredWindows: Int = 5, maxSlope: Double = 1.0, percDrop: Double = 0.1): Double {
        val regression = SimpleRegression()
        val time = DoubleArray(medians.size) { it.toDouble() }
        for (i in medians.indices) {
            regression.addData(time[i], medians[i])
        }
        val slope = regression.slope
        if (slope <= maxSlope * (1 - percDrop)) {
            println("FATIGUE!")
        }
        return slope
    }
    fun nextPowerOfTwo(n: Int): Int {
        var p = 1
        while (p < n) {
            p *= 2
        }
        return p
    }
}